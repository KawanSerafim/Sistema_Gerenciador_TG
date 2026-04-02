import { apiClient } from "./apiService";

export const bancaService = {
    // Função para buscar dados (GET)
    buscarGrupos: async () => {
        //apiClient vai fazer checkagens, passar a url base e headers
        return await apiClient("/bancas/grupos", {
            method: "GET"
        });
    },

    // Função para enviar os dados validados do formulário (POST)
    marcarNovaBanca: async (payloadBanca) => {

        //Token JWT vai ser injetado pelo apiClient
        return await apiClient("/bancas", {
            method: "POST",
            // Transforma objeto js em string json
            body: JSON.stringify(payloadBanca)
        });
    }
}