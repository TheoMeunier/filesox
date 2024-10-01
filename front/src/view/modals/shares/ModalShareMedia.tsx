import {ModalShareFormMedia} from "./partials/ModalShareFormMedia.tsx";
import {Share2} from "lucide-react";
import {ModalHeader} from "@components/modules/Modal.tsx";
import {ModalShareListMedia} from "./partials/ModalShareListMedia.tsx";
import {useTranslation} from "react-i18next";

export function ModalShareMedia() {
    const {t} = useTranslation()

    return <>
        <ModalHeader>
            <h2 className="flex items-center gap-2 text-2xl">
                <span className="text-indigo-500"><Share2 height={28} width={28}/></span>
                {t('title.modal.share_media')}
            </h2>
        </ModalHeader>

        <ModalShareListMedia/>
        <ModalShareFormMedia/>
    </>
}