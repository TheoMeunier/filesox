import {useEffect, useState} from "react";
import {ChevronLeft, ChevronRight, Download, X} from "lucide-react";
import {useFileStore} from "@stores/useFileStore";
import {useImageGalleryStore} from "@stores/useImageGalery";
import {LayoutModules} from "@/view/storages/modules/LayoutModulesImage";
import {FileType} from "@/types/api/storageType";
import {ButtonBig} from "@components/modules/Button.tsx";
import {useTranslation} from "react-i18next";
import {useDownloadApi} from "@/api/useDownloadApi.ts";

export default function ImageGallery() {
    const [file, setFile] = useState<FileType | undefined>(undefined);
    const {showImageGallery, imageGalleryIndex, setImageGalleryShow, setImageGalleryIndex} = useImageGalleryStore();
    const {files} = useFileStore();
    const {handleClickDownload} = useDownloadApi()
    const {t} = useTranslation()

    useEffect(() => {
        if (files && files.length > 0) {
            setFile(files[imageGalleryIndex]);
        }
    }, [files, imageGalleryIndex]);

    const handleSwitch = (direction: number) => {
        if (!files || files.length === 0) return;

        const newIndex = (imageGalleryIndex + direction + files.length) % files.length;
        setImageGalleryIndex(newIndex);
    };

    if (!showImageGallery || !file) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-gray-700/90">
            <div className="absolute top-0 left-0 right-0 flex justify-between items-center p-4">
                <h2 className="text-white text-xl font-semibold truncate max-w-[80%]">
                    {file.name}
                </h2>
                <button
                    className="text-white hover:text-gray-300 transition-colors focus:outline-none"
                    onClick={() => setImageGalleryShow(false)}
                    aria-label="Close gallery"
                >
                    <X size={32}/>
                </button>
            </div>

            <div className="flex items-center justify-between w-full px-4">
                <button
                    className="text-white hover:text-gray-300 transition-colors"
                    onClick={() => handleSwitch(-1)}
                >
                    <ChevronLeft size={48}/>
                </button>

                <div className="flex-grow flex flex-col justify-center items-center w-full h-full">
                    <div className="relative max-w-[80vw] max-h-[70vh] flex justify-center items-center">
                        <LayoutModules
                            file={file}
                            height="auto"
                            width="auto"
                        />
                    </div>
                    {file.icon === "file" || file.icon !== "image" && (
                        <>
                            <div className="mt-4 text-center">
                                <p className="text-white mb-4">{file.name}</p>
                            </div>
                            <ButtonBig
                                color="primary"
                                onClick={() => handleClickDownload(file.name)}
                            >
                                <Download className="mr-3" size={23}/>
                                {t('button.download')}
                            </ButtonBig>
                        </>
                    )}
                </div>

                <button
                    className="text-white hover:text-gray-300 transition-colors"
                    onClick={() => handleSwitch(1)}
                >
                    <ChevronRight size={48}/>
                </button>
            </div>
        </div>
    )
        ;
}