import { obrigatorio } from "../../utils/utils";
import { apiClient } from "../apiClient";

export const turmasService = {

    buscaTurnos: async () => {
        const resposta = await apiClient(`/compartilhado/turnos`, {
            method: "GET"
        });

        return resposta.turnos || [];
    },

    buscaDisciplianas: async () => {
        const resposta = await apiClient(`/compartilhado/disciplinas`, {
            method: "GET"
        });

        return resposta.nomes || [];
    },

    /**
     * Cadastra novas turmas no sistema
     * @param {Object} payload 
     */
    cadastrarTurmas: async (payload = obrigatorio("payload")) => {
        return await apiClient(`/turmas`, {
            method: "POST",
            body: JSON.stringify(payload)
        });
    },
    /**
     * Busca as turmas do professor tg
     */
    buscarMinhasTurmas: async () => {
        const resposta = await apiClient(`/turmas/professor-tg`, {
            method: "GET"
        });

        return resposta.turmas || [];
    },
    finalizarTurmas: async (payload = obrigatorio("payload")) => {
        return await apiClient(`/turmas/finalizar`, {
            method: "PATCH",
            body: JSON.stringify(payload)
        })
    }
}