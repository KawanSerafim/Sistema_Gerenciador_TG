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
    }
}