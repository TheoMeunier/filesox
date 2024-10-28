import {FilePaths, useLocalStorage} from "@hooks/useLocalStorage.ts";
import {ModalVerifyFileExistUpload} from "@/view/modals/uploads/ModalVerifyFileExistUpload.tsx";
import {useState} from "react";
import {useTranslation} from "react-i18next";
import {useAxios} from "@config/axios.ts";
import {useQueryClient} from "react-query";
import {useProgressBar} from "@stores/useProgressBar.ts";
import {useModal} from "@hooks/useModal.ts";
import {useAlerts} from "@context/modules/AlertContext.tsx";

export function useUploadApi() {
    const [_, setFileIsExist] = useState(false);
    const {t} = useTranslation();
    const API = useAxios()
    const queryClient = useQueryClient()
    const {getItem} = useLocalStorage()
    const {setUploadLogin, setVal} = useProgressBar()
    const {openModal} = useModal();
    const {setAlerts} = useAlerts()

    const handleFileUpload = async (files: File[], isExist: Boolean) => {
        const chunkSize = 1024 * 1024 * 5; // 5MB
        const totalChunksFiles = files.map(file => Math.ceil(file.size / chunkSize)).reduce((a, b) => a + b, 0);
        const parent_id = getItem(FilePaths.id) === 'null' ? null : getItem(FilePaths.id);
        setUploadLogin(true);

        for (const file of Array.from(files)) {
            const totalChunks = Math.ceil(file.size / chunkSize);

            const initResponse = await API.post("/files/upload/init", {
                    name: file.name,
                    size: file.size,
                    type: file.type,
                    last_modified: file.lastModified,
                    web_relative_path: file.webkitRelativePath,
                    parent_id: parent_id,
                    total_chunks: totalChunks,
                    is_exist: isExist,
                },
                {
                    headers: {
                        "Content-Type": "application/json",
                    },
                });

            const uploadResponse = initResponse.data;
            const chunks = createFileChunks(file, chunkSize);

            for (let index = 0; index < chunks.length; index++) {
                const chunk = chunks[index];
                const formData = new FormData();
                formData.append('uploadId', uploadResponse.upload_id);
                formData.append('chunkNumber', (index + 1).toString());
                formData.append('totalChunks', totalChunks.toString());
                formData.append('file', chunk, uploadResponse.filename);

                await API.post("/files/upload", formData, {
                    headers: {
                        "Content-Type": "multipart/form-data",
                    },
                });

                setVal(Math.round(((index + 1) / totalChunksFiles) * 100));
            }

            await API.post("/files/upload/complete", {
                upload_id: uploadResponse.upload_id,
                filename: uploadResponse.filename,
                is_exist: isExist,
            });

            await queryClient.invalidateQueries("storage")
        }

        setUploadLogin(false);
        setAlerts("success", t('alerts.success.upload'));
    };

    const handleVerifyIsExistFile = async (files: File[]) => {
        const parent_id = getItem(FilePaths.id) === 'null' ? null : getItem(FilePaths.id);

        const response = await API.post("/files/upload/verify", {
            files: files.map(file => ({
                name: file.name,
                parent_id: parent_id,
            }))
        });

        if (response.data.exist) {
            setFileIsExist(true);
            openModal(() => <ModalVerifyFileExistUpload
                onConfirm={() => handleFileUpload(files, true)}
            files={files}
            parentId={parent_id as string}
            />, 'md');
        } else {
            handleFileUpload(files, false);
        }
    }

    return { handleVerifyIsExistFile };
}

function createFileChunks(file: File, chunkSize: number) {
    const chunks: Blob[] = [];
    let offset = 0;

    while (offset < file.size) {
        chunks.push(file.slice(offset, offset + chunkSize));
        offset += chunkSize;
    }

    return chunks;
}