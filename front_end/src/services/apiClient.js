const BASE_URL = import.meta.env.VITE_API_URL;
/**
 * Cliente HTTP customizado que atua como interceptador de requisições.
 * Injeta automaticamente o token de autenticação e trata erros globais (ex: sessão expirada).
 *
 * @param {string} endpoint - O caminho da API a ser chamado (ex: "/bancas/grupos"). Não inclua a URL base.
 * @param {RequestInit} [options={}] - Opções customizadas para o fetch (method, headers, body, etc).
 * @returns {Promise<any>} A promessa resolvida com os dados da resposta em formato JSON.
 * @throws {Error} Lança um erro com a mensagem do servidor caso a requisição falhe.
 */
export const apiClient = async (endpoint, options = {}) => {

    // ======= Interceptando a requisição =======

    //Pega o token do localStorage
    const token = localStorage.getItem("meu_token_tg");

    //Pega headers padrões
    const headers = {
        //Mantem os headers que podem ter sido passados
        ...options.headers,
    }

    // ========= Para Upload de arquivos ==============
    // Só injeta application/json se não estiver enviando um arquivo (FormData)
    if (!(options.body instanceof FormData)) {
        //Se quem chamou a função não passou um Content-Type especifico, coloca no JSON
        headers["Content-Type"] = headers["Content-Type"] || "application/json";
    }


    //Se o token existir, coloca ele na requisição
    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    try {
        //Usa o fetch original com URL_BASE + Endpoint
        const resposta = await fetch(`${BASE_URL}${endpoint}`, {
            ...options,
            headers,
        });

        // ======= Interceptando resposta =======

        //Se backend dizer que o token é invalido/expirado (401 ou 403)
        if (resposta.status === 401 || resposta.status === 403) {
            // AJUSTE AQUI: Transformamos isRotaDeLogin em isRotaPublica
            // e adicionamos "/usuarios" (ou o endpoint exato de cadastro) e o confirmar email
            const isRotaPublica = endpoint.includes("/login") ||
                endpoint.includes("/autenticacao") ||
                endpoint.includes("/alunos") ||
                endpoint.includes("/confirmarEmail");

            // Só desloga e redireciona se der 401/403 E NÃO FOR uma rota pública
            if ((resposta.status === 401 || resposta.status === 403) && !isRotaPublica) {
                console.error("Sessão expirada. Deslogando usuário...");

                // Limpa a sessão do sistema
                localStorage.removeItem("meu_token_tg");
                localStorage.removeItem("cargo_usuario");

                //Força o usuário de volta para a tela de login
                window.location.href = "/";

                return new Promise(() => { });
            }
        }
        //Se deu qualquer erro (400, 404, 500)
        if (!resposta.ok) {
            // Tenta ler a mensagem de erro que o backend enviou, ou usa uma generica
            const dadosErro = await resposta.json().catch(() => null);

            // Tenta pegar a mensagem
            const textoErro = dadosErro?.mensagem || "Erro de comunicação com o servidor.";

            // TENTA PEGAR O CÓDIGO (Ajuste a chave 'codigo' para o nome exato que vem no JSON do seu Java)
            const codigoErro = dadosErro?.codigo || null;

            // Cria o erro padrão do JS
            const erroCustomizado = new Error(textoErro);

            // "Gruda" o código no erro para o Front-end conseguir ler depois
            erroCustomizado.codigo = codigoErro;

            throw erroCustomizado;
        }

        //Se deu tudo certo, tenta devolver o JSON (ou vazio dependendo do caso)
        if (resposta.status !== 204) {
            const text = await resposta.text();
            // Se tiver texto, faz o parse. Se não (como no 201 vazio), devolve um objeto vazio ou null
            const data = text ? JSON.parse(text) : {};

            return data;
        }
        return null;

    } catch (erro) {
        //Pega problemas de Network Error
        console.error("Erro no apiClient:", erro);
        throw erro;
    }


}