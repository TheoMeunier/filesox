import {useCurrentPath} from "@context/modules/CurrentPathContext.tsx";
import {useAxios} from "@config/axios.ts";
import {useMutation, useQuery, useQueryClient} from "react-query";
import {useFileStore} from "@stores/useFileStore.ts";
import {useTranslation} from "react-i18next";
import {SubmitHandler, useForm} from "react-hook-form";
import {useAlerts} from "@context/modules/AlertContext.tsx";
import {useModal} from "@hooks/useModal.ts";
import {useStorage} from "@hooks/useStorage.ts";
import {zodResolver} from "@hookform/resolvers/zod";
import {
    EditStorageFormFields,
    editStorageSchema,
    MoveStorageFormFields,
    moveStorageSchema
} from "@/types/form/storageFormType.ts";

export function useStoragesApi() {
    const {setFiles, setFolders} = useFileStore();
    const {currentPath, setPath} = useCurrentPath()
    const API = useAxios()

    const {isLoading} = useQuery(
        ["storage", currentPath],
        async () => {
            const response = await API.post("/storages", {
                path: currentPath === null ? 'null' : currentPath
            })
            return response.data
        }
        ,
        {
            onSuccess: (data) => {
                setPath(data.folder?.path, data.folder?.id)
                setFiles(data.files)
                setFolders(data.folders)
            }
        })

    return {isLoading}
}

export function useSearchStorageApi(search: string) {
    const {setFolders, setFiles} = useFileStore()
    const API = useAxios()

    const {isLoading} = useQuery(
        ['storage', search],
        async () => {
            const response = await API.get(`/storages?search=${search}`)
            return response.data
        },
        {
            enabled: search.length >= 3,
            keepPreviousData: true,
            onSuccess: (data) => {
                setFolders(undefined)
                setFiles(data.files)
            },
        })

    return {isLoading}
}

export function useEditStorageApi() {
    const {closeModal} = useModal()
    const {setAlerts} = useAlerts()
    const {activeStorage} = useFileStore()
    const {getPathOrName} = useStorage()

    const API = useAxios()
    const client = useQueryClient()

    const form = useForm<EditStorageFormFields>({
        resolver: zodResolver(editStorageSchema),
        defaultValues: {
            name: getPathOrName()
        }
    })

    const mutation = useMutation(
        async (data: EditStorageFormFields) => {
            await API.post("/storages/update", {
                id: activeStorage!.id,
                name: getPathOrName(),
                new_name: data.name,
                parent_id: activeStorage?.parent_id || null
            })
        }, {
            onSuccess: () => {
                client.invalidateQueries('storage')
                setAlerts('success', 'Votre media à bien été modifier')
                closeModal()
            }
        })


    const onSubmit: SubmitHandler<EditStorageFormFields> = (data: EditStorageFormFields) => {
        mutation.mutate(data)
    }

    return {form, onSubmit}
}

export function useMoveStorageApi() {
    const {t} = useTranslation()
    const {closeModal} = useModal()
    const {setAlerts} = useAlerts()
    const {activeStorage} = useFileStore()
    const {isFolder, getPathOrName} = useStorage()
    const API = useAxios()
    const client = useQueryClient()


    const form = useForm<MoveStorageFormFields>({
        resolver: zodResolver(moveStorageSchema),
        defaultValues: {
            path: isFolder() ? getPathOrName() : ''
        }
    })

    const mutation = useMutation(
        async (data: MoveStorageFormFields ) => {
            await API.post("/storages/move", {
                id: activeStorage!.id,
                path: getPathOrName(),
                new_path: data.path,
                parent_id: activeStorage!.parent_id
            })
        }, {
            onSuccess: () => {
                client.invalidateQueries('storage')
                setAlerts('success', t('alerts.success.folder.move'))
                closeModal()
            }
        })

    const onSubmit: SubmitHandler<MoveStorageFormFields> = (data: MoveStorageFormFields) => {
        mutation.mutate(data)
    }

    return {form, onSubmit}
}

export function useDeleteStorageApi() {
    const client = useQueryClient()
    const API = useAxios()
    const {t} = useTranslation()
    const {setAlerts} = useAlerts()
    const {closeModal} = useModal()
    const {activeStorage} = useFileStore()
    const {isFolder} = useStorage()

    const form = useForm()

    const mutation = useMutation(
        async () => {
            await API.post("/storages/delete", {
                id: activeStorage!.id,
                is_folder: isFolder()
            })
        }, {
            onSuccess: () => {
                client.invalidateQueries('storage')
                setAlerts('success', t('alerts.success.folder.delete'))
                closeModal()
            }
        })

    const onSubmit = () => {
        mutation.mutate()
    }

    return {form, onSubmit}
}