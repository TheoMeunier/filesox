import {FormError, FormField, FormFields, FormLabel} from "@components/modules/Form.tsx";
import {useLoginApi} from "@/api/authApi.ts";
import {useTranslation} from "react-i18next";

export default function Login() {
    const {t} = useTranslation()
    const {form, onSubmit} = useLoginApi()

    return <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-md w-full space-y-8">


            <div className="bg-white py-8 px-6 shadow-lg rounded-lg">
                <div className="text-center">
                    <img
                        src="/logo.png"
                        alt="Logo"
                        height="100"
                        width="175"
                        className="mx-auto h-20 w-auto"
                    />
                    <h2 className="text-3xl font-bold text-gray-900">
                        {t('title.auth.sign_in_to_tour_account')}
                    </h2>
                </div>

                <FormFields onSubmit={form.handleSubmit(onSubmit)}>
                    <div className="space-y-6 mt-5">
                        <div>
                            <FormLabel htmlFor="email">
                                {t('input.label.email')}
                            </FormLabel>
                            <FormField>
                                <input
                                    {...form.register('email')}
                                    type="email"
                                    placeholder={t('input.placeholder.email')}
                                    className="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-hidden focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                                />
                                <FormError>{form.formState.errors.email?.message}</FormError>
                            </FormField>
                        </div>

                        <div>
                            <FormLabel htmlFor="password">
                                {t('input.label.password')}
                            </FormLabel>
                            <FormField>
                                <input
                                    {...form.register('password')}
                                    type="password"
                                    className="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-hidden focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                                />
                                <FormError>{form.formState.errors.password?.message}</FormError>
                            </FormField>
                        </div>

                        <div>
                            <button
                                type="submit"
                                className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-hidden focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition duration-150 ease-in-out"
                            >
                                {t('button.login')}
                            </button>
                        </div>
                    </div>
                </FormFields>
            </div>
        </div>
    </div>
}