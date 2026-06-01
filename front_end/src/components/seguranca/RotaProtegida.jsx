import { Navigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

const RotaProtegida = ({ children, roleNecessaria }) => {
    // Pega o token do armazenamento
    const token = localStorage.getItem("meu_token_tg");

    // Se não tem token, o usuário nem logado está. Chuta pro Login.
    if (!token) {
        return <Navigate to="/" replace />;
    }
    // Variáveis de controle de fluxo
    let temPermissao = false;
    let tokenInvalido = false;

    try {
        // Decodifica o token para ver quem é
        const payload = jwtDecode(token);
        // Rolesvem como 'cargos'
        const cargos = payload.cargos || [];

        //Se não exigir role nenhuma(Home) OU se o usuário tiver a role exigida
        if (!roleNecessaria || cargos.includes(roleNecessaria)) {
            temPermissao = true;
        }

    } catch (erro) {
        // Se o token for inválido, corrompido ou expirado, manda pro login
        console.error("Token inválido na rota protegida:", erro);
        tokenInvalido = true;
    }

    // ==============================================================
    // 4. RETORNOS JSX
    // ==============================================================

    if (tokenInvalido) {
        // Token corrompido ou expirado
        return <Navigate to="/" replace />;
    }

    if (!temPermissao) {
        // Logado, mas sem o cargo correto
        return <Navigate to="/acesso-negado" replace />;
    }

    // Passou por todas as barreiras! Renderiza a página.
    return children;
}
export default RotaProtegida;