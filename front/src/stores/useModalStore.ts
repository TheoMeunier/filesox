import {create} from "zustand";

type Action = {
    openModal: (content: JSX.Element | (() => JSX.Element), size?: "sm" | "md" | "lg" | "xl" | "2xl" | "3xl" | "4xl" | "5xl" | "6xl" | "7xl" | "8xl" | "9xl" | "full") => void;
    closeModal: () => void;
}

type State = {
    show: boolean;
    content: JSX.Element | (() => JSX.Element) | null;
    size: "sm" | "md" | "lg" | "xl" | "2xl" | "3xl" | "4xl" | "5xl" | "6xl" | "7xl" | "8xl" | "9xl" | "full";
}

export const useModalStore = create<State & Action>((set) => ({
    show: false,
    content: null,
    size: "2xl",
    openModal: (content, size) => {
        set({show: true, content, size});
    },
    closeModal: () => {
        set({show: false, content: null});
    }
}));