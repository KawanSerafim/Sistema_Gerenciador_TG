import { apiClient } from "../../apiClient";

export const grupoService = {
  /**
   *  Cria um novo grupo de TG
   * @param {Object} payloadGrupo - Objeto contendo os dados do grupo a ser criado
   * @returns {Promise<any>} - Promise com a resposta da criação do grupo
   */
  criarGrupo: async (payloadGrupo) => {
    return await apiClient(`/grupos`, {
      method: "POST",
      body: JSON.stringify(payloadGrupo),
    });
  },
};
