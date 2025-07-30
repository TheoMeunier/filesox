import {ReactNode} from "react";
import {z} from "zod";

export interface AuthProviderProps {
    children: ReactNode;
}

export interface AuthContextProps {
    token: string | null;
    refreshToken: string | null;
    setToken: (token: string) => void;
    setRefreshToken: (token: string) => void;
    logout: () => void;
}

export const authContextDefault: AuthContextProps = {
    token: null,
    refreshToken: null,
    setToken: () => null,
    setRefreshToken: () => null,
    logout: () => null
}

export const loginSchemaType = z.object({
    token: z.string(),
    refresh_token: z.string()
})

