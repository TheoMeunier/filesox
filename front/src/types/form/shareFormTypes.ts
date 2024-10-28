import {z} from "zod";

export const shareStorageSchema = z.object({
    duration: z.number().positive(),
    type_duration: z.string(),
    password: z.string().optional()
})

export type ShareStorageFormFields = z.infer<typeof shareStorageSchema>