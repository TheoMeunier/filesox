import {createContext, ReactNode, useContext, useEffect, useMemo, useState} from "react";
import {User, useUserStore} from "@/stores/useUserStore.ts";
import {jwtDecode} from "jwt-decode";
import {FilePaths} from "@hooks/useLocalStorage.ts";
import axios from "axios";
import {BASE_URL} from "@config/axios.ts";

interface AuthContext {
    token: string | null;
    refreshToken: string | null;
    setToken: (newToken: string | null) => void;
    setRefreshToken: (newRefreshToken: string | null) => void;
    setAllTokens: (token: string, refreshToken: string) => void;
    login: (data: {token: string, refresh_token: string } ) => void;
    logout: () => void;
}

enum AuthEnum {
    TOKEN = 'token',
    REFRESH_TOKEN = 'refresh_token'
}

export const AuthContext = createContext<AuthContext | null>(null)

export const AuthProvider = ({children}: { children: ReactNode }) => {
    const {setUser} = useUserStore()
    const [token, setToken] = useState<string | null>(localStorage.getItem(AuthEnum.TOKEN));
    const [refreshToken, setRefreshToken] = useState<string | null>(localStorage.getItem(AuthEnum.REFRESH_TOKEN));

    const login = async (data: { token: string, refresh_token: string }) => {
        setAllTokens(data.token, data.refresh_token)
    }

    const logout = async () => {
        let response = await axios.post(BASE_URL + "/auth/logout", {refresh_token: refreshToken})

        if (response.status === 200) {
            setToken(null)
            setRefreshToken(null)
            localStorage.clear()
        }
    }

    const setAllTokens = (token: string, refreshToken: string) => {
        setToken(token)
        setRefreshToken(refreshToken)

        localStorage.setItem(AuthEnum.TOKEN, token);
        localStorage.setItem(AuthEnum.REFRESH_TOKEN, refreshToken);

        let user = jwtDecode<User>(token)
        setUser(user)
        localStorage.setItem(FilePaths.path, user!!.file_path);
        localStorage.setItem(FilePaths.id, user!!.path_id);
    }

    useEffect(() => {
        if (token && refreshToken) {
            localStorage.setItem(AuthEnum.TOKEN, token);
            localStorage.setItem(AuthEnum.REFRESH_TOKEN, refreshToken);
        }
    }, [token, refreshToken]);

    const contextValue = useMemo(
        () => ({
            token,
            refreshToken,
            setToken,
            setRefreshToken,
            setAllTokens,
            login,
            logout
        }),
        [token, refreshToken]
    );

    return (
        <AuthContext.Provider value={contextValue}>
            {children}
        </AuthContext.Provider>
    );
}

export const useAuth = () => {
    const context = useContext(AuthContext)
    if (context === null) {
        throw new Error('useAuth must be used within a AuthProvider')
    }
    return context
}
