import {create} from "zustand";
import {ActiveStorageType, FileType, FolderType} from "../types/api/storageType.ts";

type State = {
    files: undefined | FileType[];
    folders: undefined | FolderType[];
    activeStorage: ActiveStorageType<FolderType | FileType>;
};

type Action = {
    setFiles: (file: State["files"]) => void;
    setFolders: (folder: State["folders"]) => void;
    setActiveStorage: (folder: State["activeStorage"]) => void;
};

export const useFileStore = create<State & Action>((set) => ({
    files: [] as FileType[],
    folders: [] as FolderType[],
    activeStorage: null,
    setFiles: (file: undefined | FileType[]) => set(() => ({ files: file })),
    setFolders: (folder: undefined | FolderType[]) => set(() => ({ folders: folder })),
    setActiveStorage: (activeStorage) => set(() => ({ activeStorage: activeStorage })),
}));