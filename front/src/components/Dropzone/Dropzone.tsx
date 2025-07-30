import {useDropzone} from "react-dropzone";
import {ReactNode, useCallback} from "react";
import './dropzone.css';
import {useFileStore} from "@stores/useFileStore.ts";
import {useUploadApi} from "@/api/useUploadApi.tsx";

export function Dropzone({children}: { children: ReactNode }) {
    const {setFiles} = useFileStore()
    const {handleVerifyIsExistFile} = useUploadApi()

    const onDrop = useCallback((acceptedFiles: File[]) => {
        handleVerifyIsExistFile(acceptedFiles);
    }, [setFiles]);

    const {getRootProps, isDragActive} = useDropzone({
        onDrop,
    });

    return (
        <div {...getRootProps()} className={`relative cols-span-3 px-6 h-full`}>
            {children}

            <div className={`dropzone__file ${isDragActive ? 'active' : ''}`}></div>
        </div>
    );
}