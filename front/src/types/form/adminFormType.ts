import { z } from 'zod';

export const adminUserCreateSchema = z.object({
  name: z.string().min(3),
  email: z.email(),
  password: z.string().min(8),
  file_path: z.string().min(1),
  permissions: z.array(z.uuid()),
});

export type AdminUserCreateFormFields = z.infer<typeof adminUserCreateSchema>;

export const adminUserEditSchema = z.object({
  name: z.string().min(3),
  email: z.email(),
  file_path: z.string().nullable(),
  permissions: z.array(z.uuid()),
});

export type AdminUserEditFormFields = z.infer<typeof adminUserEditSchema>;
