import {Dropzone} from "../components/Dropzone/Dropzone.tsx";
import {useFileStore} from "../stores/useFileStore.ts";
import {LayoutsGrid} from "./storages/LayoutsGrid.tsx";
import {Breadcrumb} from "../components/modules/Breadcrumb.tsx";
import {Loader} from "../components/modules/Loader/Loader.tsx";
import {useStoragesApi} from "@/api/storageApi.ts";

export function Dashboard() {
    const {isLoading} = useStoragesApi();
    const {files, folders, setActiveStorage} = useFileStore();

    if (isLoading) {
        return <Loader/>;
    }

    return <div className="px-4 py-7 h-[87.5vh]" onClick={() => setActiveStorage(null)}>
        <Dropzone>
            <Breadcrumb/>
            <LayoutsGrid files={files} folders={folders}/>
        </Dropzone>
    </div>
}