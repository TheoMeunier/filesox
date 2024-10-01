import {ModalBody, ModalFooter, ModalHeader} from "@components/modules/Modal.tsx";
import {FormError, FormField, FormFields, FormLabel} from "@components/modules/Form.tsx";
import {Button} from "@components/modules/Button.tsx";
import {useModal} from "@hooks/useModal.ts";
import {useAlerts} from "@context/modules/AlertContext.tsx";
import {useAxios} from "@config/axios.ts";
import {useMutation, useQueryClient} from "react-query";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {z} from "zod";
import {useFileStore} from "@/stores/useFileStore.ts";
import {SquarePen} from "lucide-react";
import {useTranslation} from "react-i18next";
import {useStorage} from "@hooks/useStorage.ts";

const schema = z.object({
    name: z.string().min(2)
})

type FormFields = z.infer<typeof schema>

export function ModalEditMedia() {
    const {closeModal} = useModal()
    const {setAlerts} = useAlerts()
    const {activeStorage} = useFileStore()
    const {getPathOrName} = useStorage()

    const API = useAxios()
    const client = useQueryClient()
    const {t} = useTranslation()

    const {
        register,
        handleSubmit,
        formState: {errors},
    } = useForm<FormFields>({
        resolver: zodResolver(schema),
        defaultValues: {
            name: getPathOrName()
        }
    })

    const {mutate} = useMutation(
        async (name: string ) => {
            await API.post("/storages/update", {
                id: activeStorage!.id,
                name: getPathOrName(),
                new_name: name,
                parent_id: activeStorage?.parent_id || null
            })
        }, {
            onSuccess: () => {
                client.invalidateQueries('storage')
                setAlerts('success', 'Votre media à bien été modifier')
                closeModal()
            }
        })


    const onSubmit = (data: FormFields) => {
       mutate(data.name)
    }

    return <>
        <ModalHeader>
            <h2 className="flex items-center gap-2 text-2xl">
                <span className="text-indigo-500"><SquarePen height={28} width={28}/></span>
                {t('title.modal.edit_media')}
            </h2>
        </ModalHeader>


        <FormFields onSubmit={handleSubmit(onSubmit)} >
            <ModalBody>
                    <FormLabel htmlFor="name">Edit</FormLabel>
                    <FormField>
                        <input
                            {...register('name')}
                            type="text"
                            className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                        />
                        {errors.name &&
                            <FormError>{errors.name.message}</FormError>
                        }
                    </FormField>

            </ModalBody>

            <ModalFooter>
                <Button
                    color="primary"
                    type="submit"
                >
                    Edit
                </Button>
            </ModalFooter>
        </FormFields>
    </>
}