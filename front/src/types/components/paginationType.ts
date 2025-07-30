import {z} from "zod";

export const paginationSchemaType = <T extends z.ZodTypeAny>(dataSchema: T) => z.object({
    total: z.number(),
    total_pages: z.number(),
    current_page: z.number(),
    per_page: z.number(),
    from: z.number(),
    to: z.number(),
    data: dataSchema,
});