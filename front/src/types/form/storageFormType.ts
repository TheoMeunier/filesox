import {z} from "zod";

export const editStorageSchema = z.object({
    name: z.string().min(2)
})

export type EditStorageFormFields = z.infer<typeof editStorageSchema>

export const moveStorageSchema = z.object({
    path: z.string().min(2)
})

export type MoveStorageFormFields = z.infer<typeof moveStorageSchema>