import { z } from "zod";
import { validarNome } from "../../../utils/utils";
validarNome;

// O schema base para usuario
const usuarioSchema = z.object({
  email: z
    .email("Formato de email inválido")
    .min(1, "Email é um campo obrigatório"),
  senha: z.string().min(6, "A senha deve ter no mínimo 6 caracteres"),
  confirmarSenha: z.string().min(1, "Confirme sua senha"),
});

// Schema especifico do professor para cadastro
export const professorSchema = usuarioSchema
  .extend({
    nome: z
      .string()
      .min(1, "Nome é um campo obrigatório")
      .refine(validarNome, "Nome com caracteres inválidos"),
    matricula: z.string().length(13, "A matrícula tem que ter 13 dígitos"),
    cargo: z.string().min(1, "Cargo é um campo obrigatório"), // Garante que não fique no "Selecione..."
  })
  //Tratamento senha e confirmar senha
  .refine((data) => data.senha === data.confirmarSenha, {
    message: "Senha e Confirmar Senha devem ser iguais",
    path: ["confirmarSenha"],
  });

//Schema especifico de aluno para cadastro
export const alunoSchema = usuarioSchema
  .extend({
    nome: z
      .string()
      .min(1, "Nome é um campo obrigatório")
      .refine(validarNome, "Nome com caracteres invalidos"),
    matricula: z.string().length(13, "A matricula tem que ter 13 dígitos"),
    telefone: z.string().length(11, "Telefone inválido"),
    // Array de Redes
    redes: z.array(
      z.object({
        rede: z.string(),
        url: z.url(
          "Formato de URL inválido. Insira o link completo (http://...)",
        ),
      }),
    ),
  }) //Tratamento senha e confirmar senha
  .refine((data) => data.senha === data.confirmarSenha, {
    message: "Senha e Confirmar Senha devem ser iguais",
    path: ["confirmarSenha"],
  });


//Schema para login
export const loginSchema = z.object({
  email: z
    .email("Formato de email invalido")
    .min(1, "O email é obrigatório"),
  senha: z
    .string()
    .min(1, "A senha é campo obrigatório")
})

//Schemas para a recuperacao senha
// Schema para a Etapa 1
export const solicitarRecuperacaoSchema = z.object({
  email: z
    .email("Digite um e-mail válido")
    .min(1, "O e-mail é obrigatório")
});

// Schema para a Etapa 2
export const redefinirSenhaSchema = z.object({
  codigo: z.string().length(6, "O código deve ter exatamente 6 caracteres"),
  novaSenha: z.string().min(5, "A nova senha deve ter no mínimo 5 caracteres")
});