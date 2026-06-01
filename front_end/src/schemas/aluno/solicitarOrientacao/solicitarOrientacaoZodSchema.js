import {z} from 'zod';

export const solicitarOrientacaoZodSchema = z.object({
  orientadorId: z.coerce.string().min(1, 'Por favo, selecione um orientador da lista')
});