import {Card, CardBody} from "@components/modules/Card.tsx";
import {FormError, FormField, FormFields, FormLabel} from "@components/modules/Form.tsx";
import {Button} from "@components/modules/Button.tsx";
import {useEditProfileApi} from "@/api/profileApi.ts";
import {useTranslation} from "react-i18next";

export function EditProfileInformation() {
    const {form, onSubmit} = useEditProfileApi()
    const {t} = useTranslation()

    return (
        <Card>
            <CardBody>
                <h2 className="text-xl font-semibold mb-4">
                    {t('title.profile.edit')}
                </h2>

                <FormFields onSubmit={form.handleSubmit(onSubmit)}>

                    <FormField>
                        <FormLabel htmlFor="name">
                            {t('input.label.name')}
                        </FormLabel>
                        <input
                            {...form.register('name')}
                            type="text"
                            className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                        />
                            <FormError>{form.formState.errors.name?.message}</FormError>
                    </FormField>
                    <FormField>
                        <FormLabel htmlFor="name">
                            {t('input.label.email')}
                        </FormLabel>
                        <input
                            {...form.register('email')}
                            type="email"
                            className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                        />
                            <FormError>{form.formState.errors.email?.message}</FormError>
                    </FormField>

                    <div className="flex justify-end mt-4">
                        <Button color={'primary'} type={'submit'}>
                            {t('button.save')}
                        </Button>
                    </div>
                </FormFields>
            </CardBody>
        </Card>
    );
}