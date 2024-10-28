import {useTranslation} from "react-i18next";
import {ModalBody, ModalFooter} from "@components/modules/Modal.tsx";
import {FormError, FormField, FormFields, FormLabel} from "@components/modules/Form.tsx";
import {Row} from "@components/modules/Grid.tsx";
import {Button} from "@components/modules/Button.tsx";
import {useCreateShare} from "@/api/shareApi.ts";

export function ModalShareFormMedia() {
    const {t} = useTranslation()
    const {form, onSubmit} = useCreateShare()

    return <>
        <ModalBody>
            <FormFields id="form-share-storage" onSubmit={form.handleSubmit(onSubmit)}>
                <FormField>
                    <FormLabel htmlFor="share_duration">{t('title.modal.share_media')}</FormLabel>
                    <Row cols={2}>
                        <FormField>
                            <input
                                type="number"
                                {...form.register("duration", {valueAsNumber: true})}
                                placeholder={t('input.placeholder.share_duration')}
                                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                            />
                            <FormError>{form.formState.errors.duration?.message}</FormError>
                        </FormField>
                        <FormField>
                            <select
                                {...form.register("type_duration")}
                                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                            >
                                <option value="hours">{t('input.options.shares.hours')}</option>
                                <option value="days">{t('input.options.shares.days')}</option>
                                <option value="weeks">{t('input.options.shares.weeks')}</option>
                                <option value="months">{t('input.options.shares.months')}</option>
                                <option value="years">{t('input.options.shares.years')}</option>
                            </select>
                            <FormError>{form.formState.errors.type_duration?.message}</FormError>
                        </FormField>
                    </Row>
                </FormField>
                <FormField>
                    <FormLabel htmlFor="password ">{t('input.label.optional_password')}</FormLabel>
                    <FormField>
                        <input
                            type="password"
                            {...form.register("password")}
                            placeholder={t('input.placeholder.password')}
                            className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                        />
                        <FormError>{form.formState.errors.password?.message}</FormError>
                    </FormField>
                </FormField>
            </FormFields>
        </ModalBody>
        <ModalFooter>
            <Button
                color="primary"
                type="submit"
                form="form-share-storage"
            >
                {t('button.share')}
            </Button>
        </ModalFooter>
    </>
}