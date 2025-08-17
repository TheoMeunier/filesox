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
import { useModal } from '@hooks/useModal.ts';
import { ClipboardCopy, Trash2 } from 'lucide-react';
import { ModalDeleteShares } from '../../modals/shares/ModalDeleteShare.tsx';
import { ButtonIcon } from '@components/modules/Button.tsx';
import { Loader } from '@components/modules/Loader/Loader.tsx';
import { useSharesProfileApi } from '@/api/profileApi.ts';
import { formatDate } from '@/utils/date.ts';
import { useAlerts } from '@context/hooks/useAlert.tsx';

export function ProfileShare() {
  const { t } = useTranslation();
  const { openModal } = useModal();
  const { setAlerts } = useAlerts();

  const { data, isLoading } = useSharesProfileApi();

  const handleCopy = (id: string) => {
    navigator.clipboard.writeText(import.meta.env + '/storages/share/dl/' + id);
    setAlerts('success', t('alerts.success.shares.copy'));
  };

  if (isLoading) {
    return <Loader />;
  }

  return (
    <div className="px-7 py-4">
      <Table>
        <TableHead>
          <TableRow>
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
                              url={`/shares/delete/${share.id}`}
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
    </div>
  );
}
