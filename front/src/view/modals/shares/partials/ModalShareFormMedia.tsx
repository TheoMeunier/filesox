import {useTranslation} from "react-i18next";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {useMutation} from "react-query";
import {useAxios} from "@config/axios.ts";
import {useModal} from "@hooks/useModal.ts";
import {useAlerts} from "@context/modules/AlertContext.tsx";
import {useStorage} from "@hooks/useStorage.ts";
import {useFileStore} from "@/stores/useFileStore.ts";
import {ModalBody, ModalFooter} from "@components/modules/Modal.tsx";
import {FormError, FormField, FormFields, FormLabel} from "@components/modules/Form.tsx";
import {Row} from "@components/modules/Grid.tsx";
import {Button} from "@components/modules/Button.tsx";
import {z} from "zod";

const schema = z.object({
    duration: z.number().positive(),
    type_duration: z.string(),
    password: z.string().optional()
})

type FormFields = z.infer<typeof schema>

export function ModalShareFormMedia () {
    const API = useAxios()
    const {closeModal} = useModal()
    const {setAlerts} = useAlerts()
    const {isFolder} = useStorage()
    const {activeStorage} = useFileStore()
    const {t} = useTranslation()

    const {
        register,
        handleSubmit,
        formState: {errors},
    } = useForm<FormFields>({
        resolver: zodResolver(schema),
    })

    const {mutate} = useMutation(
        async (data: FormFields) => {
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

    const onSubmit = (data: FormFields) => {
        mutate(data)
    }

    return <>
        <FormFields onSubmit={handleSubmit(onSubmit)}>
            <ModalBody>
                <FormField>
                    <FormLabel htmlFor="share_duration">{t('title.modal.share_media')}</FormLabel>
                    <Row cols={2}>
                        <FormField>
                            <input
                                type="number"
                                {...register("duration", {valueAsNumber: true})}
                                placeholder={t('input.placeholder.share_duration')}
                                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                            />
                            {errors.duration &&
                                <FormError>{errors.duration.message}</FormError>
                            }
                        </FormField>
                        <FormField>
                            <select
                                {...register("type_duration")}
                                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                            >
                                <option value="hours">{t('input.options.shares.hours')}</option>
                                <option value="days">{t('input.options.shares.days')}</option>
                                <option value="weeks">{t('input.options.shares.weeks')}</option>
                                <option value="months">{t('input.options.shares.months')}</option>
                                <option value="years">{t('input.options.shares.years')}</option>
                            </select>
                            {errors.type_duration &&
                                <FormError>{errors.type_duration.message}</FormError>
                            }
                        </FormField>
                    </Row>
                </FormField>
                <FormField>
                    <FormLabel htmlFor="password ">{t('input.label.optional_password')}</FormLabel>
                    <FormField>
                        <input
                            type="password"
                            {...register("password")}
                            placeholder={t('input.placeholder.password')}
                            className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                        />
                        {errors.password &&
                            <FormError>{errors.password.message}</FormError>
                        }
                    </FormField>
                </FormField>
            </ModalBody>
            <ModalFooter>
                <Button
                    color="primary"
                    type="submit"
                >
                    {t('button.share')}
                </Button>
            </ModalFooter>
        </FormFields>
    </>
}