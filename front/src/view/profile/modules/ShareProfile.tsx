import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableNoData,
    TableRow
} from "@components/modules/Table.tsx";
import {useTranslation} from "react-i18next";
import {useModal} from "@hooks/useModal.ts";
import {useState} from "react";
import {ClipboardCopy, Trash2} from "lucide-react";
import {ModalDeleteShares} from "../../modals/shares/ModalDeleteShare.tsx";
import {ButtonIcon} from "@components/modules/Button.tsx";
import {Pagination} from "@components/modules/Pagination.tsx";
import {Loader} from "@components/modules/Loader/Loader.tsx";
import {environmentVariables} from "@config/env.ts";
import {useAlerts} from "@context/modules/AlertContext.tsx";
import {useSharesProfileApi} from "@/api/profileApi.ts";

export function ProfileShare() {
    const [page, setPage] = useState(1)
    const {t} = useTranslation();
    const {openModal} = useModal()
    const {setAlerts} = useAlerts()

    const {data, isLoading} = useSharesProfileApi(page)

    const handleCopy = (id: string) => {
        navigator.clipboard.writeText(environmentVariables.VITE_API_URL + '/storages/share/dl/' + id)
        setAlerts('success', t('alerts.success.shares.copy'))
    }

    if (isLoading) {
        return <Loader/>;
    }

    return <div className="px-7 py-4">
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
                {data && data.data.length > 0 ? (
                    data.data.map((share) =>
                        <TableRow key={share.id}>
                            <TableCell>{share.path}</TableCell>
                            <TableCell>{share.expired_at}</TableCell>
                            <TableCell>{share.created_at}</TableCell>
                            <TableCell>
                                <div className="flex items-center gap-2">
                                    <ButtonIcon title="copy" onClick={() => handleCopy(share.id)} svg={ClipboardCopy}/>
                                    <ButtonIcon title="delete" onClick={() => openModal(() => <ModalDeleteShares
                                        url={`/profile/shares/delete`} shareId={share.id}/>, 'md')} svg={Trash2}/>
                                </div>
                            </TableCell>
                        </TableRow>
                    )) : (
                    <TableNoData text={t('table.no_data')} colspan={8}/>
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
    </div>
}