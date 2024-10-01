import {useMutation, useQuery, useQueryClient} from "react-query";
import {SubmitHandler, useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {useAxios} from "@config/axios.ts";
import {PermissionType, usersSchemaType, UserType} from "@/types/api/userType.ts";
import {useModal} from "@hooks/useModal.ts";
import {useAlerts} from "@context/modules/AlertContext.tsx";
import {useTranslation} from "react-i18next";
import {
    AdminUserCreateFormFields,
    adminUserCreateSchema,
    AdminUserEditFormFields,
    adminUserEditSchema
} from "@/types/form/adminFormType.ts";
import {useRoles} from "@hooks/useRoles.ts";

export function useUserApi(page: number) {
    const API = useAxios()

    const {data, isLoading} = useQuery(
        ['users', page],
        async () => {
            const response = await API.get('/admin/users?page=' + page)
            return usersSchemaType.parse(response.data)
        }
    );

    return {data, isLoading}
}

export function useAdminCreateUserApi() {
    const API = useAxios()
    const queryClient = useQueryClient()
    const {closeModal} = useModal()
    const {setAlerts} = useAlerts()
    const {t} = useTranslation()

    const form = useForm<AdminUserCreateFormFields>({
        resolver: zodResolver(adminUserCreateSchema),
    })

    const mutation = useMutation(
        async (data: AdminUserCreateFormFields) => {
            await API.post('/admin/users/create',
                {
                    ...data,
                    permissions: Array.isArray(data.permissions) ? data.permissions.map((p) => parseInt(p.value)) : []
                })
        },
        {
            onSuccess: () => {
                queryClient.invalidateQueries('users')
                setAlerts('success', t('alerts.success.user.create'))
                closeModal()
            }
        })

    const onSubmit: SubmitHandler<AdminUserCreateFormFields> = (data: AdminUserCreateFormFields) => {
        mutation.mutate(data)
    }

    return {form, onSubmit, isLoading: mutation.isLoading}
}

export function useAdminEditUserApi({user, permissions}: { user: UserType, permissions: PermissionType[] | undefined }) {
    const API = useAxios()
    const queryClient = useQueryClient()
    const {setAlerts} = useAlerts()
    const {closeModal} = useModal()
    const {getPermissionsValue} = useRoles()
    const {t} = useTranslation()

    const form = useForm<AdminUserEditFormFields>({
        resolver: zodResolver(adminUserEditSchema),
        defaultValues: {
            name: user.name,
            email: user.email,
            file_path: user.file_path || './',
            permissions: permissions ? getPermissionsValue(permissions, user.permissions) : undefined,
        }
    })

    const mutation = useMutation(
        async (formData: AdminUserEditFormFields) => {
            await API.post('/admin/users/update/' + user.id, {
                ...formData,
                permissions: Array.isArray(formData.permissions) ? formData.permissions.map((p) => parseInt(p.value)) : [],
            })
        }
        , {
            onSuccess: () => {
                queryClient.invalidateQueries('users')
                setAlerts('success', t('alerts.success.user.edit'))
                closeModal()
            },
        })

    const onSubmit: SubmitHandler<AdminUserEditFormFields> = (formData: AdminUserEditFormFields) => {
        mutation.mutate(formData)
    }


    return {form, onSubmit, isLoading: mutation.isLoading}
}

export function useAdminDeleteUserApi(userId: number) {
    const {closeModal} = useModal()
    const {setAlerts} = useAlerts()
    const API = useAxios()
    const queryClient = useQueryClient()
    const {t} = useTranslation()

    const {mutate} = useMutation(async () => {
        await API.delete('/admin/users/delete/' + userId)
    }, {
        onSuccess: () => {
            queryClient.invalidateQueries('users');
            setAlerts('success', t('alerts.success.user.delete'));
            closeModal();
        }
    });

    return {mutate}
}