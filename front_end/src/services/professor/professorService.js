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


}