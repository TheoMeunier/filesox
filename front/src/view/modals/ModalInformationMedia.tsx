import {ModalBody, ModalHeader} from "@components/modules/Modal.tsx";
import {useFileStore} from "@/stores/useFileStore.ts";
import {InfoIcon} from "lucide-react";
import {useTranslation} from "react-i18next";

export function ModalInformationMedia() {
    const {activeStorage} = useFileStore()
    const {t} = useTranslation()

    return <>
        <ModalHeader>
            <h2 className="flex items-center gap-2 text-2xl">
                <span className="text-indigo-500"><InfoIcon height={28} width={28}/></span>
                <h2>{t('title.modal.information_media')}</h2>
            </h2>
        </ModalHeader>

        <ModalBody>
            <dl className="text-gray-900 divide-y divide-gray-200">
                {activeStorage && 'id' in activeStorage &&
                    <div className="flex flex-col py-3">
                        <dt className="mb-1 text-indigo-500">
                            {t('input.label.id')}:
                        </dt>
                        <dd className="mb-1 text-gray-500">{activeStorage.id}</dd>
                    </div>
                }
                {activeStorage && 'path' in activeStorage &&
                    <div className="flex flex-col py-3">
                        <dt className="mb-1 text-indigo-500">
                            {t('input.label.path')}:</dt>
                        <dd className="mb-1 text-gray-500 break-words whitespace-normal">{activeStorage.path}</dd>
                    </div>
                }
                {activeStorage && 'name' in activeStorage &&
                    <div className="flex flex-col py-3">
                        <dt className="mb-1 text-indigo-500">
                            {t('input.label.name')}:
                        </dt>
                        <dd className="mb-1 text-gray-500 break-words whitespace-normal">{activeStorage.name}</dd>
                    </div>
                }
                {activeStorage && 'size' in activeStorage &&
                    <div className="flex flex-col pt-3">
                        <dt className="mb-1 text-indigo-500">
                            {t('input.label.size')}:
                        </dt>
                        <dt className="mb-1 text-gray-500">{activeStorage.size}</dt>
                    </div>
                }
                {activeStorage && 'type' in activeStorage &&
                    <div className="flex flex-col pt-3">
                        <dt className="mb-1 text-indigo-500">
                            {t('input.label.file_type')}:
                        </dt>
                        <dt className="mb-1 text-gray-500">{activeStorage.type}</dt>
                    </div>
                }
            </dl>
        </ModalBody>
    </>
}