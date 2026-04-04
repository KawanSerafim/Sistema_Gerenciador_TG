import { z } from "zod";
import { validarNome } from "../../../../utils/utils";

// 2. Definição do Schema de Validação
export const professorSchema = z
  .object({
    nome: z
      .string()
      .min(1, "Nome é um campo obrigatório")
      .refine(validarNome, "Nome com caracteres inválidos"),
    matricula: z.string().length(11, "A matrícula tem que ter 11 dígitos"),
    email: z
      .email("Formato de email inválido")
      .min(1, "Email é um campo obrigatório"),
    senha: z.string().min(6, "A senha deve ter no mínimo 6 caracteres"),
    confirmarSenha: z.string().min(1, "Confirme sua senha"),
    cargo: z.string().min(1, "Cargo é um campo obrigatório"), // Garante que não fique no "Selecione..."
  })
  .refine((data) => data.senha === data.confirmarSenha, {
    message: "Senha e Confirmar Senha devem ser iguais",
    path: ["confirmarSenha"],
  });
