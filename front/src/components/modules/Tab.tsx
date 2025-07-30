import {ReactNode} from "react";
import {Link, useLocation} from "react-router-dom";

export function Tab({children}: { children: ReactNode }) {
    return (
        <div className="mx-auto p-4 md:px-8">
            {children}
        </div>
    );
}

export function TabItems({children}: { children: ReactNode }) {
    return <div className="w-full border-b flex items-center gap-x-3 overflow-x-auto text-sm">
        {children}
    </div>
}

export function TabItem({children, link}: { children: ReactNode, link: string }) {
    const location = useLocation();
    const isActive = location.pathname === link ;

    return <>
        <Link to={link} className={`hover:border-indigo-600 hover:text-indigo-600 border-b py-2 ${isActive ? 'border-indigo-600 text-indigo-600 border-b' : 'border-white text-gray-500'}`}>
            <div
                className="flex items-center gap-x-2 py-1.5 px-3 rounded-lg duration-150 group-hover:text-indigo-600 group-hover:bg-gray-50 group-active:bg-gray-100 font-medium">
                {children}
            </div>
        </Link>
    </>
}