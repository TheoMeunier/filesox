
export interface ModalContextProps {
    showModal: boolean;
    modalContent: any;
    modalSize: string;
    openModal: (content: () => JSX.Element, size?: string) => void;
    closeModal: () => void;
}

export interface ModalProviderProps {
    children: any;
}