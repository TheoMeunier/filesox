import { z } from 'zod';

export const adminLogsSchemaType = z.array(
  z.object({
    id: z.uuid(),
    action: z.string(),
    details: z.string(),
    created_at: z.string(),
    username: z.string(),
  })
);

export const adminSharesSchemaType = z.array(
  z.object({
    id: z.uuid(),
    path: z.string().max(255),
    username: z.string().max(255),
    expired_at: z.string(),
    created_at: z.string(),
  })
);
