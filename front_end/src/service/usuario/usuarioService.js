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
      urlBase = "/aluno";
    } else if (tipoUsuario === "professor") {
      urlBase = "/professor";
    }

    // Remove o confirmarSenha antes de enviar, dado que não faz parte da requisição
    const { confirmarSenha, ...dadosParaEnvio } = payload;
    return await apiClient(`${urlBase}/cadastro`, {
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
};
