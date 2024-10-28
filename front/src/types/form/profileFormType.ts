import {z} from "zod";

export const profileEditSchema = z.object({
    email: z.string().email(),
    name: z.string().min(3)
})

export type ProfileEditFormFields = z.infer<typeof profileEditSchema>

export const profileEditPasswordSchema = z.object({
    password: z.string().min(8),
    confirm_password: z.string().min(8)
}).refine(
    data => data.password === data.confirm_password,
    {
        message: 'Passwords must match',
        path: ['password_confirmation']
    }
)

export type ProfileEditPasswordFormFields = z.infer<typeof profileEditPasswordSchema>