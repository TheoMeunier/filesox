import {FilePaths} from "@hooks/useLocalStorage.ts";
import {createContext, useContext, useEffect, useMemo, useState} from "react";
import {Outlet} from "react-router-dom";

interface CurrentPathContext {
    currentPath: string | null;
    currentPathId: string | null;
    setPath: (path: string | null, parent_id: string | null) => void;
}

export const CurrentPathContext = createContext<CurrentPathContext | null>(null)

export const CurrentPathProvider = () => {
    const [currentPath, setCurrentPath] = useState<string | null>(localStorage.getItem(FilePaths.path));
    const [currentPathId, setCurrentPathId] = useState<string | null>(localStorage.getItem(FilePaths.id));

    const setPath_ = (path: string | null, path_id: string | null) => {
        localStorage.setItem(FilePaths.path, path ?? 'null');
        localStorage.setItem(FilePaths.id, path_id ?? 'null');
        setCurrentPath(path ?? null)
        setCurrentPathId(path_id ?? null)
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
            currentPathId: currentPathId,
            setPath: setPath_,
        }),
        [currentPath, currentPathId]
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