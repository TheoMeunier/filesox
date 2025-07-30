import {paginationSchemaType} from "../components/paginationType.ts";
import {z} from "zod";

export const apiProfileSharedSchemaType = paginationSchemaType(
    z.array(z.object({
        id: z.string().uuid(),
        path: z.string().max(255),
        expired_at: z.string(),
        created_at: z.string(),
    })),
)