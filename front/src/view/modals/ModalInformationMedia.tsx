import {ModalBody, ModalHeader} from "../../components/modules/Modal.tsx";
import {useFileStore} from "../../stores/useFileStore.ts";
import {InfoIcon} from "lucide-react";

export function ModalInformationMedia() {
    const {activeStorage} = useFileStore()

    return <>
        <ModalHeader>
            <h2 className="flex items-center gap-2 text-2xl">
                <span className="text-indigo-500"><InfoIcon height={28} width={28}/></span>
                Information Média
            </h2>
        </ModalHeader>

        <ModalBody>
            <dl className="text-gray-900 divide-y divide-gray-200">
                {activeStorage && 'name' in activeStorage &&
                    <div className="flex flex-col pb-3">
                        <dt className="mb-1 text-indigo-500">Nom:</dt>
                        <dd className="mb-1 text-gray-500">{activeStorage.name}</dd>
                    </div>
                }
                {activeStorage && 'path' in activeStorage &&
                    <div className="flex flex-col py-3">
                        <dt className="mb-1 text-indigo-500">Chemain:</dt>
                        <dd className="mb-1 text-gray-500">{activeStorage.path}</dd>
                    </div>
                }
                {activeStorage && 'name' in activeStorage &&
                    <div className="flex flex-col py-3">
                        <dt className="mb-1 text-indigo-500">Nom:</dt>
                        <dd className="mb-1 text-gray-500">{activeStorage.name}</dd>
                    </div>
                }
                {activeStorage && 'size' in activeStorage &&
                    <div className="flex flex-col pt-3">
                        <dt className="mb-1 text-indigo-500">Taille:</dt>
                        <dt className="mb-1 text-gray-500">{activeStorage!.size}</dt>
                    </div>
                }
            </dl>
        </ModalBody>
    </>
}