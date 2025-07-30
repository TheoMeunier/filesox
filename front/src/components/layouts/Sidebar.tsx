import {ComponentType, MouseEventHandler, ReactNode} from "react";
import {Link, useLocation} from "react-router-dom";
import {ChevronLeft} from "lucide-react";

export function Sidebar({children, sidebarOpen, setter}: { children: ReactNode, sidebarOpen: boolean, setter: (value: boolean) => void}) {
    return <>
        <aside
            className={`bg-white ${sidebarOpen ? 'translate-x-0' : ''} fixed  z-20 h-full top-0 left-0 pt-16 lg:flex flex-shrink-0 flex-col w-64  transition-transform -translate-x-full xl:translate-x-0`}>
            <div className="relative flex-1 flex flex-col justify-between  min-h-full border-r border-gray-200 bg-white pt-0">
                    {children}

                <button
                    className={`absolute top-4 -right-4 bg-indigo-500 rounded-full p-1 text-white cursor-pointer ${sidebarOpen ? 'lg:hidden' : 'hidden'}`}
                    onClick={() => setter(false)}
                >
                    <ChevronLeft size={20}/>
                </button>
            </div>
        </aside>
    </>
}

export function SidebarMenu({children}: { children: ReactNode }) {
    return <>
        <ol className="space-y-2 mt-4 mb-12 font-medium">{children}</ol>
    </>
}

export function SidebarMenuContent({children}: { children: ReactNode }) {
    return <>
        <div>{children}</div>
    </>
}

export function SidebarTitleMenu({children}: { children: ReactNode }) {
    return <>
        <li className="pl-4 text-gray-400 text-sm mb-2">{children}</li>
    </>
}

export function SidebarMenuItem({svg: SvgComponent, children, href, active, onClick}: {
    svg: ComponentType<any>,
    children: ReactNode,
    href?: string,
    active?: string,
    onClick?: MouseEventHandler<HTMLButtonElement> | undefined
}) {
    const location = useLocation();
    let isActive;

    if (active && location.pathname === href) {
        isActive = 'border-indigo-700 text-indigo-700 bg-indigo-50';
    } else if (active && location.pathname.startsWith(href!) && href !== '/') {
        isActive = 'border-indigo-700 text-indigo-700 bg-indigo-50';
    } else {
        isActive = 'text-gray-600 border-transparent';
    }

    return <>
        <li className={`${isActive}  pr-2 py-2 text-sm hover:text-indigo-700 border-l-4  hover:border-indigo-700 hover:bg-indigo-50`}>
            {href === undefined ?
                <button
                    className="flex items-center gap-2 pl-3"
                    onClick={onClick}>
                    <SvgComponent size={20} strokeWidth={1.75} />
                    {children}
                </button>
                :
                <Link to={href} className="flex items-center gap-2 pl-3">
                    <SvgComponent size={20} strokeWidth={1.75} />
                    {children}
                </Link>
            }
        </li>
    </>
}

export function SidebarItemVersion({children}: { children: ReactNode }) {
    return <>
        <li className="text-center text-gray-400 text-sm mb-6">{children}</li>
    </>
}