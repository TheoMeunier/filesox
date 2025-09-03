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
import { useSharesProfileApi } from '@/api/profileApi.ts';
import { formatDate } from '@/utils/date.ts';
import { useAlerts } from '@context/hooks/useAlert.tsx';
import TableSkeleton from '@components/skeletons/TableSkeleton.tsx';

export function ProfileShare() {
  const { t } = useTranslation();
  const { openModal } = useModal();
  const { setAlerts } = useAlerts();

  const { data, isLoading } = useSharesProfileApi();

  const handleCopy = (path: string) => {
    const storage = path.startsWith('/') ? path : `/${path}`;
    navigator.clipboard.writeText(
      `${window.location.origin}/api/shares/dl${storage}`
    );
    setAlerts('success', t('alerts.success.shares.copy'));
  };

  return (
    <div className="px-7 py-4">
      {isLoading ? (
        <TableSkeleton col={4} />
      ) : (
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
                        onClick={() =>
                          handleCopy(share.path + '?password=' + share.password)
                        }
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
      )}
    </div>
  );
}
