import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Container, Card, Alert, Button, Spinner, Row, Col, Badge } from "react-bootstrap";
import UserNavBar from "../../../components/usernavbar/UserNavBar";

import { alunoService } from "../../../services/aluno/alunoService"

const VisaoGrupoAluno = () => {
    const navigate = useNavigate();

    const [grupo, setGrupo] = useState(null);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);
    const [semGrupo, setSemGrupo] = useState(false);

    useEffect(() => {
        const carregarMeuGrupo = async () => {
            try {
                setCarregando(true);
                setErro(null);

                const dados = await alunoService.buscarGrupoAluno();
                setGrupo(dados);
            } catch (err) {
                // Se for um erro indicando que não achou o grupo (ex: 404 ou erro mapeado)
                if (err.message && err.message.toLowerCase().includes("registro não encontrado")) {
                    setSemGrupo(true);
                } else {
                    setErro(err.message || "Não foi possível carregar os dados do seu grupo.");
                }
            } finally {
                setCarregando(false);
            }
        };

        carregarMeuGrupo();
    }, []);

    return (
        <>
            <UserNavBar userName='Aluno' maxWidth="1000px" />

            <Container className="mt-5" style={{ maxWidth: '1000px' }}>
                <div className="d-flex justify-content-between align-items-center bg-primary rounded-top-4 p-3 shadow-sm">
                    <h2 className='text-white fs-2 m-0'>Meu Trabalho de Graduação</h2>
                    <Button variant="outline-light" onClick={() => navigate('/inicio')}>
                        Voltar
                    </Button>
                </div>

                <div className='form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm'>

                    {carregando ? (
                        <div className="text-center py-5">
                            <Spinner animation="border" variant="primary" style={{ width: '4rem', height: '4rem' }} />
                            <p className="mt-3 text-secondary fs-5 fw-medium">Carregando informações...</p>
                        </div>
                    ) : erro ? (
                        <Alert variant="danger" className="text-center fw-bold fs-5">
                            {erro}
                        </Alert>
                    ) : semGrupo ? (
                        // TELA DE ESTADO VAZIO (Aluno sem grupo)
                        <div className="text-center py-5">
                            <Alert variant="warning" className="fs-4 fw-bold mb-4 shadow-sm">
                                Você ainda não está vinculado a nenhum grupo de TG.
                            </Alert>
                            <p className="fs-5 text-secondary mb-4">
                                Para começar o seu projeto, junte-se a outros alunos ou forme o seu próprio grupo.
                            </p>
                            <Button
                                variant="primary"
                                size="lg"
                                className="fw-medium px-5"
                                onClick={() => navigate('/aluno/formarGrupo')}
                            >
                                Formar Novo Grupo
                            </Button>
                        </div>
                    ) : grupo && (
                        // TELA DE EXIBIÇÃO DO GRUPO
                        <Row className="g-4">
                            {/* COLUNA ESQUERDA: Dados do Projeto */}
                            <Col md={7}>
                                <Card className="h-100 shadow-sm border-0 border-start border-primary border-4">
                                    <Card.Body className="p-4">
                                        <div className="mb-4">
                                            <h5 className="text-secondary fw-bold mb-1">Tema do Projeto</h5>
                                            <h3 className="text-dark mb-0">{grupo.tema}</h3>
                                        </div>

                                        <div>
                                            <h5 className="text-secondary fw-bold mb-2">Descrição</h5>
                                            <p className="text-dark fs-5" style={{ textAlign: 'justify', whiteSpace: 'pre-line' }}>
                                                {grupo.descricao || "Nenhuma descrição fornecida para este projeto."}
                                            </p>
                                        </div>
                                    </Card.Body>
                                </Card>
                            </Col>

                            {/* COLUNA DIREITA: Equipe e Orientação */}
                            <Col md={5}>
                                <Card className="h-100 shadow-sm border-0">
                                    <Card.Body className="p-4 bg-light rounded">

                                        <div className="mb-4">
                                            <h5 className="text-secondary fw-bold mb-3 border-bottom pb-2">Equipe ({grupo.integrantes?.length})</h5>
                                            <ul className="list-unstyled mb-0 fs-5">
                                                {grupo.integrantes?.map((nome, idx) => (
                                                    <li key={idx} className="mb-2 d-flex align-items-center">
                                                        <span className="text-primary me-2">■</span> {nome}
                                                    </li>
                                                ))}
                                            </ul>
                                        </div>

                                        <div className="mb-3">
                                            <h5 className="text-secondary fw-bold mb-2 border-bottom pb-2">Orientação</h5>

                                            <div className="mb-2 fs-5">
                                                <strong>Orientador: </strong><br />
                                                {grupo.nomeOrientador ? (
                                                    <span className="text-dark">{grupo.nomeOrientador}</span>
                                                ) : (
                                                    <Badge bg="warning" text="dark">Aguardando Vinculação</Badge>
                                                )}
                                            </div>

                                            <div className="fs-5">
                                                <strong>Coorientador: </strong><br />
                                                {grupo.nomeCoorientador ? (
                                                    <span className="text-dark">{grupo.nomeCoorientador}</span>
                                                ) : (
                                                    <span className="text-secondary fst-italic">Nenhum</span>
                                                )}
                                            </div>
                                        </div>

                                    </Card.Body>
                                </Card>
                            </Col>
                        </Row>
                    )}
                </div>
            </Container>
        </>
    );
};

export default VisaoGrupoAluno;