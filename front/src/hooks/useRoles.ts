import {RoleEnum} from "../types/enum/RoleEnum.ts";
import {PermissionType} from "../types/api/userType.ts";
import {Permissions} from "@/types/types/rolesTypes.ts";

export function useRoles() {
    const getPermissionsValue = (
        permissions: PermissionType[],
        userPermissions: string[]): Permissions | undefined =>
    {
        return permissions
            .filter(permission => userPermissions.includes(permission.name))
            .map(permission => ({ label: permission.name, value: permission.id.toString(), disabled: false }));
    }

    const role = (permissions: string[], userPermissions : string[]) => {
        if (userPermissions.includes(RoleEnum.ADMIN)) {
            return true
        }

        for (let i = 0; i < permissions.length; i++) {
            if (userPermissions.includes(permissions[i])) {
                return true
            }
        }

        return false
    }

    return {getPermissionsValue, role}
}