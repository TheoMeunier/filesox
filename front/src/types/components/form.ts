import {FormEvent, ReactNode} from "react";

export interface LabelProps {
    children: ReactNode,
    htmlFor: string
}

export interface FormFieldsProps {
    children: ReactNode,
    onSubmit: (event: FormEvent<HTMLElement>) => void
    id?: string
}