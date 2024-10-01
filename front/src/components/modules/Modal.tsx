import {getSize, useModal} from "@hooks/useModal.ts";
import {ReactNode} from "react";
import {Button, ButtonIcon} from "./Button.tsx";
import {X} from "lucide-react";
import {useTranslation} from "react-i18next";

export function Modal() {
    const {show, size, content } = useModal();
    const widthModal = getSize(size);

    const renderModalContent = (content: JSX.Element | (() => JSX.Element) | null | undefined) => {
        if (typeof content === 'function') {
            return content();
        }
        return content;
    };

    return <>
        {show && <div id={`modal`} className="fixed inset-0 z-50 overflow-y-auto">
            <div className="fixed inset-0 w-full h-full bg-black opacity-40"></div>
            <div className="flex items-center min-h-screen">
                <div
                    className={`${widthModal} relative w-full p-4 mx-auto bg-white rounded-md shadow-lg transition duration-500 ease-in-out transform`}>
                    {renderModalContent(content)}
                </div>
            </div>
        </div>
        }
    </>
}

export function ModalHeader({children}: { children: ReactNode }) {
    const {closeModal} = useModal()

    return <div className="flex justify-between items-center pb-5">
        {children}

        <ButtonIcon svg={X} title="Close modal" onClick={() => closeModal()}
        >
            <X size={20} strokeWidth={1.75}/>
        </ButtonIcon>
    </div>
}

export function ModalHeaderLogo({color, children}: {color: string, children: ReactNode }) {
    const colorClass = getColor(color)

    return <div className="flex justify-center mt-3">
        <div className={`rounded-full p-6 ${colorClass}`}>
            {children}
        </div>
    </div>
}

export function ModalBody({children}: { children: ReactNode }) {
    return <div>
        {children}
    </div>
}

export function ModalFooter({children}: { children: ReactNode}) {
    const {closeModal} = useModal()
    const {t} = useTranslation()

    return <div className="pt-5">
        <div className="flex justify-end items-center gap-2">
            <Button
                color="white"
                type="button"
                onClick={() => closeModal()}
            >
                {t('button.cancel')}
            </Button>
            {children}
        </div>
    </div>
}

export function ModalFooterButton({children}: { children: ReactNode}) {
    return <div className="pt-5 mb-3">
        <div className="flex justify-center items-center gap-2">
            {children}
        </div>
    </div>
}

function getColor(color: string) {
    switch (color) {
        case 'danger':
            return 'bg-red-100 text-red-500';
        case 'primary':
            return 'bg-indigo-100 text-indigo-500';
        default:
            return 'bg-white hover:bg-gray-100 text-gray-800 border-gray-500';
    }
}