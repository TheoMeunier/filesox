import {ModalBody, ModalFooter, ModalHeader} from "@components/modules/Modal.tsx";
import {FormError, FormField, FormFields, FormLabel} from "@components/modules/Form.tsx";
import {Button} from "@components/modules/Button.tsx";
import {SubmitHandler, useForm} from "react-hook-form";
import {useMutation, useQueryClient} from "react-query";
import {useAlerts} from "@context/modules/AlertContext.tsx";
import {z} from "zod";
import {zodResolver} from "@hookform/resolvers/zod";
import {useModal} from "@hooks/useModal.ts";
import {useAxios} from "@config/axios.ts";
import {useTranslation} from "react-i18next";
import {FilePaths, useLocalStorage} from "@hooks/useLocalStorage.ts";
import {FolderPlus} from "lucide-react";

const schema = z.object({
    path: z.string().min(2)
})

type FormFields = z.infer<typeof schema>

export function ModalCreateFolder() {
    const {setAlerts} = useAlerts()
    const {closeModal} = useModal()
    const client = useQueryClient()
    const API = useAxios()
    const {t} = useTranslation()
    const {getItem} = useLocalStorage()

    const {
        register,
        handleSubmit,
        formState: {errors},
    } = useForm<FormFields>({
        resolver: zodResolver(schema),
    })

    const {mutate} = useMutation(
        async ({path} :{path: string}) => {
            await API.post("/folders/create", {
                path:  getItem(FilePaths.path) === 'null' ? '' + path : getItem(FilePaths.path) + path,
                parent_id: getItem(FilePaths.id) === 'null' ? null : getItem(FilePaths.id)
            })
        }, {
        onSuccess: () => {
            client.invalidateQueries('storage')
            setAlerts('success', t('alerts.success.folder.create'))
            closeModal()
        }
    })

    const onSubmit: SubmitHandler<FormFields> = (data: FormFields) => {
        mutate({
            path: data.path
        })
    }

    return <>
        <ModalHeader>
            <h2 className="flex items-center gap-2 text-2xl">
                <span className="text-indigo-500"><FolderPlus height={28} width={28}/></span>
                {t('title.modal.create_folder')}
            </h2>
        </ModalHeader>

        <FormFields onSubmit={handleSubmit(onSubmit)}>
            <ModalBody>
                <FormLabel htmlFor="name">
                    {t('input.label.name')}
                </FormLabel>
                <FormField>
                    <input
                        {...register('path')}
                        type="text"
                        className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                    />
                    {errors.path &&
                        <FormError>{errors.path.message}</FormError>
                    }
                </FormField>
            </ModalBody>

            <ModalFooter>
                <Button color="primary" type={'submit'}>
                    {t('button.create')}
                </Button>
            </ModalFooter>
        </FormFields>
    </>
}