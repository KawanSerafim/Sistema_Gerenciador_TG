import { z} from "zod"

// Controlador do ano minimo 
const anoAtual = new Date().getFullYear();

//Schema
export const camposSchema = z.object({
    ano: z.coerce.number()
        .min(anoAtual, `O ano minimo é ${anoAtual}`)
        .max( anoAtual + 2, `O ano limite é ${anoAtual + 2}`)
        .refine((ano) => ano.toString().length === 4, "O ano deve ter 4 dígitos."),

    semestre: z.string()
        .min(1, "Selecione o semestre"),

    // Objeto dinâmico onde as chaves serão "Disciplina-Turno" e o valor o Nome do Prof.
    turmas: z.record(z.string(), z.string().min(1, "Obigatório"))
    })
    .superRefine((dados, ctx) => {
    //Pega todas as opções de disciplinas e turnos do curso
    const disciplinas = ["TG1", "TG2"];
    const turnos = ["Noite", "Tarde", "Manhã"];

    // Valida se todas as combinaçõẽs existem no objeto
    turnos.forEach(t => {
        disciplinas.forEach(d => {
            const chave = `${d}-${t}`;
            if (!dados.turmas[chave]) {
                ctx.addIssue({
                    code: z.custom(),
                    message: "Selecione professores para todas as turmas.",
                    //Atrela o erro ao campo "turmas"
                    path: ["turmas"]
                })
                return;
            }
        });
    });
})
