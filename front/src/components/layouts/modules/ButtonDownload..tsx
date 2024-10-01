import {Download} from "lucide-react";
import {useStorage} from "@hooks/useStorage.ts";
import {useFileStore} from "@/stores/useFileStore.ts";
import {ButtonIcon} from "../../modules/Button.tsx";
import {useAxios} from "@config/axios.ts";

export function ButtonDownload() {
    const {isFolder, getPathOrName, getFolderName} = useStorage()
    const {activeStorage} = useFileStore()
    const API = useAxios()

    const handleClickDownload = async () => {
        try {
            const response = await API.post("/storages/download", {
                id: activeStorage?.id,
                is_folder: isFolder(),
                path: getPathOrName()
            }, {
                responseType: 'blob',
                onDownloadProgress: (progressEvent) => {
                    const total = progressEvent.total ?? 0
                    const percentCompleted = Math.round((progressEvent.loaded * 100) / total);
                    console.log(`Téléchargé ${percentCompleted}%`);

                    // Mise à jour de la barre de progression de l'onglet du navigateur
                    if ('setAppBadge' in navigator) {
                        navigator.setAppBadge(percentCompleted).catch(error => {
                            console.error("Erreur lors de la mise à jour de la barre de progression:", error);
                        });
                    }
                }
            });

            const blob = new Blob([response.data]);
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.style.display = 'none';
            a.href = url;
            a.download = !isFolder() ? getPathOrName() as string : getFolderName(getPathOrName() as string) + '.zip';
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error('Erreur lors du téléchargement:', error);
        }
    }

    return <>
        <ButtonIcon
            svg={Download}
            title="Download"
            onClick={handleClickDownload}
        />
    </>
}