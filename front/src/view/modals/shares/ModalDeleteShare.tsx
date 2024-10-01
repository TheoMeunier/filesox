import {useAlerts} from "@context/modules/AlertContext.tsx";
import {useModal} from "@hooks/useModal.ts";
import {useMutation, useQueryClient} from "react-query";
import {useTranslation} from "react-i18next";
import {useAxios} from "@config/axios.ts";
import {useForm} from "react-hook-form";
import {ModalBody, ModalFooterButton, ModalHeaderLogo} from "@components/modules/Modal.tsx";
import {Trash2} from "lucide-react";
import {FormFields} from "@components/modules/Form.tsx";
import {ButtonBig} from "@components/modules/Button.tsx";

export function ModalDeleteShares({url, shareId}: {url: string, shareId: string}) {
    const {setAlerts} = useAlerts()
    const {closeModal} = useModal()

    const client = useQueryClient()
    const API = useAxios()
    const {t} = useTranslation()

    const {
        handleSubmit,
    } = useForm()

    const {mutate} = useMutation(
        async () => {
            await API.post(url, {
                id: shareId
            })
        }, {
        onSuccess: () => {
            client.invalidateQueries('shares')
            setAlerts('success', t('alerts.success.shares.delete'))
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
                    {t('title.modal.delete_share')}
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