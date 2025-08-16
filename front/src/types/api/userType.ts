import {z} from "zod";

export interface UserType {
    id: string;
    name: string;
    email: string;
    created_at: string;
    permissions: string[];
    file_path: string | null;
    layout?: boolean;
}

export interface UpdatePasswordProfileType {
    password: string;
    confirm_password: string;
}

export interface UpdateProfileType {
    email: string;
    name: string;
    layout: boolean;
}

export interface CreateUserType {
    name: string,
    email: string,
    file_path: string,
    password: string,
}

export interface UpdateUserType {
    id: number,
    name: string,
    email: string,
    file_path: string
}

export interface PermissionType {
    id: number;
    name: string;
}

// schema zod
export const usersSchemaType = z.array(z.object({
    id: z.string().uuid(),
    name: z.string(),
    email: z.string(),
    //file_path: z.string().nullable(),
    created_at: z.string(),
    permissions: z.array(z.string()),
}))

export const logsProfileSchemaType =   z.array(z.object({
        id: z.string().uuid(),
        action: z.string(),
        details: z.string(),
        created_at: z.string(),
        username: z.string().nullable(),
    }))

export const profileSchemaType = z.object({
    id: z.number(),
    name: z.string(),
    email: z.string(),
    file_path: z.string().nullable(),
    permissions: z.array(z.string()),
})

export const permissionsSchemaType = z.array(z.object({
    id: z.string().uuid(),
    name: z.string(),
}))
