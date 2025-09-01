import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableNoData,
  TableRow,
} from '@components/modules/Table.tsx';
import { useTranslation } from 'react-i18next';
import { ButtonIcon } from '@components/modules/Button.tsx';
import { ClipboardCopy, Share2, Trash2 } from 'lucide-react';
import { useModal } from '@hooks/useModal.ts';
import { ModalDeleteShares } from '../modals/shares/ModalDeleteShare.tsx';
import { useAdminSharesApi } from '@/api/admin/adminApi.ts';
import { formatDate } from '@/utils/date.ts';
import { useAlerts } from '@context/hooks/useAlert.tsx';
import TableSkeleton from '@components/skeletons/TableSkeleton.tsx';

export function AdminShares() {
  const { t } = useTranslation();
  const { openModal } = useModal();
  const { setAlerts } = useAlerts();

  const { data, isLoading } = useAdminSharesApi();

  const handleCopy = (id: string) => {
    navigator.clipboard.writeText(
      import.meta.env.VITE_API_URL + '/storages/share/dl/' + id
    );
    setAlerts('success', t('alerts.success.shares.copy'));
  };

  return (
    <div className="px-7 py-4">
      <div className="flex items-center gap-3 mb-4">
        <Share2 className="text-indigo-500" />
        <h1 className="text-2xl text-indigo-950 font-semibold">
          {t('title.admin.shares')}
        </h1>
      </div>

      {isLoading ? (
        <TableSkeleton col={4} />
      ) : (
        <Table>
          <TableHead>
            <TableRow>
              <TableHeader>{t('table.user')}</TableHeader>
              <TableHeader>{t('table.path')}</TableHeader>
              <TableHeader>{t('table.expired_at')}</TableHeader>
              <TableHeader>{t('table.created_at')}</TableHeader>
              <TableHeader>{t('table.actions')}</TableHeader>
            </TableRow>
          </TableHead>
          <TableBody>
            {data && data.length > 0 ? (
              data.map((share) => (
                <TableRow key={share.id}>
                  <TableCell>{share.username}</TableCell>
                  <TableCell>{share.path}</TableCell>
                  <TableCell>{formatDate(share.expired_at)}</TableCell>
                  <TableCell>{formatDate(share.created_at)}</TableCell>
                  <TableCell>
                    <div className="flex items-center gap-2">
                      <ButtonIcon
                        title="copy"
                        onClick={() => handleCopy(share.id)}
                        svg={ClipboardCopy}
                      />
                      <ButtonIcon
                        title="delete"
                        onClick={() =>
                          openModal(
                            () => (
                              <ModalDeleteShares
                                url={`/admin/shares/delete}`}
                              />
                            ),
                            'md'
                          )
                        }
                        svg={Trash2}
                      />
                    </div>
                  </TableCell>
                </TableRow>
              ))
            ) : (
              <TableNoData text={t('table.no_data')} colspan={8} />
            )}
          </TableBody>
        </Table>
      )}
    </div>
  );
}
