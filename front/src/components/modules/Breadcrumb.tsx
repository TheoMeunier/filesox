import {Home} from "lucide-react";
import {ReactNode} from "react";
import {useTranslation} from "react-i18next";
import {useCurrentPath} from "@context/modules/CurrentPathContext.tsx";
import {useUserStore} from "@stores/useUserStore.ts";

export function Breadcrumb() {
    const {user} = useUserStore()
    const {currentPath} = useCurrentPath()
    const {t} = useTranslation()
    const pathnames = currentPath?.split("/").filter((x: string) => x) ?? []

    return <div className="mb-8">
        <div className="flex items-center gap-3">
            <BreadcrumbItem
                to={"null"}
                active={currentPath !== user!.file_path}
            >
                <Home strokeWidth={1.5} size={20}/>
                {t('title.home')}
            </BreadcrumbItem>

            {currentPath !== 'null' &&
                <BreadcrumbSeparator/>
            }

            {currentPath !== 'null' && pathnames.map((name, index) =>
                <div key={index} className="flex gap-3">
                    <BreadcrumbItem
                        key={index}
                        to={`${pathnames.slice(0, index + 1).join("/")}/`}
                        active={index === pathnames.length - 2}
                    >
                        {name !== '.' ? name : ''}
                    </BreadcrumbItem>
                    {index < pathnames.length - 1 && <BreadcrumbSeparator/>}
                </div>
            )}
        </div>

    </div>
}

function BreadcrumbSeparator() {
    return <span className="text-gray-600">/</span>
}

function BreadcrumbItem({to, active, children}: { to: string, active: boolean, children: ReactNode }) {
    const {setPath} = useCurrentPath()

    const handleSetItem = () => {
        setPath(to, null)
    }

    return <>
        <div
            className={`flex items-center gap-3 text-sm text-gray-600 ${active ? 'hover:text-indigo-500 cursor-pointer' : 'hover:text-gray-800'}`}
            { ...(active ? {onClick: handleSetItem} : {})}
        >
                {children}
        </div>
    </>
}
