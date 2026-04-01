// Vai vir de um arquivo .env
const BASE_URL = import.meta.env.VITE_API_URL;

export const bancaService = {
    // Função para buscar dados (GET)
    buscarGrupos: async () => {
        try {
            const resposta = await fetch(`${BASE_URL}/grupos`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            });
            //Regra de Ouro do fetch: Verifica se deu erro HTTP (400, 404, 500)
            if (!resposta.ok) {
                throw new Error("Falha ao buscar os grupos da API");
            }

            //Extrai o JSON da resposta
            const dados = await resposta.json();
            return dados;

        } catch (erro) {
            console.error("Erro no service (buscarGrupos):", erro);
            //Lança para o componente lidar com a interface
            throw erro;
        }
    },

    // Função para enviar os dados validados do formulário (POST)
    marcarNovaBanca: async (payloadBanca) => {
        try {
            const resposta = await fetch(BASE_URL, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                    //TODO: adicionar token JWT
                    // "Authorization": `Bearer ${token}`
                },
                // Transforma objeto js em string json
                body: JSON.stringify(payloadBanca)
            });

            if (!resposta.ok) {
                const dadosErro = await resposta.json().catch(() => null);
                throw new Error(dadosErro?.message || "Erro interno ao cadastrar banca.");
            }

            // Retorna sucesso
            return await resposta.json();
        } catch (erro) {
            console.error("Erro no Service (marcarNovaBanca):", erro);
            throw erro;
        }
    }
}