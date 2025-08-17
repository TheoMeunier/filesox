import { logsProfileSchemaType } from '@/types/api/userType.ts';
import { useAxios } from '@config/axios.ts';
import { apiProfileSharedSchemaType } from '@/types/api/apiProfileType.ts';
import { SubmitHandler, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useUserStore } from '@stores/useUserStore.ts';
import {
  ProfileEditFormFields,
  ProfileEditPasswordFormFields,
  profileEditPasswordSchema,
  profileEditSchema,
} from '@/types/form/profileFormType.ts';
import { useMutation, useQuery } from '@tanstack/react-query';
import { useAlerts } from '@context/hooks/useAlert.tsx';

export function useLogsProfileApi() {
  const API = useAxios();

  const { data, isLoading } = useQuery({
    queryKey: ['profile-logs'],
    queryFn: async () => {
      const response = await API.get('/profile/logs');
      return logsProfileSchemaType.parse(response.data);
    },
  });

  return { data, isLoading };
}

export function useSharesProfileApi() {
  const API = useAxios();

  const { data, isLoading } = useQuery({
    queryKey: ['profile-shares'],
    queryFn: async () => {
      const response = await API.get('/profile/shares');
      return apiProfileSharedSchemaType.parse(response.data);
    },
  });

  return { data, isLoading };
}

export function useEditProfileApi() {
  const API = useAxios();
  const { setAlerts } = useAlerts();
  const { user, setUser } = useUserStore();

  const form = useForm<ProfileEditFormFields>({
    resolver: zodResolver(profileEditSchema),
    defaultValues: {
      email: user?.email,
      name: user?.username,
    },
  });

  const mutation = useMutation({
    mutationFn: async (data: ProfileEditFormFields) => {
      await API.post('/profile/update', {
        name: data.name,
        email: data.email,
        layout: user?.layout,
      });
    },
    onSuccess: () => {
      setAlerts('success', 'Profile information updated');
    },
  });

  const onSubmit: SubmitHandler<ProfileEditFormFields> = (
    data: ProfileEditFormFields
  ) => {
    mutation.mutate(data);
    setUser({ ...user!, username: data.name, email: data.email });
  };

  return { form, onSubmit };
}

export function useProfileEditPasswordApi() {
  const API = useAxios();
  const { setAlerts } = useAlerts();

  const form = useForm<ProfileEditPasswordFormFields>({
    resolver: zodResolver(profileEditPasswordSchema),
  });

  const mutation = useMutation({
    mutationFn: async ({
      password,
      confirm_password,
    }: {
      password: string;
      confirm_password: string;
    }) => {
      await API.post('/profile/update/password', {
        password: password,
        confirm_password: confirm_password,
      });
    },
    onSuccess: () => {
      form.reset({ password: '', confirm_password: '' });
      setAlerts('success', 'Password updated successfully');
    },
  });

  const onSubmit: SubmitHandler<ProfileEditPasswordFormFields> = (
    data: ProfileEditPasswordFormFields
  ) => {
    mutation.mutate(data);
  };

  return { form, onSubmit };
}
