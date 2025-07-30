import {
    ModalBody,
    ModalFooterButton,
    ModalHeaderLogo,
} from "@components/modules/Modal.tsx";
import {ButtonBig} from "@components/modules/Button.tsx";
import {FormFields} from "@components/modules/Form.tsx";
import {useModal} from "@hooks/useModal.ts";
import {Trash2} from "lucide-react";
import {useDeleteStorageApi} from "@/api/storageApi.ts";
import {useTranslation} from "react-i18next";

export function ModalDeleteMedia() {
    const {closeModal} = useModal()
    const {t} = useTranslation()
    const {form, onSubmit} = useDeleteStorageApi()

    return <>
        <ModalHeaderLogo color="danger">
            <Trash2 height="42" width="42"/>
        </ModalHeaderLogo>

        <ModalBody>
            <FormFields id="form-delete-storage" onSubmit={form.handleSubmit(onSubmit)}>
                <p className="text-center mt-3 py-3.5">
                    {t('title.modal.delete_media_message')}
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
                form="form-delete-storage"
            >
                {t('button.delete')}
            </ButtonBig>
        </ModalFooterButton>
    </>
}