import { apiClient } from "../apiClient";
import { jwtDecode } from "jwt-decode";

export const autenticacaoService = {
    /** 
       *  Realiza o login do usuário (Aluno, Professor)
       * @param {Object} credenciais - Objeto contendo {email, senha}
       * @returns {Promise<any>} Dados do usuário e resultado da operação
       */
    login: async (credenciais) => {
        //Realiza requisição
        const resposta = await apiClient(`/autenticacao/login`, {
            method: "POST",
            body: JSON.stringify(credenciais),
        });

        //Se não cair no catch, o login foi um sucesso
        //Então irá salvar o token JWT no local storage
        if (resposta && resposta.token) {
            localStorage.setItem("meu_token_tg", resposta.token);

            try {
                const payloadDecodificado = jwtDecode(resposta.token);

                const arrayCargos = payloadDecodificado.cargos;

                if (arrayCargos && arrayCargos.length > 0) {
                    //Salva o cargo convertendo o array em texto
                    localStorage.setItem("cargo_usuario", JSON.stringify(arrayCargos));
                    console.log("Cargos salvos com sucesso!");
                } else {
                    console.warn("A chave 'cargos' não existe no payload do token.");
                }

            } catch (erro) {
                console.error("Erro ao decodificar o token JWT: ", erro);
            }
        }
        return resposta;
    },

    reenviarCodigo: async (email) => {
        return await apiClient(`/autenticacao/reenviar-codigo`, {
            method: "POST",
            body: JSON.stringify({ email: email })
        });
    },

    /**
     * Desloga o usuário limpando a sessão do navegador
     */
    logout: () => {
        localStorage.removeItem("meu_token_tg");
        localStorage.removeItem("cargo_usuario");
        //Redireciona para a tela de login
        window.location.href = "/login";
    }
}