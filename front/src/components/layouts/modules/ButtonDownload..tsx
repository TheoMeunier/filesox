import {Download} from "lucide-react";
import {ButtonIcon} from "../../modules/Button.tsx";
import {useDownloadApi} from "@/api/useDownloadApi.ts";

export function ButtonDownload() {
    const {handleClickDownload} = useDownloadApi()

    return <>
        <ButtonIcon
            svg={Download}
            title="Download"
            onClick={() => handleClickDownload()}
        />
    </>
}