import { obrigatorio } from "../../utils/utils";
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
  listarVisaoGrupos: async (pagina = 0, tamanho = 10, somenteSemGrupo = false) => {
    try {
      // Ajuste a rota para suportar as Query Params
      const url = `/gruposTg/visao-gruposTg?pagina=${pagina}&tamanho=${tamanho}&somenteSemGrupo=${somenteSemGrupo}`;
      const resposta = await apiClient(url, { method: "GET" });

      // Retorna o objeto Pagina completo
      return resposta?.grupos || resposta;
    } catch (erro) {
      console.error("Erro ao buscar visão dos grupos:", erro);
      throw erro;
    }
  },
  vincularCoorientadorExterno: async (payload = obrigatorio("payload")) => {
    return await apiClient(`/gruposTg/coorientadores-externos`, {
      method: "PATCH",
      body: JSON.stringify(payload),
    });
  },
  /**
  * Busca os grupos orientados pelo professor logado.
  * @param {string|number} ano (Opcional) Ano letivo
  * @param {string|number} semestre (Opcional) Semestre letivo
  * @returns {Promise<Array>} Lista de grupos
  */
  buscarGruposOrientados: async (ano, semestre) => {
    let url = "/gruposTg/grupos-orientados"; // Ajuste o prefixo se o seu @RequestMapping for diferente
    const params = [];

    if (ano) params.push(`ano=${ano}`);
    if (semestre) params.push(`semestre=${semestre}`);

    if (params.length > 0) {
      url += `?${params.join("&")}`;
    }

    return await apiClient(url, {
      method: "GET",
    });
  },
};
