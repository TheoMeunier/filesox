import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@components/modules/Table.tsx";
import {Pagination} from "@components/modules/Pagination.tsx";
import {useState} from "react";
import {Pill} from "@components/modules/Pill.tsx";
import {useTranslation} from "react-i18next";
import {Archive} from "lucide-react";
import {Loader} from "@components/modules/Loader/Loader.tsx";
import {useAdminLogsApi} from "@/api/admin/adminApi.ts";

export function AdminLogs() {
    const [page, setPage] = useState(1)
    const {t} = useTranslation();

    const {data, isLoading} = useAdminLogsApi(page)

    if (isLoading) {
        return <Loader/>;
    }

    return <div className="px-7 py-4">
        <div className="flex items-center gap-3 mb-4">
            <Archive className="text-indigo-500"/>
            <h1 className="text-2xl text-indigo-950 font-semibold">
                {t('title.admin.logs')}
            </h1>
        </div>

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
                {data && data.data.map((log) =>
                    <TableRow key={log.id}>
                        <TableCell>{log.username}</TableCell>
                        <TableCell>{log.subject}</TableCell>
                        <TableCell>
                            <Pill type={log.action}>{log.action}</Pill>
                        </TableCell>
                        <TableCell>{log.created_at}</TableCell>
                    </TableRow>
                )}
            </TableBody>
        </Table>

        {data &&
            <Pagination
                currentPage={data.current_page}
                totalPage={data.total_pages}
                onPageChange={(p) => {
                    setPage(p)
                }}
            />
        }
    </div>
}