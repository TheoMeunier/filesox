import {Card, CardBody} from "@components/modules/Card.tsx";
import {FormError, FormField, FormFields, FormLabel} from "@components/modules/Form.tsx";
import {Button} from "@components/modules/Button.tsx";
import {z} from "zod";
import {useAlerts} from "@context/modules/AlertContext.tsx";
import {SubmitHandler, useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {useMutation} from "react-query";
import {useAxios} from "@config/axios.ts";

const schema = z.object({
    password: z.string().min(8),
    confirm_password: z.string().min(8)
}).refine(
    data => data.password === data.confirm_password,
    {
        message: 'Passwords must match',
        path: ['password_confirmation']
    }
)

type FormFields = z.infer<typeof schema>


export function EditProfilePassword() {
    const {setAlerts} = useAlerts()
    const API = useAxios()

    const {
        register,
        handleSubmit,
        formState: { errors},
        reset
    } = useForm<FormFields>({
        resolver: zodResolver(schema),
    })

    const {mutate} = useMutation(
        async ({password, confirm_password}: {password: string,  confirm_password: string}) => {
            await API.post('/profile/update/password',{
                password: password,
                confirm_password: confirm_password
            })
        }, {
        onSuccess: () => {
            reset({password: '', confirm_password: ''})
            setAlerts('success', 'Password updated successfully')
        },
        onError: (error: any) => {
            setAlerts('error', error.response.data.message)
        }
    })

    const onSubmit: SubmitHandler<FormFields> = (data) => {
        mutate(data)
    }

    return <>
        <Card>
            <CardBody>
                <h2 className="text-xl font-semibold mb-4">Edit Password</h2>

                <FormFields onSubmit={handleSubmit(onSubmit)}>
                    <FormField>
                        <FormLabel htmlFor="password">Password</FormLabel>
                        <input
                            {...register('password')}
                            type="password"
                            className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                        />
                        {errors.password &&
                            <FormError>{errors.password.message}</FormError>
                        }
                    </FormField>
                    <FormField>
                        <FormLabel htmlFor="confirm_password">Confirm Password</FormLabel>
                        <input
                            {...register('confirm_password')}
                            type="password"
                            className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                        />
                        {errors.confirm_password &&
                            <FormError>{errors.confirm_password.message}</FormError>
                        }
                    </FormField>

                    <div className="flex justify-end mt-4">
                        <Button color={'primary'} type={'submit'}>Edit password</Button>
                    </div>
                </FormFields>
            </CardBody>
        </Card>
    </>
}