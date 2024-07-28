import {FilePaths} from "../../hooks/useLocalStorage.ts";
import {createContext, useContext, useEffect, useMemo, useState} from "react";
import {Outlet} from "react-router-dom";

interface CurrentPathContext {
    currentPath: string | null;
    setPath: (path: string) => void;
}

export const CurrentPathContext = createContext<CurrentPathContext | null>(null)

export const CurrentPathProvider = () => {
    const [currentPath, setCurrentPath] = useState<string | null>(localStorage.getItem(FilePaths.path));

    const setPath_ = (path: string) => {
        localStorage.setItem(FilePaths.path, path);
        setCurrentPath(path)
    }

    useEffect(() => {
        const storedPath = localStorage.getItem(FilePaths.path);
        if (storedPath !== currentPath) {
            setCurrentPath(storedPath);
        }
    }, []);

    const contextValue = useMemo(
        () => ({
            currentPath: currentPath,
            setPath: setPath_,
        }),
        [currentPath]
    );

    return <CurrentPathContext.Provider value={contextValue}>
        <Outlet/>
    </CurrentPathContext.Provider>
}

export const useCurrentPath = () => {
    const context = useContext(CurrentPathContext)

    if (!context) {
        throw new Error('useCurrentPath must be used within a CurrentPathProvider')
    }

    return context
}