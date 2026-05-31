import { apiClient } from '../apiClient';

export const diretorService = {
    buscarDiretorAtual: async () => {
        const response = await apiClient('/diretores/atual', { skipJsonParse: true });
        if (response.status === 204) return null; // Sem diretor
        return response.json();
    },

    atribuirDiretor: async (payload) => {
        return await apiClient('/diretores/atribuir', {
            method: 'POST',
            body: JSON.stringify(payload)
        });
    },

    retirarDiretor: async () => {
        return await apiClient('/diretores/retirar', {
            method: 'POST'
        });
    }
};