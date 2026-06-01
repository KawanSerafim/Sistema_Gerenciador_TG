import { obrigatorio } from "../../utils/utils";
import { apiClient } from "../apiClient";
export const usuarioService = {

  /**
   * Cadastra um novo usuario
   * @param {Object} payload - Objeto contendo {email, senha}
   * @param {string} tipoUsuario - string com o tipo do usuario (aluno ou professor)
   * @return {Promise<any>} Dados do usuario criado
   */
  cadastrarUsuario: async (
    payload = obrigatorio("payload"),
    tipoUsuario = obrigatorio("tipoUsuario"),
  ) => {
    let urlBase = "";
    if (tipoUsuario === "aluno") {
      urlBase = "/alunos";
    } else if (tipoUsuario === "professor") {
      urlBase = "/professores";
    }

    // Remove o confirmarSenha antes de enviar, dado que não faz parte da requisição
    const { _confirmarSenha, ...dadosParaEnvio } = payload;
    return await apiClient(`${urlBase}`, {
      method: "POST",
      body: JSON.stringify(dadosParaEnvio),
    });
  },

  /**
   * * Busca um usuario pelo seu ID único
   * @param {string} id - ID do usuario (UUID)
   * @return {Promise<any>} Dados da respota da requisição
   */

  buscarPorId: async (id) => {
    return await apiClient(`/usuarios/${id}`, {
      method: "GET",
    });
  },

  /**
   * Busca um usuário pelo email
   * @param {string} email
   * @return {Promise<any>} Dados da respota da requisição
   */
  buscaPorEmail: async (email) => {
    //encondedURIComponent para evitar que o '@' quebre a URL
    const emailSeguro = encodeURIComponent(email);
    return await apiClient(`/usuarios/${emailSeguro}`, {
      method: "GET",
    });
  },

  // ========================================================================
  // Funções do fluxo de recuperação de senha
  // ========================================================================

  /**
   * Solicita a recuperação de senha enviando um código de verificação para o e-mail
   * @param {string} email - Email cadastrado na conta
   * @return {Promise<void>} Sem retorno (204 No Content) em caso de sucesso
   */
  solicitarRecuperacaoSenha: async (email = obrigatorio("email")) => {
    return await apiClient(`/conta-usuario/senha/solicitar-recuperacao`, {
      method: "POST",
      body: JSON.stringify({ email }),
    });
  },

  /**
   * Redefine a senha utilizando o código OTP enviado por e-mail
   * @param {Object} payload - Objeto contendo {email, codigo, novaSenha}
   * @return {Promise<void>} Sem retorno (204 No Content) em caso de sucesso
   */
  redefinirSenha: async (payload = obrigatorio("payload")) => {
    return await apiClient(`/conta-usuario/senha/redefinir`, {
      method: "POST",
      body: JSON.stringify(payload),
    });
  }

};
