import { useState } from "react"
import { useNavigate } from "react-router-dom";
import { Alert, Button, Container, Form, FormGroup } from "react-bootstrap"
import { autenticacaoService } from "../../../services/usuario/autenticacaoService"

import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { loginSchema } from "../../../schemas/utils/usuarios/usuariosZodSchema"
import { jwtDecode } from "jwt-decode";

const Login = () => {
    const [erro, setErro] = useState()

    const navigate = useNavigate();

    const handleClick = (rota) => {
        navigate(rota);
    };

    const { register, handleSubmit, formState: { errors } } = useForm({
        resolver: zodResolver(loginSchema),
        defaultValues: { email: "", senha: "" }
    });

    const realizarLogin = async (dadosValidados) => {
        try {
            //Limpa erros anteriores
            setErro("");
            console.log("Enviando dados ao backend", dadosValidados);
            // Chama o Service
            await autenticacaoService.login({
                email: dadosValidados.email,
                senha: dadosValidados.senha
            });

            // 2. Recupera e decodifica o token para decidir a rota inicial
            const token = localStorage.getItem("meu_token_tg");
            const payload = jwtDecode(token);
            const cargos = payload.cargos || [];

            console.log("Login realizado com sucesso. Redirecionando...");

            // 3. Lógica de Redirecionamento simplificada
            if (cargos.includes("ROLE_ADMIN")) {
                // Admin geralmente tem um painel de controle totalmente diferente
                navigate("/curso/cadastro");
            } else {
                // ALUNO, PROFESSOR_TG, COORDENADOR e ORIENTADOR 
                // Todos vão para a tela de inicio universal
                navigate("/inicio");
            }

        } catch (erro) {
            console.log("Erro capturado no login:", erro.codigo, erro.message);
            const mensagemErro = (erro.message || "").toLowerCase();
            const codigoErro = erro.codigo || ""; // Pega o código que o apiClient injetou!

            const codigoContaPendente = "RN_001_ESTADO_INVALIDO_PARA_ACAO";
            // Verifica se a mensagem do backend tem palavras-chave que indicam pendência
            if (codigoErro === codigoContaPendente ||
                mensagemErro.includes("deve ter o estado 'ativo'") ||
                mensagemErro.includes("verificação")
            ) {

                console.warn("Usuário pendente de verificação. Redirecionando...");

                // Joga para a tela de código passando o e-mail que ele acabou de tentar logar
                navigate("/confirmarEmail", {
                    state: { emailCapturado: dadosValidados.email }
                });
                return; // Para a execução da função aqui
            }

            // Se for outro erro (senha errada, não existe, etc), exibe o erro normal
            setErro(erro.message || "Usuário ou senha incorretos");
        }
    };

    return (
        <>
            <Container className="mt-5" style={{ maxWidth: '800px' }}>
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Login</h2>
                <Form
                    noValidate
                    onSubmit={handleSubmit(realizarLogin)}
                    className='form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm'>

                    {/* Email */}
                    <Form.Group className="mb-3" controlId="formBasicEmail">
                        <Form.Label className='text-secondary fs-4 fw-bold'>Email</Form.Label>
                        <Form.Control type="email" placeholder="Digite seu email"
                            {...register("email")}
                            isInvalid={!!errors.email}
                            className='bg-white text-black fw-normal fs-5' />

                        {/* Feedback de erro */}
                        <Form.Control.Feedback type="invalid">
                            {errors.email?.message}
                        </Form.Control.Feedback>
                    </Form.Group>
                    {/* Senha */}
                    <Form.Group className="mb-3" controlId="formBasicPassword">
                        <Form.Label className='text-secondary fs-4 fw-bold'>Senha</Form.Label>
                        <Form.Control type="password" placeholder="Digite sua senha"
                            {...register("senha")}
                            isInvalid={!!errors.senha} className='bg-white text-black fw-normal fs-5' />
                        {/* Feedback de erro */}
                        <Form.Control.Feedback type="invalid">
                            {errors.senha?.message}
                        </Form.Control.Feedback>
                    </Form.Group>


                    {/* Botão de Cadastrar */}
                    <FormGroup className="text-center">
                        <Button
                            variant="primary"
                            type="submit"
                            id='btn-cadastro' className='mb-2 fs-5 fw-medium w-100'
                        >
                            Entrar
                        </Button>
                    </FormGroup>

                    <FormGroup className="d-flex  gap-5 text-center">
                        {/* Btn do fluxo de recuperação de senha */}
                        <Button
                            variant="primary"
                            onClick={() => handleClick('/recuperar-senha')}
                            id='btn-cadastro' className='mb-2 fs-5 fw-medium w-50'
                        >
                            Esqueceu sua senha?
                        </Button>
                        <Button
                            variant="primary"
                            onClick={() => handleClick('/aluno/cadastro')}
                            id='btn-cadastro' className='mb-2 fs-5 fw-medium w-50'
                        >
                            Cadastrar-se como aluno
                        </Button>

                    </FormGroup>
                </Form>
                {erro && (
                    <Alert variant="danger"
                        onClose={() => setErro('')}
                        className="mt-3 shadow-sm fw-bold text-center"
                    >
                        {erro}
                    </Alert>
                )}
            </Container>
        </>
    )
}
export default Login