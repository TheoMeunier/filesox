import {ComponentType, InputHTMLAttributes, ReactNode} from "react";
import {FormFieldsProps, LabelProps} from "@/types/components/form.ts";

export function FormFields({children, onSubmit, id}: FormFieldsProps) {
    return <form id={id} method='POST' onSubmit={onSubmit}>
        {children}
    </form>
}

export function FormLabel({children, htmlFor}: LabelProps): JSX.Element {
    return <label htmlFor={htmlFor} className="block text-sm font-medium leading-6 text-gray-900">
        {children}
    </label>
}

export function FormDescription({children}: { children: ReactNode }) {
    return <p className="prose text-sm text-gray-700 mt-1 mb-2">
        {children}
    </p>
}

export function FormError({children}: { children: ReactNode }) {
    return <span className="text-red-500">{children}</span>
}

export function FormField({children}: { children: ReactNode }) {
    return <div className="mt-2">
        {children}
    </div>
}

export function FormButton({children}: { children: ReactNode }) {
    return <button type="submit"
                   className="flex w-full justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600">
        {children}
    </button>
}

export function FormInput({type, ...props }: { type: string } & InputHTMLAttributes<HTMLInputElement>) {

    return <>
        <input
            type={type}
            className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
            {...props}
        />
    </>
}

export function InputIcon({svg: SvgComponent}: { svg: ComponentType<any> }) {
    return <div className="relative">
        <div className="text-gray-400 flex absolute inset-y-0 left-0 items-center pl-3 pointer-events-none">
            <SvgComponent size={20} strokeWidth={1.75}/>
        </div>

        <input
            type="search"
            name="search"
            placeholder="Search"
            className="rounded-md border-gray-300 focus:border-indigo-500 text-sm w-full pl-10"
        />
    </div>
}