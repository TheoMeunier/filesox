import {ModalBody, ModalFooter, ModalHeader} from "../../components/modules/Modal.tsx";
import {FormError, FormField, FormFields, FormLabel} from "../../components/modules/Form.tsx";
import {Button} from "../../components/modules/Button.tsx";
import {useModal} from "../../hooks/useModal.ts";
import {useAlerts} from "../../context/modules/AlertContext.tsx";
import {useAxios} from "../../config/axios.ts";
import {useMutation, useQueryClient} from "react-query";
import {useForm} from "react-hook-form";
import {z} from "zod";
import {zodResolver} from "@hookform/resolvers/zod";
import {FilePaths, useLocalStorage} from "../../hooks/useLocalStorage.ts";
import {useUserStore} from "../../stores/useUserStore.ts";
import {useFileStore} from "../../stores/useFileStore.ts";
import {useTranslation} from "react-i18next";

const schema = z.object({
    path: z.string().min(2)
})

type FormFields = z.infer<typeof schema>

export function ModalMoveMedia() {
    const {closeModal} = useModal()
    const {setAlerts} = useAlerts()
    const {getItem} = useLocalStorage()
    const {activeStorage} = useFileStore()
    const {user} = useUserStore()
    const {t} = useTranslation()

    const API = useAxios()
    const client = useQueryClient()

    const {
        register,
        handleSubmit,
        formState: {errors},
    } = useForm<FormFields>({
        resolver: zodResolver(schema),
    })

    const {mutate} = useMutation(
        async (path: string ) => {
            console.log(activeStorage!.name)
            await API.post("/folders/move", {
                path: user?.file_path === "./" ? activeStorage!.name : user?.file_path + getItem(FilePaths.path)! + activeStorage!.name,
                new_path: path
            })
        }, {
            onSuccess: () => {
                client.invalidateQueries('storage')
                setAlerts('success', t('alerts.success.folder.move'))
                closeModal()
            }
        })

    const onSubmit = (data: FormFields) => {
        mutate(data.path)
    }

    return <>
        <ModalHeader>
            <h2 className="text-2xl">Move media</h2>
        </ModalHeader>

        <FormFields onSubmit={handleSubmit(onSubmit)}>
            <ModalBody>
                <FormLabel htmlFor="name">
                    {t('input.label.path')}
                </FormLabel>
                <FormField>
                    <input
                        {...register('path')}
                        type="text"
                        placeholder= {t('input.placeholder.path')}
                        className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                    />
                    {errors.path &&
                        <FormError>{errors.path.message}</FormError>
                    }
                </FormField>
            </ModalBody>

            <ModalFooter>
                <Button
                    color="primary"
                    type="submit"
                >
                    {t('button.move')}
                </Button>
            </ModalFooter>
        </FormFields>
    </>
}