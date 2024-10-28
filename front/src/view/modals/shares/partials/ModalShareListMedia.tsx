import {useTranslation} from "react-i18next";
import {ButtonIcon} from "@components/modules/Button.tsx";
import {ClipboardCopy, Trash2} from "lucide-react";
import {ModalDeleteShares} from "../ModalDeleteShare.tsx";
import {useModal} from "@hooks/useModal.ts";
import {Loader} from "@components/modules/Loader/Loader.tsx";
import {environmentVariables} from "@config/env.ts";
import {useAlerts} from "@context/modules/AlertContext.tsx";
import {useSharesByStorageId} from "@/api/shareApi.ts";

export function ModalShareListMedia() {
    const {t} = useTranslation()
    const {openModal} = useModal()
    const {setAlerts} = useAlerts()

    const {data, isLoading} = useSharesByStorageId()

    const handleCopy = (id: string) => {
        navigator.clipboard.writeText(environmentVariables.VITE_API_URL + '/storages/share/dl/' + id)
        setAlerts('success', t('alerts.success.shares.copy'))
    }

    if (isLoading) {
        return <Loader/>;
    }

    return <>
        {data && data.length > 0 && (
            <div>
                <ul className="grid-3 gap-3 mb-2 text-gray-600 text-sm border py-2 px-3 border-gray-200 bg-gray-50 rounded-t-md">
                    <li className="col-span-2">{t('table.expired_at')}</li>
                    <li>{t('table.actions')}</li>
                </ul>
                <div className="divide-y">
                    {data.map((share) => (
                        <ul key={share.id} className="grid-3 items-center text-sm gap-3 px-2 mb-2 text-gray-600 ">
                            <li className="col-span-2">{share.expired_at}</li>
                            <li>
                                <div className="flex items-center gap-2">
                                    <ButtonIcon title="copy" onClick={() => handleCopy(share.id)}
                                                svg={ClipboardCopy}/>
                                    <ButtonIcon title="delete" onClick={() => openModal(() => <ModalDeleteShares
                                        url={`/profile/shares/delete`} shareId={share.id}/>, 'md')} svg={Trash2}/>
                                </div>
                            </li>
                        </ul>
                    ))}
                </div>

                <hr className="mb-4"/>
            </div>
        )}
    </>
}