import {useMutation, useQuery, useQueryClient} from "react-query";
import {useAxios} from "@config/axios.ts";
import {useTranslation} from "react-i18next";
import {SubmitHandler, useForm} from "react-hook-form";
import {useAlerts} from "@context/modules/AlertContext.tsx";
import {useModal} from "@hooks/useModal.ts";
import {ListModalShareSchemaType} from "@/types/api/storageType.ts";
import {useFileStore} from "@stores/useFileStore.ts";
import {zodResolver} from "@hookform/resolvers/zod";
import {useStorage} from "@hooks/useStorage.ts";
import {ShareStorageFormFields, shareStorageSchema} from "@/types/form/shareFormTypes.ts";

export function useSharesByStorageId() {
    const API = useAxios()
    const {activeStorage} = useFileStore()

    const {data, isLoading} = useQuery(
        ['shares', activeStorage!.id],
        async () => {
            const response = await API.get('/storages/share/' + activeStorage!.id)
            return ListModalShareSchemaType.parse(response.data)
        },
    );

    return  {data, isLoading}
}

export function useCreateShare() {
    const API = useAxios()
    const {closeModal} = useModal()
    const {setAlerts} = useAlerts()
    const {isFolder} = useStorage()
    const {activeStorage} = useFileStore()
    const {t} = useTranslation()

    const form = useForm<ShareStorageFormFields>({
        resolver: zodResolver(shareStorageSchema),
    })

    const mutation = useMutation(
        async (data: ShareStorageFormFields) => {
            await API.post("/storages/share", {
                storage_id: activeStorage!.id,
                type: !isFolder ? "file" : "folder",
                password: data.password === "" ? null : data.password,
                duration: data.duration,
                type_duration: data.type_duration
            })
        }, {
            onSuccess: () => {
                setAlerts('success', t('alerts.success.shares.create'))
                closeModal()
            }
        })

    const onSubmit: SubmitHandler<ShareStorageFormFields> = (data: ShareStorageFormFields) => {
        mutation.mutate(data)
    }

    return {form, onSubmit}
}

export function useDeleteShare({url, shareId}: {url: string, shareId: string}) {
    const {setAlerts} = useAlerts()
    const {closeModal} = useModal()
    const client = useQueryClient()
    const API = useAxios()
    const {t} = useTranslation()

    const form = useForm()

    const {mutate} = useMutation(
        async () => {
            await API.post(url, {
                id: shareId
            })
        }, {
            onSuccess: () => {
                client.invalidateQueries('shares')
                setAlerts('success', t('alerts.success.shares.delete'))
                closeModal()
            }
        })

    const onSubmit = () => {
        mutate()
    }

    return { form, onSubmit}
}