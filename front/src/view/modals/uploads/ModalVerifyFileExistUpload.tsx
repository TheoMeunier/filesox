import {ModalBody, ModalFooterButton, ModalHeaderLogo,} from "@components/modules/Modal.tsx";
import {ButtonBig} from "@components/modules/Button.tsx";
import {useModal} from "@hooks/useModal.ts";
import {RefreshCcw} from "lucide-react";
import {useTranslation} from "react-i18next";

export function ModalVerifyFileExistUpload( {
    onConfirm,
}: {
    onConfirm: () => void;
    files: File[];
    parentId?: string;
}) {
    const {closeModal} = useModal()
    const {t} = useTranslation()

    const handleReplaceFile = async () => {
        try {
            onConfirm();
            closeModal();
        } catch (error) {
            console.error('Error replacing files:', error);
        }
    };

    return <>
        <ModalHeaderLogo color="primary">
            <RefreshCcw height="42" width="42"/>
        </ModalHeaderLogo>

        <ModalBody>
            <p className="text-center mt-3 py-3.5">
                {t('title.modal.replace_file')}
            </p>
        </ModalBody>

        <ModalFooterButton>
            <ButtonBig
                color="white"
                onClick={() => closeModal()}
            >
                {t('button.cancel')}
            </ButtonBig>
            <ButtonBig
                color="primary"
                onClick={handleReplaceFile}
            >
                {t('button.reply')}
            </ButtonBig>
        </ModalFooterButton>
    </>
}