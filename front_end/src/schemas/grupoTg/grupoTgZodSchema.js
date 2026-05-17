import { z } from "zod";

export const vincularCoorientadorSchema = z.object({
    nome: z.string()
        .min(3, "O nome deve ter pelo menos 3 caracteres"),
    origem: z.string()
        .min(2, "A origem/empresa é obrigatória")
});