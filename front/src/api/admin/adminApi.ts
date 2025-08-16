import {useQuery} from "react-query";
import {useAxios} from "@config/axios.ts";
import {adminLogsSchemaType, adminSharesSchemaType} from "@/types/api/adminType.ts";
import {permissionsSchemaType} from "@/types/api/userType.ts";

export function useAdminLogsApi() {
    const API = useAxios()

    const {data, isLoading} = useQuery(
        ['logs'],
        async () => {
            const response = await API.get('/admin/logs')
            return adminLogsSchemaType.parse(response.data)
        },
    );

    return {data, isLoading}
}

export function useAdminSharesApi() {
    const API = useAxios()

    const {data, isLoading} = useQuery(
        ['admin-shares'],
        async () => {
            const response = await API.get('/admin/shares')
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