import { z } from "zod";

//Schema de validação
export const cursoSchema = z.object({
    nome: z.string()
        .min(1, "Nome do curso é um campo obrigatório"),

    turno: z.array(z.string()).min(1, "Selecione pelo menos um turno"),
    disciplina: z.array(z.string()).min(1, "Selecione pelo menos uma disciplina"),
    coordenador: z.string().min(1, "Selecione pelo menos um tipo coordenador"),
    tiposTG: z.array(z.object({
        id: z.string(),
        label: z.string(),
        ativo: z.boolean(),
        qntMax: z.coerce.number().min(0).optional(),
    }))
        .superRefine((tgs, ctx) => {
            //ctx = contexto, permite enviar mais de 1 msg de erro
            // Filtra apenas os ativos para fazer as validações
            const ativos = tgs.filter(tg => tg.ativo);

            // Se nenhum estiver ativo, lança o erro
            if (ativos.length === 0) {
                ctx.addIssue({
                    code: z.custom,
                    message: "Selecione pelo menos um tipo de trabalho de conclusão (TG)",
                    //Caminho para o acesso ao root para exibição do errro
                    path: []
                });
                return; // Se não tem nenhum, nem adianta verificar as quantidades
            }

            // 3. Se tem ativos, verifica se todos têm quantidade > 0
            const temQuantidadeZerada = ativos.some(tg => !tg.qntMax || tg.qntMax <= 0);
            if (temQuantidadeZerada) {
                ctx.addIssue({
                    code: z.custom,
                    message: "Informe a quantidade máxima (maior que zero) para os TGs selecionados",
                    //Parametro para o acesso ao root para exibição do errro
                    path: []
                });
            }
        })
        // 4. Só DEPOIS que tudo está validado, transformamos para o backend!
        .transform(tgs => tgs.filter(tg => tg.ativo))
})
