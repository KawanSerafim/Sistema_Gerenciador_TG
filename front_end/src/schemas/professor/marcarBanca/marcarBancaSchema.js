import {z} from "zod"

export const camposSchema = z.object({
    grupoId: z.string()
    .min(1, "Selecione um grupo"),

    membros: z.array(
      z.object({
       id: z.union([z.string(), z.number()]), // Aceita o ID do prof (número) ou email do externo (string)
            nome: z.string(),
            tipo: z.string(),
            tipoLabel: z.string().optional(),
            email: z.string().email().optional(), // Opcional, pois professores internos não têm email no array
            telefone: z.string().optional()
        })
    ).min(1, "Adicione pelo menos um membro à banca"),


    // Data DD/MM/AAAA
    data: z.string()
    .regex(/^\d{4}-\d{2}-\d{2}$/, { message: "Selecione uma data válida no calendário" })
    .refine((dataRecebida) => {
      // Garante que é uma data real (bloqueia 2026-02-30, por exemplo)
        const dataObj = new Date(dataRecebida);
        return !isNaN(dataObj.getTime())
    },
    {
      message: "A data informada não existe no calendário.",
    }),

    //Hora HH:MM
    hora: z.string().regex(/^([01]\d|2[0-3]):([0-5]\d)$/, {
      message: "A hora deve estar no formato HH:MM (ex: 14:30 e não pode passar de 23:59)",
    }),
    //Local
    local: z.string()
        .trim().min(5, "O local deve ter pelo menos 5 caracteres (Ex: Sala 111 ou Teams)")
})
    