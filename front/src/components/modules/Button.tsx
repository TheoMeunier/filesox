import {ComponentType, ReactNode} from "react";

export function ButtonIcon({svg: SvgComponent, title, ...props}: { svg: ComponentType<any>, title: string, [key: string]: any  }) {
    return <button
        title={title}
        className="flex items-center justify-center w-10 h-10 rounded-md hover:bg-gray-100"
        {...props}
    >
        <SvgComponent size={20} strokeWidth={1.75}/>
    </button>
}

export function Button({children, color, ...props}: { children: ReactNode, color: string, [key: string]: any }) {
    const colorClass = getColor(color)

    return <button
        className={`${colorClass} flex items-center text-sm py-2 px-4 rounded`}
        {...props}
    >
        {children}
    </button>
}

export function ButtonBig({children, color, ...props}: { children: ReactNode, color: string, [key: string]: any }) {
    const colorClass = getColor(color)

    return <button
        className={`${colorClass} flex items-center text-md py-3 px-10 rounded`}
        {...props}
    >
        {children}
    </button>
}

function getColor(color: string) {
    switch (color) {
        case 'danger':
            return 'bg-red-500 hover:bg-red-600 text-white border-red-500';
        case 'success':
            return 'bg-green-500 hover:bg-green-600 text-white border-green-500';
        case 'primary':
            return 'bg-indigo-500 hover:bg-indigo-600 text-white border-indigo-500';
        case 'white':
            return 'bg-white hover:bg-gray-100 text-gray-800 border border-gray-200';
        default:
            return 'bg-white hover:bg-gray-100 text-gray-800 border-gray-500';
    }
}