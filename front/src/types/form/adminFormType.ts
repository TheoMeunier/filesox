import {z} from "zod";

// Select multiple
const selectValueSchema = z.object({
    value: z.string(),
    label: z.string(),
    disabled: z.boolean().optional(),
    isSelected: z.boolean().optional()
});

export const adminUserCreateSchema = z.object({
    name: z.string().min(3),
    email: z.string().email(),
    password: z.string().min(8),
    file_path: z.string().nullable(),
    permissions: z.array(selectValueSchema),
})

export type AdminUserCreateFormFields = z.infer<typeof adminUserCreateSchema>

export const adminUserEditSchema = z.object({
    name: z.string().min(3),
    email: z.string().email(),
    file_path: z.string().nullable(),
    permissions: z.array(selectValueSchema),
})

export type AdminUserEditFormFields = z.infer<typeof adminUserEditSchema>
