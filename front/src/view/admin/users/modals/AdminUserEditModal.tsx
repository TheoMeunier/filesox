import {Controller} from "react-hook-form";
import {ModalBody, ModalFooter, ModalHeader} from "@components/modules/Modal.tsx";
import {FormError, FormField, FormFields, FormLabel} from "@components/modules/Form.tsx";
import {Button} from "@components/modules/Button.tsx";
import {Row} from "@components/modules/Grid.tsx";
import {PermissionType, UserType} from "@/types/api/userType.ts";
import {useTranslation} from "react-i18next";
import {UserPen} from "lucide-react";
import {Loader} from "@components/modules/Loader/Loader.tsx";
import {useAdminEditUserApi} from "@/api/admin/adminUserApi.ts";
import {useAdminPermissionsApi} from "@/api/admin/adminApi.ts";
import CustomMultiSelect from "@components/modules/Inputs/Select.tsx";

export function AdminEditUserModal({user}: { user: UserType }) {
    const {t} = useTranslation()

    const {isLoading, permissions} = useAdminPermissionsApi()
    console.log(user)

    if (isLoading) return <div><Loader/></div>;

    return <>
        <ModalHeader>
            <h2 className="flex items-center gap-2 text-2xl">
                <span className="text-indigo-500"><UserPen height={28} width={28}/></span>
                {t('title.admin.user.edit')}
            </h2>
        </ModalHeader>

        <ModalBody>
            <AdminEditUserForm user={user} permissions={permissions}/>
        </ModalBody>

        <ModalFooter>
            <Button
                form="admin-edit-user-form"
                color="primary"
                type="submit"
            >
                {t('button.edit')}
            </Button>
        </ModalFooter>
    </>
}

const AdminEditUserForm = ({user, permissions}: { user: UserType, permissions?: PermissionType[] }) => {
    const {t} = useTranslation()
    const {form, onSubmit} = useAdminEditUserApi({user, permissions})

    return <FormFields onSubmit={form.handleSubmit(onSubmit)} id="admin-edit-user-form">
        <Row cols={2}>
            <FormField>
                <FormLabel htmlFor="name">{t('input.label.name')}</FormLabel>
                <input
                    {...form.register('name')}
                    type="text"
                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-xs ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                />
                <FormError>{form.formState.errors.name?.message}</FormError>
            </FormField>
            <FormField>
                <FormLabel htmlFor="email">{t('input.label.email')}</FormLabel>
                <input
                    {...form.register('email')}
                    type="email"
                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-xs ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                />
                <FormError>{form.formState.errors.email?.message}</FormError>
            </FormField>
        </Row>
        <FormField>
            <FormLabel htmlFor="file_path">{t('input.label.file_path')}</FormLabel>
            <input
                {...form.register('file_path')}
                type="text"
                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-xs ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
            />
            <FormError>{form.formState.errors.file_path?.message}</FormError>
        </FormField>
        <FormField>
            <FormLabel htmlFor="permission">{t('input.label.permissions')}</FormLabel>
            <Controller
                name="permissions"
                control={form.control}
                render={({field}) => (
                    <CustomMultiSelect
                        options={permissions?.map((p) => ({ value: p.id, label: p.name })) || []}
                        value={field.value}
                        isMultiple={true}
                        isSearchable={false}
                        onChange={field.onChange}
                        placeholder={t('input.placeholder.permissions')}
                        className="w-full"
                    />
                )}
            />
            <FormError>{form.formState.errors.permissions?.message}</FormError>
        </FormField>
    </FormFields>
}