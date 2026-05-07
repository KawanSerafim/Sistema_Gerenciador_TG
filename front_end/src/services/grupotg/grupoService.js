import { apiClient } from "../apiClient";

export const grupoService = {
  /**
   *  Cria um novo grupo de TG
   * @param {Object} payloadGrupo - Objeto contendo os dados do grupo a ser criado
   * @returns {Promise<any>} - Promise com a resposta da criação do grupo
   */
  criarGrupo: async (payloadGrupo) => {
    return await apiClient(`/gruposTg`, {
      method: "POST",
      body: JSON.stringify(payloadGrupo),
    });
  },
  listarVisaoGrupos: async () => {
    try {
      const resposta = await apiClient(`/gruposTg/visao-gruposTg`, {
        method: "GET"
      });
      // Adapte conforme o formato que o seu backend devolver (ex: resposta.grupos, resposta.conteudo)
      return resposta?.grupos || resposta || [];
    } catch (erro) {
      console.error("Erro ao buscar visão dos grupos:", erro);
      throw erro;
    }
  },
};
