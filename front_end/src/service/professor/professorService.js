import { obrigatorio } from "../../utils/utils"
import { apiClient } from "../apiClient"

export const professorService = {

    /**
     * Busca professores por cargo
     * @param {string} cargo 
     * @returns {Promise<Object[]>} resposta da requisição
     */
    buscaProfessoresPorCargo: async (cargo = obrigatorio("cargo")) => {
        return await apiClient(`/professor/cargo/${cargo}`, {
            method: "GET"
        })
    }
}