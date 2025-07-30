import {create} from "zustand";

type Action = {
    setVal: (value: number) => void;
    setUploadLogin: (uploadLogin: boolean) => void;
}

type State = {
    value: number;
    uploadLogin: boolean;
}

export const useProgressBar = create<State & Action>((set) => ({
    value: 0,
    uploadLogin: false,
    setVal: (value) => set({value}),
    setUploadLogin: (uploadLogin) => set({uploadLogin}),
}));