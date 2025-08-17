import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableNoData,
    TableRow
} from "@components/modules/Table.tsx";
import {Pill} from "@components/modules/Pill.tsx";
import {Loader} from "@components/modules/Loader/Loader.tsx";
import {useLogsProfileApi} from "@/api/profileApi.ts";
import {useTranslation} from "react-i18next";
import {formatDate} from "@/utils/date.ts";

export function ProfileLog() {
    const {t} = useTranslation()
    const {data, isLoading} = useLogsProfileApi()

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
                {data && data.length > 0 ? (
                    data.map((log) =>
                        <TableRow key={log.id}>
                            <TableCell>{log.details}</TableCell>
                            <TableCell>
                                <Pill type={log.action}>{log.action}</Pill>
                            </TableCell>
                            <TableCell>{formatDate(log.created_at)}</TableCell>
                        </TableRow>
                    )) : (
                    <TableNoData text={t('table.no_data')} colspan={8}/>
                )}

            </TableBody>
        </Table>
    </div>;
}