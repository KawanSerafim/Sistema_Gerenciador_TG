import { z } from "zod"

//Schema de validação
export const camposSchema = z.object({
    tema: z.string()
        .min(1, "O tema é obrigatório"),
    tipoTG: z.string()
        .min(1, "O Tipo de TG é um campo obrigatório"),
    integrantes: z.array(z.object({
        // Aceita string ou number
        id: z.union([z.string(), z.number()]),
        nome: z.string()
    })).min(1, "Adicione pelo menos um integrante ao grupo.")
})