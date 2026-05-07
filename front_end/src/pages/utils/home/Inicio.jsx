import { Button, Container } from "react-bootstrap";
import UserNavBar from "../../../components/usernavbar/UserNavBar";
import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import { useEffect, useMemo } from "react";

const Inicio = () => {
    const navigate = useNavigate();

    // ==============================================================
    // 1. LAZY INITIALIZATION (Evita o erro de cascading renders)
    // Passamos uma função para o useState. O React vai rodar isso 
    // APENAS UMA VEZ antes de desenhar a tela pela primeira vez.
    // ==============================================================
    const dadosUsuario = useMemo(() => {
        const token = localStorage.getItem("meu_token_tg");

        if (token) {
            try {
                const payload = jwtDecode(token);

                // TRUQUE PARA O NOME: Se não vier o 'nome', pegamos o 'sub' (email),
                // quebramos no '@' e formatamos "leon.kennedy" para "Leon Kennedy"
                let nomeFormatado = "Usuário";
                if (payload.nome) {
                    nomeFormatado = payload.nome;
                } else if (payload.sub) {
                    const parteEmail = payload.sub.split('@')[0]; // Pega "leon.kennedy"
                    nomeFormatado = parteEmail
                        .split('.')
                        .map(palavra => palavra.charAt(0).toUpperCase() + palavra.slice(1))
                        .join(' '); // Transforma em "Leon Kennedy"
                }

                return {
                    nome: nomeFormatado,
                    // AJUSTE CRUCIAL: Agora lemos de 'payload.cargos'
                    roles: payload.cargos || []
                };
            } catch (error) {
                console.error("Token inválido", error);
                return { nome: "Usuário", roles: [] };
            }
        }
        return { nome: "Usuário", roles: [] };
        // O array vazio garante que isso rode apenas 1 vez na montagem
    }, []);

    // Extrai as variáveis para facilitar o uso no JSX
    const { nome: nomeUsuario, roles } = dadosUsuario;

    // ==============================================================
    // 2. PROTEÇÃO DE ROTA
    // O useEffect agora cuida APENAS de expulsar quem não tem token
    // ==============================================================
    useEffect(() => {
        const token = localStorage.getItem("meu_token_tg");
        if (!token) {
            navigate("/"); // Redireciona para o login
        }
    }, [navigate]);

    const handleClick = (rota) => {
        navigate(rota);
    };

    return (
        <>
            <UserNavBar userName={nomeUsuario} opcoes={["sair"]} />
            <Container className="mt-5" style={{ maxWidth: '1200px' }}>
                <div className="border border-dark rounded-4 shadow-sm pb-4">
                    <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center mb-5'>
                        Seja Bem-vindo, {nomeUsuario}
                    </h2>

                    <div className="d-flex flex-wrap justify-content-center gap-4 m-3 px-4">

                        {/* ========================================== */}
                        {/* BOTÕES DO ALUNO                          */}
                        {/* ========================================== */}
                        {roles.includes("ROLE_ALUNO") && (
                            <>
                                <Button variant="primary" className='fs-4 fw-medium flex-grow-1' style={{ minWidth: '300px' }}
                                    onClick={() => handleClick('/aluno/formarGrupo')}>
                                    Criar Grupo
                                </Button>

                                <Button variant="primary" className='fs-4 fw-medium flex-grow-1' style={{ minWidth: '300px' }}
                                    onClick={() => handleClick('/aluno/enviarTG')}>
                                    Enviar Trabalho de Graduação
                                </Button>
                                <Button variant="primary" className='fs-4 fw-medium flex-grow-1' style={{ minWidth: '300px' }}
                                    onClick={() => handleClick('/aluno/solicitarOrientacao')}>
                                    Solicitar Orientação
                                </Button>
                                {/* ... MANTENHA O RESTO DOS SEUS BOTÕES AQUI ... */}
                            </>
                        )}

                        {/* ========================================== */}
                        {/* BOTÕES DO PROFESSOR DE TG                  */}
                        {/* ========================================== */}
                        {roles.includes("ROLE_PROFESSOR_TG") && (
                            <>
                                <Button variant="primary" className='fs-4 fw-medium flex-grow-1' style={{ minWidth: '300px' }}
                                    onClick={() => handleClick('/professor/visaoGrupos')}>
                                    Visão dos Grupos TG
                                </Button>
                                <Button variant="primary" className='fs-4 fw-medium flex-grow-1' style={{ minWidth: '300px' }}
                                    onClick={() => handleClick('/professor/enviarTurma')}>
                                    Enviar Arquivo da Turma
                                </Button>
                                <Button variant="primary" className='fs-4 fw-medium flex-grow-1' style={{ minWidth: '300px' }}
                                    onClick={() => handleClick('/professor/finalizarDisciplinas')}>
                                    Finalizar Disciplinas
                                </Button>
                                <Button variant="primary" className='fs-4 fw-medium flex-grow-1' style={{ minWidth: '300px' }}
                                    onClick={() => handleClick('/professor/visaoSolicitacoesOrientacao')}>
                                    Visão Solicitacoes de Orientação
                                </Button>

                            </>
                        )}

                        {/* ========================================== */}
                        {/* BOTÕES DO COORDENADOR DE CURSO             */}
                        {/* ========================================== */}
                        {roles.includes("ROLE_COORDENADOR_CURSO") && (
                            <>
                                <Button variant="success" className='fs-4 fw-medium flex-grow-1' style={{ minWidth: '300px' }}
                                    onClick={() => handleClick('/coordenador/cadastrarTurmaTG')}>
                                    Cadastrar Turma TG
                                </Button>
                                {/* Outros botões de coordenador... */}
                            </>
                        )}

                        {/* ========================================== */}
                        {/* BOTÕES DO ORIENTADOR                       */}
                        {/* ========================================== */}
                        {roles.includes("ROLE_ORIENTADOR") && (
                            <>
                                <Button variant="info" className='fs-4 fw-medium text-white flex-grow-1' style={{ minWidth: '300px' }}
                                    onClick={() => handleClick('/orientador/meusGrupos')}>
                                    Meus Grupos Orientados
                                </Button>
                                <Button variant="primary" className='fs-4 fw-medium flex-grow-1' style={{ minWidth: '300px' }}
                                    onClick={() => handleClick('/professor/visaoBancas')}>
                                    Visão Bancas e Notas
                                </Button>
                                <Button variant="primary" className='fs-4 fw-medium flex-grow-1' style={{ minWidth: '300px' }}
                                    onClick={() => handleClick('/professor/marcarBanca')}>
                                    Marcar Banca
                                </Button>
                                <Button variant="primary" className='fs-4 fw-medium flex-grow-1' style={{ minWidth: '300px' }}
                                    onClick={() => handleClick('/professor/certificados')}>
                                    Certificados de Bancas
                                </Button>
                            </>
                        )}
                    </div>

                </div>
            </Container>
        </>
    )
}

export default Inicio;