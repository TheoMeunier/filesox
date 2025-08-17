import {
  ModalBody,
  ModalFooter,
  ModalHeader,
} from '@components/modules/Modal.tsx';
import {
  FormDescription,
  FormError,
  FormField,
  FormFields,
  FormLabel,
} from '@components/modules/Form.tsx';
import { Button } from '@components/modules/Button.tsx';
import { Row } from '@components/modules/Grid.tsx';
import { useTranslation } from 'react-i18next';
import { UserPlus } from 'lucide-react';
import { TypoCode } from '@components/modules/Typo.tsx';
import { useAdminPermissionsApi } from '@/api/admin/adminApi.ts';
import { useAdminCreateUserApi } from '@/api/admin/adminUserApi.ts';
import { Loader } from '@/components/modules/Loader/Loader';
import { Controller } from 'react-hook-form';
import CustomMultiSelect from '@/components/modules/Inputs/Select';

export function AdminCreateUserModal() {
  const { permissions, isLoading: isLoadingPagination } =
    useAdminPermissionsApi();
  const { t } = useTranslation();
  const { form, onSubmit } = useAdminCreateUserApi();

  if (isLoadingPagination) return <Loader />;

  return (
    <>
      <ModalHeader>
        <h2 className="flex items-center gap-2 text-2xl">
          <span className="text-indigo-500">
            <UserPlus height={28} width={28} />
          </span>
          {t('title.admin.user.create')}
        </h2>
      </ModalHeader>
      <FormFields onSubmit={form.handleSubmit(onSubmit)}>
        <ModalBody>
          <FormDescription>
            {t('input.description.create_user')} <TypoCode>./</TypoCode>.
          </FormDescription>
          <Row cols={2}>
            <FormField>
              <FormLabel htmlFor="name">{t('input.label.name')}</FormLabel>
              <input
                {...form.register('name')}
                type="text"
                placeholder={t('input.placeholder.name')}
                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-xs ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
              />
              <FormError>{form.formState.errors.name?.message}</FormError>
            </FormField>
            <FormField>
              <FormLabel htmlFor="email">{t('input.label.email')}</FormLabel>
              <input
                {...form.register('email')}
                type="email"
                placeholder={t('input.placeholder.email')}
                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-xs ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
              />
              <FormError>{form.formState.errors.email?.message}</FormError>
            </FormField>
          </Row>
          <Row cols={2}>
            <FormField>
              <FormLabel htmlFor="password">
                {t('input.label.password')}
              </FormLabel>
              <input
                {...form.register('password')}
                type="password"
                placeholder={t('input.placeholder.password')}
                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-xs ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
              />
              <FormError>{form.formState.errors.password?.message}</FormError>
            </FormField>
            <FormField>
              <FormLabel htmlFor="file_path">
                {t('input.label.file_path')}
              </FormLabel>
              <input
                {...form.register('file_path')}
                type="text"
                placeholder={t('input.placeholder.file_path')}
                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-xs ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
              />
              <FormError>{form.formState.errors.file_path?.message}</FormError>
            </FormField>
          </Row>

          <FormField>
            <FormLabel htmlFor="permission">
              {t('input.label.permissions')}
            </FormLabel>
            <Controller
              name="permissions"
              control={form.control}
              render={({ field }) => (
                <CustomMultiSelect
                  options={
                    permissions?.map((p) => ({ value: p.id, label: p.name })) ||
                    []
                  }
                  value={field.value}
                  isMultiple={true}
                  isSearchable={false}
                  onChange={field.onChange}
                  placeholder={t('input.placeholder.permissions')}
                  className="w-full"
                />
              )}
            />
            <FormError>{form.formState.errors.permissions?.message}</FormError>
          </FormField>
        </ModalBody>
        <ModalFooter>
          <Button color="primary" type="submit">
            {t('button.create')}
          </Button>
        </ModalFooter>
      </FormFields>
    </>
  );
}
