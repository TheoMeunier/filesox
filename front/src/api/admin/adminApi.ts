import {useQuery} from "react-query";
import {useAxios} from "@config/axios.ts";
import {adminLogsSchemaType, adminSharesSchemaType} from "@/types/api/adminType.ts";
import {permissionsSchemaType} from "@/types/api/userType.ts";

export function useAdminLogsApi(page: number) {
    const API = useAxios()

    const {data, isLoading} = useQuery(
        ['logs', page],
        async () => {
            const response = await API.get('/admin/logs?page=' + page)
            return adminLogsSchemaType.parse(response.data)
        },
    );

    return {data, isLoading}
}

export function useAdminSharesApi(page: number) {
    const API = useAxios()

    const {data, isLoading} = useQuery(
        ['shares', page],
        async () => {
            const response = await API.get('/admin/shares?page=' + page)
            return adminSharesSchemaType.parse(response.data)
        },
    );

    return {data, isLoading}
}

export function useAdminPermissionsApi() {
    const API = useAxios()

    const {isLoading, data: permissions} = useQuery(
        'permissions',
        async () => {
            const response = await API.get('/admin/permissions')
            return permissionsSchemaType.parse(response.data)
        },
        {
            refetchOnWindowFocus: false
        })

    return {permissions, isLoading}
}