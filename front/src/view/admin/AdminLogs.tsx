import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableNoData,
  TableRow,
} from '@components/modules/Table.tsx';
import { Pill } from '@components/modules/Pill.tsx';
import { useTranslation } from 'react-i18next';
import { Archive } from 'lucide-react';
import { useAdminLogsApi } from '@/api/admin/adminApi.ts';
import { formatDate } from '@/utils/date.ts';
import TableSkeleton from '@components/skeletons/TableSkeleton.tsx';

export function AdminLogs() {
  const { t } = useTranslation();

  const { data, isLoading } = useAdminLogsApi();

  return (
    <div className="px-7 py-4">
      <div className="flex items-center gap-3 mb-4">
        <Archive className="text-indigo-500" />
        <h1 className="text-2xl text-indigo-950 font-semibold">
          {t('title.admin.logs')}
        </h1>
      </div>

      {isLoading ? (
        <TableSkeleton col={4} />
      ) : (
        <Table>
          <TableHead>
            <TableRow>
              <TableHeader>{t('table.user')}</TableHeader>
              <TableHeader>{t('table.subject')}</TableHeader>
              <TableHeader>{t('table.actions')}</TableHeader>
              <TableHeader>{t('table.created_at')}</TableHeader>
            </TableRow>
          </TableHead>
          <TableBody>
            {data && data.length > 0 ? (
              data.map((log) => (
                <TableRow key={log.id}>
                  <TableCell>{log.username}</TableCell>
                  <TableCell>{log.details}</TableCell>
                  <TableCell>
                    <Pill type={log.action}>{log.action}</Pill>
                  </TableCell>
                  <TableCell>{formatDate(log.created_at)}</TableCell>
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
