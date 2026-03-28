import {z} from 'zod';

 //Limite de 5MB
const MAX_FILE_SIZE = 5*1024*1024;
//Aceita apenas .xlsx
const ACCEPTED_TYPES = [
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
];

export const enviarTurmaSchema = z.object({
    //Select de turmas retorna um id
    turmaId: z.string().nonempty("Selecione uma turma para continuar"),

    // O input type="file" retorna um FileList do navegador
    arquivo: z.any()
        //Verifique se foi enviado um arquivo
        .refine((files)=> files?.length === 1, "A planilha de alunos é obrigatória.")
        .refine(
            //Tamanho do arquivo
            (files) => files?.[0]?.size <= MAX_FILE_SIZE,
            "O arquivo é muito grande. O tamanho máximo é 5MB."
        )
        .refine(
            //Verfica o tipo do arquivo
            (files) => ACCEPTED_TYPES.includes(files?.[0].type), "Formato inválido. Envie apenas planilhas Excel (.xlsx)"
        )
        

})

