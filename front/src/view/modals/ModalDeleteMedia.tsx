import {
    ModalBody,
    ModalFooterButton,
    ModalHeaderLogo,
} from "@components/modules/Modal.tsx";
import {ButtonBig} from "@components/modules/Button.tsx";
import {useAlerts} from "@context/modules/AlertContext.tsx";
import {FormFields} from "@components/modules/Form.tsx";
import {useForm} from "react-hook-form";
import {useMutation, useQueryClient} from "react-query";
import {useModal} from "@hooks/useModal.ts";
import {useAxios} from "@config/axios.ts";
import {useTranslation} from "react-i18next";
import {Trash2} from "lucide-react";
import {useStorage} from "@hooks/useStorage.ts";
import {useFileStore} from "@/stores/useFileStore.ts";

export function ModalDeleteMedia() {
    const {setAlerts} = useAlerts()
    const {activeStorage} = useFileStore()
    const {closeModal} = useModal()
    const {isFolder} = useStorage()

    const client = useQueryClient()
    const API = useAxios()
    const {t} = useTranslation()

    const {
        handleSubmit,
    } = useForm()

    const {mutate} = useMutation(
        async () => {
            await API.post("/storages/delete", {
                id: activeStorage!.id,
                is_folder: isFolder()
            })
        }, {
        onSuccess: () => {
            client.invalidateQueries('storage')
            setAlerts('success', t('alerts.success.folder.delete'))
            closeModal()
        }
    })

    const onSubmit = () => {
        mutate()
    }

    return <>
        <ModalHeaderLogo color="danger">
            <Trash2 height="42" width="42"/>
        </ModalHeaderLogo>

        <FormFields onSubmit={handleSubmit(onSubmit)}>
            <ModalBody>

                <p className="text-center mt-3 py-3.5">
                    {t('title.modal.delete_media_message')}
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
                    type="submit"
                >
                    {t('button.delete')}
                </ButtonBig>
            </ModalFooterButton>
        </FormFields>
    </>
}