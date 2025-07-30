import {ModalBody, ModalFooterButton, ModalHeaderLogo} from "@components/modules/Modal.tsx";
import {Trash2} from "lucide-react";
import {FormFields} from "@components/modules/Form.tsx";
import {ButtonBig} from "@components/modules/Button.tsx";
import {useDeleteShare} from "@/api/shareApi.ts";
import {useModal} from "@hooks/useModal.ts";
import {useTranslation} from "react-i18next";

export function ModalDeleteShares({url, shareId}: { url: string, shareId: string }) {
    const {form, onSubmit} = useDeleteShare({url, shareId})
    const {closeModal} = useModal()
    const {t} = useTranslation()

    return <>
        <ModalHeaderLogo color="danger">
            <Trash2 height="42" width="42"/>
        </ModalHeaderLogo>

        <ModalBody>
            <FormFields id="form-delete-share" onSubmit={form.handleSubmit(onSubmit)}>
                <p className="text-center mt-3 py-3.5">
                    {t('title.modal.delete_share')}
                </p>
            </FormFields>
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
                type="submit"
                form="form-delete-share"
            >
                {t('button.delete')}
            </ButtonBig>
        </ModalFooterButton>
    </>
}