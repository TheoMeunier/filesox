import {z} from "zod";

export const apiProfileSharedSchemaType = z.array(z.object({
        id: z.uuid(),
        path: z.string().max(255),
        expired_at: z.string(),
        created_at: z.string(),
    }))