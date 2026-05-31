import { apiClient } from "../apiClient";

export const bancaService = {
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
    },
    cancelarBanca: async (idBanca) => {
        return await apiClient(`/bancas/${idBanca}/cancelar`, {
            method: "PUT",
        });
    },
    baixarAtaBanca: async (idBanca) => {
        const resposta = await apiClient(`/bancas/${idBanca}/ata/baixar`, {
            method: "GET",
            //Avisa o fetch que não é um json e sim um arquivo
            skipJsonParse: true
        });
        //Transforma a resposta crua em blob para ser 
        return await resposta.blob();
    }
}