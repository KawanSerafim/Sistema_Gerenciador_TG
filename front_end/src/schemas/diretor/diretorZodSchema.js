import z from 'zod'
export const diretorZodSchema = z.object({
    matriculaProfessor: z.string().min(1, "Selecione um professor da lista."),
    dataInicio: z.string().min(1, "A data de início é obrigatória."),
    dataFim: z.string().optional(),
    assinaturaBase64: z.string().min(1, "O upload da imagem de assinatura é obrigatório.")
});