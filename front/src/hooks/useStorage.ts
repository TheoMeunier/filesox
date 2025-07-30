import {useFileStore} from "../stores/useFileStore.ts";

export const useStorage = () => {
    const {activeStorage} = useFileStore()

    const getPath = (path: string, userPath: string, currentPath: string) => {
        currentPath === 'null' ? currentPath = '' : currentPath

        if (typeof(userPath) === 'object' || userPath === null || userPath == 'null') {
            userPath = ''
        }

        return userPath + currentPath + path
    }

    const isFolder = () => {
        if (activeStorage && 'name' in  activeStorage && activeStorage.name) {
            return false
        } else if (activeStorage && 'path' in  activeStorage && activeStorage.path) {
            return true
        }
    }

    const getPathOrName = () => {
        if (activeStorage && 'name' in  activeStorage && activeStorage.name) {
            return activeStorage.name
        } else if (activeStorage && 'path' in  activeStorage && activeStorage.path) {
            return activeStorage.path
        }
    }

    const getFolderName = (path: string) => {
        return path.split('/').reverse()[1]
    }

    const getNewPath = (path: string, formData: string, name? : string) => {
        const isFolder = !name
        const lastFolder = path.split('/').reverse()[1]

        if (isFolder) {
            return formData === './' ? lastFolder + '/' : formData + '/' + path
        } else {
            return formData === './' ? name : formData + '/' + name
        }
    }

    return {isFolder, getPath, getFolderName, getPathOrName, getNewPath}
}