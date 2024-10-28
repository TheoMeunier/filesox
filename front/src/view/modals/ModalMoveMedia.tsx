import {ModalBody, ModalFooter, ModalHeader} from "@components/modules/Modal.tsx";
import {FormDescription, FormError, FormField, FormFields, FormLabel} from "@components/modules/Form.tsx";
import {Button} from "@components/modules/Button.tsx";
import {useTranslation} from "react-i18next";
import {MoveUpRight} from "lucide-react";
import {TypoCode} from "@components/modules/Typo.tsx";
import {useMoveStorageApi} from "@/api/storageApi.ts";

export function ModalMoveMedia() {
    const {t} = useTranslation()
    const {form, onSubmit} = useMoveStorageApi()

    return <>
        <ModalHeader>
            <h2 className="flex items-center gap-2 text-2xl">
                <span className="text-indigo-500"><MoveUpRight height={28} width={28}/></span>
                {t('title.modal.move_media')}
            </h2>
        </ModalHeader>

        <ModalBody>
            <FormFields id="form-move-storage" onSubmit={form.handleSubmit(onSubmit)}>
                <FormDescription>
                    {t('input.description.move_storage')}: <TypoCode>./</TypoCode>.
                </FormDescription>
                <FormLabel htmlFor="name">
                    {t('input.label.path')}
                </FormLabel>
                <FormField>
                    <input
                        {...form.register('path')}
                        type="text"
                        placeholder={t('input.placeholder.path')}
                        className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                    />
                    <FormError>{form.formState.errors.path?.message}</FormError>
                </FormField>
            </FormFields>
        </ModalBody>

        <ModalFooter>
            <Button
                color="primary"
                type="submit"
                form="form-move-storage"
            >
                {t('button.move')}
            </Button>
        </ModalFooter>
    </>
}