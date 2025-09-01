import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { SubmitHandler, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import axios from 'axios';
import { loginSchemaType } from '@/types/api/authType.ts';
import {
  loginSchema,
  LoginSchemaFormFields,
} from '@/types/form/authFormType.ts';
import { useMutation } from '@tanstack/react-query';
import { useAuth } from '@context/hooks/useAuth.tsx';
import { useAlerts } from '@context/hooks/useAlert.tsx';

export function useLoginApi() {
  const { login } = useAuth();
  const { t } = useTranslation();
  const nav = useNavigate();
  const { setAlerts } = useAlerts();

  const form = useForm<LoginSchemaFormFields>({
    resolver: zodResolver(loginSchema),
  });

  const mutation = useMutation({
    mutationFn: async (data: LoginSchemaFormFields) => {
      return await axios.post('/api/auth/login', {
        email: data.email,
        password: data.password,
      });
    },
    onSuccess: (response: any) => {
      login(loginSchemaType.parse(response.data));
      nav('/');
    },
    onError: () => {
      setAlerts('danger', t('alerts.error.auth.login'));
    },
  });

  const onSubmit: SubmitHandler<LoginSchemaFormFields> = async (
    data: LoginSchemaFormFields
  ) => {
    mutation.mutate(data);
  };

  return {
    form,
    onSubmit,
    isLoading: mutation.isPending,
  };
}
