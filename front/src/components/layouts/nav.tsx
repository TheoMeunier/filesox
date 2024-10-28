import {ReactNode} from "react";

export function Navbar({children}: {children: ReactNode}) {
    return <>
        <nav className="bg-white border-b border-gray-200 fixed z-30 w-full">
            {children}
        </nav>
    </>
}

export function NavItems({children}: { children: ReactNode}) {
    return <div className="w-full mx-auto">
        <div className="flex items-center justify-between h-16">
            {children}
        </div>
    </div>
}

export function NavItemsLeft({children}: {children: ReactNode}) {
    return <div className="flex gap-2 items-center pl-2">
        {children}
    </div>
}

export function NavItemsRight({children}: {children: ReactNode}) {
    return <div className="flex gap-2 items-center pr-4">
        {children}
    </div>
}

export function NavItem({children}: {children: ReactNode}) {
    return <div className="flex items-center">
        {children}
    </div>
}