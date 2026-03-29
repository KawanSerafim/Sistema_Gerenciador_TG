import {z} from "zod"

export const camposSchema = z.object({
    idGrupo: z.coerce.string().min(1,),
    nota: z.coerce.number({
        invalid_type_error: "A nota deve ser um número",
    })
        .min(0,"Notas não podem ser numeros negativos")
        .max(10, "A nota limite é 10")
})