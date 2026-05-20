import { obrigatorio } from "../../utils/utils"
import { apiClient } from "../apiClient"

export const professorService = {

    /**
     * Busca professores por cargo
     * @param {string} cargo 
     * @returns {Promise<Object[]>} resposta da requisição
     */
    buscaProfessoresPorCargo: async (cargo = obrigatorio("cargo")) => {
        const resposta = await apiClient(`/professores?cargo=${cargo}`, {
            method: "GET"
        });
        // Se a resposta existir, retorna o array. Se não, retorna um array vazio.
        return resposta?.professoresDTO || [];
    },

    /**
     * Busca as opções de cargos do professor
     * @returns {Promise<string[]>} lista de cargos em string
     */
    buscarCargos: async () => {
        const resposta = await apiClient(`/professores/cargos`, {
            method: "GET"
        });

        return resposta?.cargos || [];
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

    /**
     * Listar solicitações orientação pendentes
     * 
     */
    listarSolicitacoesPendentes: async () => {
        return await apiClient(`/solicitacoes-orientacao/pendentes`, {
            method: "GET",
        });
    },

    /**
     * Responder solicitação de orientação
     * @param {String} idSolicitacao 
     * @param {Boolean} aceita 
     * @returns 
     */
    responderSolicitacao: async (idSolicitacao, aceita) => {
        return await apiClient(`/solicitacoes-orientacao/${idSolicitacao}/responder`, {
            method: "POST",
            body: JSON.stringify({ aceita }),
        });
    },

    /**
      * Envia os dados para marcar a banca de um grupo.
      * @param {Object} payload Objeto contendo os dados da requisição
      * @returns {Promise<void>}
      */
    marcarBanca: async (payload) => {
        // Ajuste a rota base ("/bancas" ou "/professores/bancas") de acordo com 
        // o @RequestMapping que está na classe do seu Controlador
        return await apiClient(`/bancas`, {
            method: "POST",
            body: JSON.stringify(payload),
        });
    },

    // Busca a lista de bancas do orientador
    listarBancas: async () => {
        return await apiClient(`/bancas`, {
            method: "GET",
        });
    },

    // Atribui as notas da banca
    atribuirNotasBanca: async (idBanca, payload) => {
        return await apiClient(`/bancas/${idBanca}/notas`, {
            method: "PUT",
            body: JSON.stringify(payload),
        });
    }
}