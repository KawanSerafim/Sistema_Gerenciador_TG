import { z } from "zod"

export const camposSchema = z.object({
    idBanca: z.coerce.string().min(1,),
    notas: z.array(
        z.object({
            nomeMembroBanca: z.string(),
            nota: z.coerce.number({
                invalid_type_error: "A nota deve ser um número"
            })
                .min(0)
                .max(10, "A nota máxima é 10")
        })
    )
})