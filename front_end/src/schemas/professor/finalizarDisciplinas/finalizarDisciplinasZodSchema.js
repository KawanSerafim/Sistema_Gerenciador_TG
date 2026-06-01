import {z} from "zod";

export const finalizarDisciplinasZodSchema = z.object({
    disciplinasSelecionadas: z.array(z.string()).min(1, "Selecione pelo menos uma disciplina para finalizar.")
})