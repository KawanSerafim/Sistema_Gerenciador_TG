import { obrigatorio } from "../../utils/utils";
import { apiClient } from "../apiClient";

export const cursoService = {

    /**
     * Busca as disciplinas disponiveis
     * @returns {Promise<string[]>} disciplinas
     */
    buscaDisciplinas: async () => {
        const resposta = await apiClient(`/compartilhado/disciplinas`, {
            method: "GET"
        });
        //Devolve apenas o array
        return Array.isArray(resposta) ? resposta : (resposta?.nomes || []);
    },

    /**
     * Busca os turnos disponiveis
     * @returns {Promise<string[]>} turnos
     */
    buscaTurnos: async () => {
        const resposta = await apiClient(`/compartilhado/turnos`, {
            method: "GET"
        });
        return Array.isArray(resposta) ? resposta : (resposta?.turnos || []);
    },

    /**
     * Busca os tipos de TG disponíveis
     * @returns {Promise<string[]>} Array de strings com os tipos de TG
     */
    buscaTiposTg: async () => {
        const resposta = await apiClient(`/cursos/tipos-tg`, {
            method: "GET"
        });
        return Array.isArray(resposta) ? resposta : (resposta?.tipos || []);
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
        });
    }


};