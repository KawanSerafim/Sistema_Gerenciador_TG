import {z} from "zod";

 //Limite de 10MB
const MAX_FILE_SIZE = 10*1024*1024;
//Aceita apenas .xlsx
const ACCEPTED_TYPES = [
    "application/pdf", // .pdf
    "application/msword", // .doc
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
    "application/vnd.oasis.opendocument.text" // .odt
];

export const camposSchema = z.object({
    grupoId: z.coerce.string().min(1, "Erro: Grupo não identificado. Faça login novamente."),
    arquivo: z.any()
        //Valida se foi selecionado
        .refine((files) => files?.length === 1, "O arquivo de TG é obrigatório")
         .refine(
            //Tamanho do arquivo
            (files) => files?.[0]?.size <= MAX_FILE_SIZE,
            "O arquivo é muito grande. O tamanho máximo é 10MB."
        )
        //Formato do arquivo    
        .refine((files) => ACCEPTED_TYPES.includes(files?.[0]?.type), "Formato invalido. Envie apenas PDF, DOC, DOCX, ou ODT.")
})
