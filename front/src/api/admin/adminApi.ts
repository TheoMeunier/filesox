import {useAxios} from "@config/axios.ts";
import {adminLogsSchemaType, adminSharesSchemaType} from "@/types/api/adminType.ts";
import {permissionsSchemaType} from "@/types/api/userType.ts";
import {useQuery} from "@tanstack/react-query";

export function useAdminLogsApi() {
    const API = useAxios()

    const {data, isLoading} = useQuery({
        queryKey: ['admin-logs'],
        queryFn:  async () => {
            const response = await API.get('/admin/logs')
            return adminLogsSchemaType.parse(response.data)
        },
    })

    return {data, isLoading}
}

export function useAdminSharesApi() {
    const API = useAxios()

    const {data, isLoading} = useQuery({
        queryKey: ['admin-shares'],
        queryFn:  async () => {
            const response = await API.get('/admin/shares')
            return adminSharesSchemaType.parse(response.data)
        },
    });

    return {data, isLoading}
}

export function useAdminPermissionsApi() {
    const API = useAxios()

    const {isLoading, data: permissions} = useQuery({
        queryKey: ['admin-permissions'],
        queryFn:  async () => {
            const response = await API.get('/admin/permissions')
            return permissionsSchemaType.parse(response.data)
        },
        refetchOnWindowFocus: false
    })

    return {permissions, isLoading}
}