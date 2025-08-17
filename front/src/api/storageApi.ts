import { useAxios } from '@config/axios.ts';
import { useFileStore } from '@stores/useFileStore.ts';
import { useTranslation } from 'react-i18next';
import { SubmitHandler, useForm } from 'react-hook-form';
import { useModal } from '@hooks/useModal.ts';
import { useStorage } from '@hooks/useStorage.ts';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  EditStorageFormFields,
  editStorageSchema,
  MoveStorageFormFields,
  moveStorageSchema,
} from '@/types/form/storageFormType.ts';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useEffect } from 'react';
import { useCurrentPath } from '@context/hooks/useCurrentPath.tsx';
import { useAlerts } from '@context/hooks/useAlert.tsx';

export function useStoragesApi() {
  const { setFiles, setFolders } = useFileStore();
  const { currentPath, setPath } = useCurrentPath();
  const API = useAxios();

  const { data, isSuccess, isLoading } = useQuery({
    queryKey: ['storage', currentPath],
    queryFn: async () => {
      const response = await API.post('/storages', {
        path: currentPath,
      });
      return response.data;
    },
  });

  useEffect(() => {
    if (isSuccess && data) {
      setPath(data.folder?.path, data.folder?.id);
      setFiles(data.files);
      setFolders(data.folders);
    }
  }, [isSuccess, data, setPath, setFiles, setFolders]);

  return { isLoading };
}

export function useSearchStorageApi(search: string) {
  const { setFolders, setFiles } = useFileStore();
  const API = useAxios();

  const { data, isSuccess, isLoading } = useQuery({
    queryKey: ['storage', search],
    queryFn: async () => {
      const response = await API.get(`/storages?search=${search}`);
      return response.data;
    },
    enabled: search.length >= 3,
  });

  if (isSuccess) {
    setFolders(undefined);
    setFiles(data.files);
  }

  return { isLoading };
}

export function useEditStorageApi() {
  const { closeModal } = useModal();
  const { setAlerts } = useAlerts();
  const { activeStorage } = useFileStore();
  const { getPathOrName } = useStorage();

  const API = useAxios();
  const client = useQueryClient();

  const form = useForm<EditStorageFormFields>({
    resolver: zodResolver(editStorageSchema),
    defaultValues: {
      name: getPathOrName(),
    },
  });

  const mutation = useMutation({
    mutationFn: async (data: EditStorageFormFields) => {
      await API.post('/storages/update', {
        id: activeStorage!.id,
        name: getPathOrName(),
        new_name: data.name,
        parent_id: activeStorage?.parent_id || null,
      });
    },
    onSuccess: () => {
      client.invalidateQueries({ queryKey: ['storage'] }).then(() => {
        setAlerts('success', 'Votre media à bien été modifier');
        closeModal();
      });
    },
  });

  const onSubmit: SubmitHandler<EditStorageFormFields> = (
    data: EditStorageFormFields
  ) => {
    mutation.mutate(data);
  };

  return { form, onSubmit };
}

export function useMoveStorageApi() {
  const { t } = useTranslation();
  const { closeModal } = useModal();
  const { setAlerts } = useAlerts();
  const { activeStorage } = useFileStore();
  const { isFolder, getPathOrName } = useStorage();
  const API = useAxios();
  const client = useQueryClient();

  const form = useForm<MoveStorageFormFields>({
    resolver: zodResolver(moveStorageSchema),
    defaultValues: {
      path: isFolder() ? getPathOrName() : '',
    },
  });

  const mutation = useMutation({
    mutationFn: async (data: MoveStorageFormFields) => {
      await API.post('/storages/move', {
        id: activeStorage!.id,
        storage_name: getPathOrName(),
        new_path: data.path,
        parent_id: activeStorage!.parent_id,
      });
    },
    onSuccess: () => {
      client.invalidateQueries({ queryKey: ['storage'] }).then(() => {
        setAlerts('success', t('alerts.success.folder.move'));
        closeModal();
      });
    },
  });

  const onSubmit: SubmitHandler<MoveStorageFormFields> = (
    data: MoveStorageFormFields
  ) => {
    mutation.mutate(data);
  };

  return { form, onSubmit };
}

export function useDeleteStorageApi() {
  const client = useQueryClient();
  const API = useAxios();
  const { t } = useTranslation();
  const { setAlerts } = useAlerts();
  const { closeModal } = useModal();
  const { activeStorage } = useFileStore();
  const { isFolder } = useStorage();

  const form = useForm();

  const mutation = useMutation({
    mutationFn: async () => {
      await API.post('/storages/delete', {
        id: activeStorage!.id,
        is_folder: isFolder(),
      });
    },
    onSuccess: () => {
      client.invalidateQueries({ queryKey: ['storage'] }).then(() => {
        setAlerts('success', t('alerts.success.folder.delete'));
        closeModal();
      });
    },
  });

  const onSubmit = () => {
    mutation.mutate();
  };

  return { form, onSubmit };
}
