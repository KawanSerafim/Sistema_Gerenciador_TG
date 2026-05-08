import { Container, Navbar, NavDropdown } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import Nav from 'react-bootstrap/Nav';
import './Styles.css';
import { useMemo } from 'react';
import { jwtDecode } from 'jwt-decode';

import { autenticacaoService } from '../../services/usuario/autenticacaoService';

const UserNavBar = ({ userName = '', opcoes = ["inicio", "nome", "sair"], maxWidth = "900px" }) => {
    const navigate = useNavigate();

    // ==============================================================
    // LÊ O TOKEN PARA DESCOBRIR OS CARGOS (Blindado)
    // ==============================================================
    const cargos = useMemo(() => {
        const token = localStorage.getItem("meu_token_tg");
        if (token) {
            try {
                const payload = jwtDecode(token);
                return payload.cargos || [];
            } catch (erro) {
                console.error(erro)
                return [];
            }
        }
        return [];
    }, []);

    // Função de clique mais segura: previne o reload da página e usa o navigate do React Router
    const handleNavegacao = (evento, rota) => {
        evento.preventDefault();
        navigate(rota);
    };
    let largura = "100%"; // Simplificado: usa 100% de espaço por padrão
    if (opcoes.length === 1) largura = "15%";
    else if (opcoes.length === 2) largura = "75%";

    return (
        <>
            <Container className="mt-2" style={{ maxWidth: maxWidth }}>
                <Nav
                    variant="pills"
                    className='bg-primary rounded d-flex align-items-center px-3'
                    style={{ minHeight: '60px', width: largura }}
                >
                    {/* LADO ESQUERDO: INÍCIO E MENU */}
                    <div className="d-flex flex-1 align-items-center">

                        {opcoes.includes("inicio") && (
                            <Nav.Item>
                                {/* Todos vão para /inicio */}
                                <Nav.Link
                                    href="/inicio"
                                    onClick={(e) => handleNavegacao(e, "/inicio")}
                                    className='text-white nav__link fs-3 fs-md-4 fw-bold me-3'
                                >
                                    Inicio
                                </Nav.Link>
                            </Nav.Item>
                        )}

                        {/* MENU SUSPENSO DINÂMICO BASEADO NO JWT */}
                        {opcoes.includes("inicio") && cargos.length > 0 && (
                            <NavDropdown
                                title={<span className="text-white fs-4 fw-bold">Menu</span>}
                                id="nav-dropdown-dinamico"
                                // Estilização para combinar com a NavBar
                                menuVariant="dark"
                            >
                                {/* AÇÕES DO ADMIN */}
                                {cargos.includes("ROLE_ADMIN") && (
                                    <>
                                        <NavDropdown.Item onClick={(e) => handleNavegacao(e, "/admin/cadastrar-professor")}>Cadastrar Professor</NavDropdown.Item>
                                        <NavDropdown.Item onClick={(e) => handleNavegacao(e, "/curso/cadastro")}>Cadastrar Curso</NavDropdown.Item>
                                    </>
                                )}
                                {/* AÇÕES DO ALUNO */}
                                {cargos.includes("ROLE_ALUNO") && (
                                    <>
                                        <NavDropdown.Item onClick={(e) => handleNavegacao(e, "/aluno/formarGrupo")}>Formar Grupo</NavDropdown.Item>
                                        <NavDropdown.Item onClick={(e) => handleNavegacao(e, "/aluno/solicitarOrientacao")}>Solicitar Orientação</NavDropdown.Item>
                                    </>
                                )}

                                {/* AÇÕES DO PROFESSOR */}
                                {cargos.includes("ROLE_PROFESSOR_TG") && (
                                    <>
                                        {cargos.includes("ROLE_ALUNO") && <NavDropdown.Divider />}
                                        <NavDropdown.Item onClick={(e) => handleNavegacao(e, "/professor/visaoGrupos")}>Visão Grupos TG</NavDropdown.Item>
                                        <NavDropdown.Item onClick={(e) => handleNavegacao(e, "/professor/enviarTurma")}>Enviar Arquivos da Turma</NavDropdown.Item>
                                    </>
                                )}

                                {/* AÇÕES DO COORDENADOR */}
                                {cargos.includes("ROLE_COORDENADOR_CURSO") && (
                                    <>
                                        {(cargos.includes("ROLE_ALUNO") || cargos.includes("ROLE_PROFESSOR_TG")) && <NavDropdown.Divider />}
                                        <NavDropdown.Item onClick={(e) => handleNavegacao(e, "/coordenador/cadastrarTurmaTG")}>Cadastrar Turma TG</NavDropdown.Item>
                                    </>
                                )}
                            </NavDropdown>
                        )}
                    </div>

                    {/* CENTRO: NOME DO USUÁRIO */}
                    <div className="d-flex flex-grow-1 justify-content-center">
                        {opcoes.includes("nome") && (
                            <Nav.Item className="mx-auto">
                                <Navbar.Text className='text-white fs-3 fs-md-4 fw-bold'>
                                    Olá, {userName}
                                </Navbar.Text>
                            </Nav.Item>
                        )}
                    </div>

                    {/* LADO DIREITO: SAIR / VOLTAR */}
                    <div className="d-flex flex-1 justify-content-end">
                        {opcoes.includes("sair") && (
                            <Nav.Item className={opcoes.includes("nome") ? "" : "ms-auto"}>
                                <Nav.Link
                                    href="/"
                                    onClick={(e) => {
                                        e.preventDefault();
                                        // Se for "Sair", limpa o token para encerrar a sessão
                                        if (userName !== "") {
                                            //usa função de logout, quem chama ela tem que fazer o redirecionamento para login
                                            autenticacaoService.logout();
                                        }
                                        navigate("/");
                                    }}
                                    className='text-white nav__link fs-3 fs-md-4 fw-bold'
                                >
                                    {userName !== "" ? "Sair" : "Voltar"}
                                </Nav.Link>
                            </Nav.Item>
                        )}
                    </div>
                </Nav>
            </Container>
        </>
    )
}
export default UserNavBar;