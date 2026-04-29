import { obrigatorio } from "../../utils/utils";
import { apiClient } from "../apiClient";
import { jwtDecode } from "jwt-decode";

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

  /** 
   *  Realiza o login do usuário (Aluno, Professor)
   * @param {Object} credenciais - Objeto contendo {email, senha}
   * @returns {Promise<any>} Dados do usuário e resultado da operação
   */
  login: async (credenciais) => {
    //Realiza requisição
    const resposta = await apiClient(`/conta-usuario/login`, {
      method: "POST",
      body: JSON.stringify(credenciais),
    });

    //Se não cair no catch, o login foi um sucesso
    //Então irá salvar o token JWT no local storage
    if (resposta && resposta.token) {
      localStorage.setItem("meu_token_tg", resposta.token);

      try {
        const payloadDecodificado = jwtDecode(resposta.token);

        const arrayCargos = payloadDecodificado.cargos;

        if (arrayCargos && arrayCargos.length > 0) {
          //Salva o cargo convertendo o array em texto
          localStorage.setItem("cargo_usuario", JSON.stringify(arrayCargos));
        }

      } catch (erro) {
        console.error("Erro ao decodificar o token JWT: ", erro);
      }
    }
    return resposta;
  },

  /**
   * Desloga o usuário limpando a sessão do navegador
   */
  logout: () => {
    localStorage.removeItem("meu_token_tg");
    localStorage.removeItem("cargo_usuario");
    //Redireciona para a tela de login
    window.location.href = "/login";
  }
};
