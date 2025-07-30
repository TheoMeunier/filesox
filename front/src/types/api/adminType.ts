import {z} from "zod";
import {paginationSchemaType} from "../components/paginationType.ts";

export const adminLogsSchemaType = paginationSchemaType(
    z.array(z.object({
        id: z.number(),
        action: z.string(),
        subject: z.string(),
        created_at: z.string(),
        username: z.string(),
    })),
)

export const adminSharesSchemaType = paginationSchemaType(
    z.array(z.object({
        id: z.string().uuid(),
        path: z.string().max(255),
        username: z.string().max(255),
        expired_at: z.string(),
        created_at: z.string(),
    })),
)