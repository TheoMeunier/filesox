import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@components/modules/Table.tsx";
import {Pagination} from "@components/modules/Pagination.tsx";
import {useState} from "react";
import {Pill} from "@components/modules/Pill.tsx";
import {Loader} from "@components/modules/Loader/Loader.tsx";
import {useLogsProfileApi} from "@/api/profileApi.ts";
import {useTranslation} from "react-i18next";

export function ProfileLog() {
    const {t} = useTranslation()
    const [page , setPage] = useState(1)
    const {data, isLoading} = useLogsProfileApi(page)

    if (isLoading) {
        return <Loader/>;
    }

    return <div className="px-7 py-4">
        <Table>
            <TableHead>
                <TableRow>
                    <TableHeader>{t('table.subject')}</TableHeader>
                    <TableHeader>{t('table.actions')}</TableHeader>
                    <TableHeader>{t('table.created_at')}</TableHeader>
                </TableRow>
            </TableHead>
            <TableBody>
                {data && data.data.map((log) =>
                    <TableRow key={log.id}>
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
                onPageChange={(p) => {setPage(p)}}
            />
        }
    </div>;
}