import {useFileStore} from "../stores/useFileStore.ts";

export function useStore() {
    const {activeStorage, setActiveStorage} = useFileStore();
    
    return {
        activeStorage,
        setActiveStorage
    };
}

export function truncateString(str: string, num: number) {
    if (str.length <= num) {
        return str
    }
    return str.slice(0, num) + '...'
}