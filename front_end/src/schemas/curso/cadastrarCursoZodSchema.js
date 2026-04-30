import { z } from "zod";

//Schema de validação
export const cursoSchema = z.object({
    nome: z.string()
        .min(1, "Nome do curso é um campo obrigatório"),

    turno: z.array(z.string()).min(1, "Selecione pelo menos um turno"),
    disciplina: z.array(z.string()).min(1, "Selecione pelo menos uma disciplina"),
    coordenador: z.string()
        .length(13, "A matrícula deve ter exatamente 13 dígitos")
        .regex(/^\d+$/, "A matrícula deve conter apenas números"),

    tiposTG: z.array(z.object({
        id: z.string(),
        label: z.string(),
        ativo: z.boolean(),
        qntMax: z.coerce.number().optional(),
    }))
        .superRefine((tgs, ctx) => {
            //ctx = contexto, permite enviar mais de 1 msg de erro
            // Filtra apenas os ativos para fazer as validações
            const ativos = tgs.filter(tg => tg.ativo);

            // Se nenhum estiver ativo, lança o erro
            if (ativos.length === 0) {
                ctx.addIssue({
                    code: z.custom,
                    message: "Selecione pelo menos um tipo de trabalho de graduação (TG)",
                    //Caminho para o acesso ao root para exibição do errro
                    path: []
                });
                return; // Se não tem nenhum, nem adianta verificar as quantidades
            }
            // Validação 2: Verifica as quantidades INDIVIDUALMENTE (O pulo do gato)
            tgs.forEach((tg, index) => {
                // Só validamos a quantidade se o checkbox estiver marcado
                if (tg.ativo) {
                    if (!tg.qntMax || tg.qntMax < 1) {
                        ctx.addIssue({
                            code: z.custom,
                            message: "A quantidade deve ser no mínimo 1",
                            // Isso injeta o erro especificamente em errors.tiposTG[index].qntMax
                            path: [index, "qntMax"]
                        });
                    }
                }
            });
        })
})
    .transform((dados) => ({
        nome: dados.nome,
        turnos: dados.turno,
        disciplinas: dados.disciplina,
        matriculaCoordenador: dados.coordenador,
        ajustes: dados.tiposTG
            .filter(tg => tg.ativo)
            .map(tg => ({
                tipoTg: tg.label,
                maxAlunosGrupo: tg.qntMax
            }))
    }));