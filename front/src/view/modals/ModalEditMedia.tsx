import {ModalBody, ModalFooter, ModalHeader} from "@components/modules/Modal.tsx";
import {FormError, FormField, FormFields, FormLabel} from "@components/modules/Form.tsx";
import {Button} from "@components/modules/Button.tsx";
import {SquarePen} from "lucide-react";
import {useTranslation} from "react-i18next";
import {useEditStorageApi} from "@/api/storageApi.ts";

export function ModalEditMedia() {
    const {t} = useTranslation()
    const {form, onSubmit} = useEditStorageApi()

    return <>
        <ModalHeader>
            <h2 className="flex items-center gap-2 text-2xl">
                <span className="text-indigo-500"><SquarePen height={28} width={28}/></span>
                {t('title.modal.edit_media')}
            </h2>
        </ModalHeader>

        <ModalBody>
            <FormFields id="form-edit-storage" onSubmit={form.handleSubmit(onSubmit)}>
                <FormLabel htmlFor="name">Edit</FormLabel>
                <FormField>
                    <input
                        {...form.register('name')}
                        type="text"
                        className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                    />
                    <FormError>{form.formState.errors.name?.message}</FormError>
                </FormField>
            </FormFields>
        </ModalBody>

        <ModalFooter>
            <Button
                color="primary"
                type="submit"
                form="form-edit-storage"
            >
                Edit
            </Button>
        </ModalFooter>
    </>
}