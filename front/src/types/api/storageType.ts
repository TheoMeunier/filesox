import {z} from "zod";

export interface FileType {
    id: string;
    name: string;
    type: string;
    parent_id?: string;
    size: number;
    icon: string;
}

export interface FolderType {
    id: string;
    path: string;
    parent_id?: string;
}

export type ActiveStorageType<T> = T | null;

export const ListModalShareSchemaType = z.array(
    z.object({
        id: z.string().uuid(),
        expired_at: z.string(),
        created_at: z.string(),
    })
);