import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Alert, Button, Container, Form, FormGroup } from "react-bootstrap";

import { usuarioService } from "../../services/usuario/usuarioService";

const RecuperacaoSenha = () => {
    const navigate = useNavigate();

    // Estados de controle do fluxo
    const [etapa, setEtapa] = useState(1);
    const [email, setEmail] = useState("");
    const [codigo, setCodigo] = useState("");
    const [novaSenha, setNovaSenha] = useState("");

    // Estados de feedback
    const [loading, setLoading] = useState(false);
    const [erro, setErro] = useState("");
    const [segundos, setSegundos] = useState(5);

    // ========================================================================
    // SOLICITAR CÓDIGO
    // ========================================================================
    const handleSolicitarCodigo = async (e) => {
        e.preventDefault();

        if (!email) {
            setErro("Por favor, preencha o e-mail.");
            return;
        }

        setLoading(true);
        setErro("");

        try {
            await usuarioService.solicitarRecuperacaoSenha(email);
            setEtapa(2);

        } catch (err) {
            setErro(err.message || "Erro ao solicitar recuperação.");
        } finally {
            setLoading(false);
        }
    };

    // ========================================================================
    // REDEFINIR SENHA
    // ========================================================================
    const handleRedefinirSenha = async (e) => {
        e.preventDefault();

        if (!codigo || !novaSenha) {
            setErro("Por favor, preencha o código e a nova senha.");
            return;
        }

        setLoading(true);
        setErro("");

        try {
            await usuarioService.redefinirSenha({
                email: email,
                codigo: codigo,
                novaSenha: novaSenha
            });
            setEtapa(3);
        } catch (err) {
            setErro(err.message || "Erro ao redefinir a senha.");
        } finally {
            setLoading(false);
        }
    };

    // ========================================================================
    // EFEITO TIMER PARA REDIRECIONAMENTO (Etapa 3)
    // ========================================================================
    useEffect(() => {
        if (etapa === 3) {
            const intervalo = setInterval(() => {
                setSegundos((prev) => {
                    if (prev <= 1) {
                        clearInterval(intervalo);
                        //redireciona para a rota de login
                        navigate("/");
                        return 0;
                    }
                    return prev - 1;
                });
            }, 1000);

            // Cleanup do intervalo caso o componente desmonte
            return () => clearInterval(intervalo);
        }
    }, [etapa, navigate]);

    return (
        <Container className="mt-5" style={{ maxWidth: '800px' }}>
            <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>
                Recuperação de Senha
            </h2>

            <div className='form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm'>

                {/* ========================================================= */}
                {/* ETAPA 1: INFORMAR E-MAIL                                  */}
                {/* ========================================================= */}
                {etapa === 1 && (
                    <Form noValidate onSubmit={handleSolicitarCodigo}>
                        <p className="text-center text-secondary fs-5 mb-4">
                            Digite o e-mail cadastrado. Enviaremos um código de 6 dígitos para você.
                        </p>

                        <Form.Group className="mb-4" controlId="formEmailRecuperacao">
                            <Form.Label className='text-secondary fs-4 fw-bold'>Email</Form.Label>
                            <Form.Control
                                type="email"
                                placeholder="Digite seu email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className='bg-white text-black fw-normal fs-5'
                                required
                            />
                        </Form.Group>

                        <FormGroup className="text-center">
                            <Button
                                variant="primary"
                                type="submit"
                                disabled={loading}
                                className='mb-2 fs-5 fw-medium w-100'
                            >
                                {loading ? "Enviando..." : "Continuar"}
                            </Button>

                            <Button
                                variant="outline-secondary"
                                onClick={() => navigate('/')}
                                className='mt-2 fs-5 fw-medium w-100'
                            >
                                Voltar para o Login
                            </Button>
                        </FormGroup>
                    </Form>
                )}

                {/* ========================================================= */}
                {/* ETAPA 2: INFORMAR CÓDIGO E NOVA SENHA                     */}
                {/* ========================================================= */}
                {etapa === 2 && (
                    <Form noValidate onSubmit={handleRedefinirSenha}>
                        <p className="text-center text-secondary fs-5 mb-4">
                            Enviamos um código para <strong>{email}</strong>.<br />
                            Ele expira em 5 minutos.
                        </p>

                        <Form.Group className="mb-3" controlId="formCodigo">
                            <Form.Label className='text-secondary fs-4 fw-bold'>Código de Verificação</Form.Label>
                            <Form.Control
                                type="text"
                                placeholder="XXXXXX"
                                maxLength={6}
                                value={codigo}
                                onChange={(e) => setCodigo(e.target.value)}
                                className='bg-white text-black fw-bold fs-4 text-center tracking-widest'
                                style={{ letterSpacing: '0.5em' }}
                                required
                            />
                        </Form.Group>

                        <Form.Group className="mb-4" controlId="formNovaSenha">
                            <Form.Label className='text-secondary fs-4 fw-bold'>Nova Senha</Form.Label>
                            <Form.Control
                                type="password"
                                placeholder="Digite a nova senha"
                                value={novaSenha}
                                onChange={(e) => setNovaSenha(e.target.value)}
                                className='bg-white text-black fw-normal fs-5'
                                required
                            />
                        </Form.Group>

                        <FormGroup className="text-center">
                            <Button
                                variant="primary"
                                type="submit"
                                disabled={loading}
                                className='mb-2 fs-5 fw-medium w-100'
                            >
                                {loading ? "Validando..." : "Confirmar Nova Senha"}
                            </Button>

                            <Button
                                variant="link"
                                onClick={() => setEtapa(1)}
                                className='mt-2 text-decoration-none text-secondary'
                            >
                                Voltar e alterar e-mail
                            </Button>
                        </FormGroup>
                    </Form>
                )}

                {/* ========================================================= */}
                {/* ETAPA 3: SUCESSO E REDIRECIONAMENTO                       */}
                {/* ========================================================= */}
                {etapa === 3 && (
                    <div className="text-center py-4">
                        <Alert variant="success" className="fs-4 fw-bold shadow-sm">
                            Senha Alterada com Sucesso!
                        </Alert>
                        <p className="fs-5 text-secondary mt-3">
                            Sua senha foi redefinida. Agora você já pode acessar o sistema.
                        </p>
                        <p className="fs-5 fw-bold text-primary mt-4">
                            Redirecionando para o login em {segundos} segundos...
                        </p>
                    </div>
                )}
            </div>

            {/* FEEDBACK DE ERRO GLOBAL*/}
            {erro && (
                <Alert variant="danger" onClose={() => setErro('')} dismissible className="mt-3 shadow-sm fw-bold text-center fs-5">
                    {erro}
                </Alert>
            )}
        </Container>
    );
};

export default RecuperacaoSenha;