import {useModalStore} from "../stores/useModalStore.ts";

export function getSize(size: string): string {
    let widthModal = "sm:max-w-md md:max-w-xl lg:max-w-2xl"

    switch (size) {
        case 'sm':
            widthModal = "sm:max-w-sm"
            break;
        case 'md':
            widthModal = "sm:max-w-md"
            break;
        case 'lg':
            widthModal = "sm:max-w-md md:max-w-lg"
            break;
        case 'xl':
            widthModal = "sm:max-w-md md:max-w-xl"
            break;
        case '3xl':
            widthModal = "sm:max-w-md md:max-w-xl lg:max-w-3xl"
            break;
        case '4xl':
            widthModal = "sm:max-w-md md:max-w-xl lg:max-w-3xl xl:max-w-4xl"
            break;
        case '5xl':
            widthModal = "sm:max-w-md md:max-w-xl lg:max-w-3xl xl:max-w-5xl"
            break;
        case '6xl':
            widthModal = "sm:max-w-md md:max-w-xl lg:max-w-3xl xl:max-w-5xl 2xl:max-w-6xl"
            break;
        case '7xl':
            widthModal = "sm:max-w-md md:max-w-xl lg:max-w-3xl xl:max-w-5xl 2xl:max-w-7xl"
            break;
    }

    return widthModal
}

export function useModal() {
    const {show, size, content, openModal, closeModal} = useModalStore();
    return {show, size, content, openModal, closeModal};
}