import { obrigatorio } from "../../utils/utils";
import { apiClient } from "../apiClient";

export const cursoService = {

    /**
     * Busca as disciplinas disponiveis
     * @returns {Promise<string[]>} disciplinas
     */
    buscaDisciplinas: async () => {
        return await apiClient(`/disciplinas`, {
            method: "GET"
        })
    },

    /**
     * Busca os turnos disponiveis
     * @returns {Promise<string[]>} turnos
     */
    buscaTurnos: async () => {
        return await apiClient(`/turnos`, {
            method: "GET"
        })
    },

    /**
     * Cadastra o curso
     * @param {Object} payload
     * @return {Promise<any>} resposta da requisição
    */
    cadastarCurso: async (payload = obrigatorio("payload")) => {
        return await apiClient(`/cursos`, {
            method: "POST",
            body: JSON.stringify(payload)
        })
    }


}