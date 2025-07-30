import {useAuth} from "@context/modules/AuthContext.tsx";
import {useTranslation} from "react-i18next";
import {useNavigate} from "react-router-dom";
import {useAlerts} from "@context/modules/AlertContext.tsx";
import {SubmitHandler, useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {useMutation} from "react-query";
import axios from "axios";
import {BASE_URL} from "@config/axios.ts";
import {loginSchemaType} from "@/types/api/authType.ts";
import {loginSchema, LoginSchemaFormFields} from "@/types/form/authFormType.ts";

export function useLoginApi() {
    const {login} = useAuth()
    const {t} = useTranslation()
    const nav = useNavigate()
    const {setAlerts} = useAlerts()

    const form = useForm<LoginSchemaFormFields>({
        resolver: zodResolver(loginSchema)
    })

    const mutation = useMutation(
        async (data :  LoginSchemaFormFields ) => {
            return await axios.post(BASE_URL + '/auth/login', {
                email: data.email,
                password: data.password
            });
        },
        {
            onSuccess: (response: any) => {
                login(loginSchemaType.parse(response.data));
                nav("/");
            },
            onError: () => {
                setAlerts('danger',  t('alerts.error.auth.login'))
            }
        }
    );


    const onSubmit: SubmitHandler<LoginSchemaFormFields> = async (data: LoginSchemaFormFields) => {
        mutation.mutate(data)
    }

    return {
        form,
        onSubmit,
        isLoading: mutation.isLoading,
    }
}