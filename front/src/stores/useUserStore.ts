import {create} from "zustand";
import {createJSONStorage, persist} from "zustand/middleware";

export interface User {
    id: number;
    name: string;
    email: string;
    layout: boolean;
    file_path: string;
    path_id: string;
    roles: string[];
}

interface UserState {
    user: User | null | undefined;
    setUser: (user: User | null | undefined) => void;
}

export const useUserStore = create<UserState>()(
    persist(
        (set) => ({
            user: undefined,
            setUser: (user: User | null | undefined) => set({ user })
        }),
        {
            name: 'user-store',
            storage: createJSONStorage(() => localStorage)
        }
    )
);
