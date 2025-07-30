export enum FilePaths {
    path = "FILE_PATH_AUTH",
    id = "FILE_PATH_ID"
}

export function useLocalStorage() {
    const getItem = (key: string) => {
       return localStorage.getItem(key);
    }

    const setItem = (key: string, value: any) => {
        localStorage.setItem(key, value);
    }

    return {getItem, setItem};
}