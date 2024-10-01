import {FileType, FolderType} from "@/types/api/storageType.ts";
import {ReactNode} from "react";
import {FilePaths, useLocalStorage} from "@hooks/useLocalStorage.ts";
import {LayoutModules} from "./modules/LayoutModulesImage.tsx";
import {useTranslation} from "react-i18next";
import {useCurrentPath} from "@context/modules/CurrentPathContext.tsx";
import {useFileStore} from "@stores/useFileStore.ts";
import {truncateString} from "@hooks/useStore.ts";

export function LayoutsGrid({files, folders}: { files: FileType[] | undefined, folders: FolderType[] | undefined }) {
    const {activeStorage, setActiveStorage} = useFileStore();
    const {setPath} = useCurrentPath()
    const {t} = useTranslation()

    const handleDoubleClick = (folder: FolderType) => {
        setPath(folder.path, folder.id)
    }

    const handleFocus = (file: FileType) => {
        setActiveStorage(file)
    }

    return <>
        <div>
            <h1 className="text-2xl font-bold text-gray-800">
                {t('title.folders')}
            </h1>
            <hr className="mb-4" />

            <div className="flex flex-wrap items-center gap-5">
                {folders && folders.map((folder, index) => (
                    <div
                        key={index}
                        tabIndex={0}
                        onClick={(e) => {
                            setActiveStorage(folder);
                            e.stopPropagation();
                        }}
                        onDoubleClick={() => {
                            handleDoubleClick(folder);
                        }}
                        className={`min-w-full md:min-w-72 flex gap-3 items-center px-4 py-2 rounded-lg border border-gray-200 ${activeStorage &&  activeStorage.id === folder.id ? 'bg-indigo-50 text-indigo-500 shadow-md cursor-pointer' : 'cursor-pointer shadow-md bg-white text-black'}`}
                    >
                        <LayoutCardGrid name={folder.path} isFolder={true}>
                            <img src="images/folder-icon.png" alt="folder-icon" width="42" height="42" />
                        </LayoutCardGrid>
                    </div>
                ))}
            </div>
        </div>

        <div className="mt-7">
            <h1 className="text-2xl font-bold text-gray-800">
                {t('title.files')}
            </h1>
            <hr className="mb-4" />

            <div className="flex flex-wrap items-center gap-5">
                {files && files.map((file, index) => (
                    <div
                        key={index}
                        onClick={(e) => {
                            e.stopPropagation();
                            handleFocus(file);
                        }}
                        tabIndex={0}
                        className={`flex gap-3 items-center px-4 py-2 border border-gray-200 rounded-md min-w-full md:min-w-72 md:max-w-72 ${activeStorage && activeStorage.id === file.id ? 'bg-indigo-50 text-indigo-500 shadow-md cursor-pointer' : 'cursor-pointer shadow-md bg-white'}`}
                    >
                        <LayoutCardGrid name={file.name} isFolder={false} size={file.size}>
                            <LayoutModules file={file} />
                        </LayoutCardGrid>
                    </div>
                ))}
            </div>
        </div>
    </>
}

export function LayoutCardGrid({name, isFolder, size, children}: {
    name: string,
    isFolder: boolean,
    children: ReactNode
    size?: string | number,
}) {
    const {getItem} = useLocalStorage()
    const path = getItem(FilePaths.path)

    return <>
        {children}

        {!isFolder ?
            <div className="gap-3 items-center">
                <p className="truncate">{truncateString(name.replace(path ?? "", ""), 17)}</p>
                <span className="text-gray-500 text-xs">{size}</span>
            </div>
            :
            <p className="truncate">{name.replace(path ?? "", "")}</p>
        }
    </>
}