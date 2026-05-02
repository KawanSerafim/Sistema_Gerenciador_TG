import { z } from "zod"

// Controlador do ano minimo 
const anoAtual = new Date().getFullYear();

//Schema
export const camposSchema = z.object({
    ano: z.coerce.number()
        .min(anoAtual, `O ano minimo é ${anoAtual}`)
        .max(anoAtual + 2, `O ano limite é ${anoAtual + 2}`)
        .refine((ano) => ano.toString().length === 4, "O ano deve ter 4 dígitos."),

    semestre: z.string()
        .min(1, "Selecione o semestre"),

    // Objeto dinâmico onde as chaves serão "Disciplina-Turno" e o valor o Nome do Prof.
    turmas: z.record(z.string(), z.string().min(1, "Obigatório"))
})
    .superRefine((dados, ctx) => {
        //Pega todas as opções de disciplinas e turnos do curso
        const chavesRegistradas = Object.keys(dados.turmas);
        //Se o objeto estiver totalmente vazio (nenhum select na tela ou form intocado)
        if (chavesRegistradas.length == 0) {
            ctx.addIssue({
                code: z.custom(),
                message: "Aguarde o carregamento das turmas ou selecione os professores.",
                path: ["turmas"]
            });
            return;
        }
        //Verifica se algum dos selects que estão na tela ficou sem professor (valor "")
        const existeTurmaSemProf = chavesRegistradas.some(chave => !dados.turmas[chave] || dados.turmas[chave].trim() === "");

        if (existeTurmaSemProf) {
            ctx.addIssue({
                code: z.custom(),
                message: "Selecione professores para todas as turmas.",
                path: ["turmas"] // Joga o erro para o Alert geral no final da página
            })
        }
    })
