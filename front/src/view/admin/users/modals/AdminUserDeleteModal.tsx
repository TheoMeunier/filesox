import {
    ModalBody,
    ModalFooterButton,
    ModalHeaderLogo
} from "@components/modules/Modal.tsx";
import {ButtonBig} from "@components/modules/Button.tsx";
import {useModal} from "@hooks/useModal.ts";
import {useTranslation} from "react-i18next";
import {Trash2} from "lucide-react";
import {useAdminDeleteUserApi} from "@/api/admin/adminUserApi.ts";

export function AdminDeleteUserModal({userId}: {userId: number}) {
    const {closeModal} = useModal()
    const {t}  = useTranslation()

    const {mutate} = useAdminDeleteUserApi(userId)

    return <>
        <ModalHeaderLogo color="danger">
            <Trash2 height="42" width="42"/>
        </ModalHeaderLogo>
        <ModalBody>
            <p className="text-center  py-4">
                {t('title.admin.user.delete.message')}
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
                color="danger"
                type="button"
                onClick={() => mutate()}
            >
                {t('button.delete')}
            </ButtonBig>
        </ModalFooterButton>
    </>
}