import {useStorage} from "@hooks/useStorage.ts";
import {useFileStore} from "@stores/useFileStore.ts";
import {useAxios} from "@config/axios.ts";

export function useDownloadApi() {

    const {isFolder, getPathOrName, getFolderName} = useStorage()
    const {activeStorage} = useFileStore()
    const API = useAxios()

    const handleClickDownload = async (filePath? : string) => {
        console.log('Download file:', filePath)
        try {
            const response = await API.post("/storages/download", {
                id: activeStorage?.id,
                is_folder: isFolder(),
                path: filePath ?? getPathOrName()
            }, {
                responseType: 'blob',
                onDownloadProgress: (progressEvent) => {
                    const total = progressEvent.total ?? 0
                    const percentCompleted = Math.round((progressEvent.loaded * 100) / total);

                    if ('setAppBadge' in navigator) {
                        navigator.setAppBadge(percentCompleted).catch(error => {
                            console.error("Error on download", error);
                        });
                    }
                }
            });

            const blob = new Blob([response.data]);
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.style.display = 'none';
            a.href = url;

            if (filePath) {
                a.download = filePath.split('/').pop() || 'download';
            } else if (isFolder()) {
                a.download = `${getFolderName(getPathOrName() as string)}.zip`;
            } else {
                a.download = getPathOrName() as string;
            }

            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error('Error on download:', error);
        }
    }

    return {handleClickDownload}
}