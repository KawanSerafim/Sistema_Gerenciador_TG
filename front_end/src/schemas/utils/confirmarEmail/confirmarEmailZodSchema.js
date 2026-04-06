import { z } from "zod";

//Schema de validação
export const camposSchema = z.object({
    codigo: z.string().min(1, "Codigo é um campo obrigatório")
})