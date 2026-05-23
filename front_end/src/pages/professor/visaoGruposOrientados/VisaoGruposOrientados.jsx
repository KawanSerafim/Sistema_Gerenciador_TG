import { useState, useEffect, useMemo } from "react";
import { Container, Form, Alert, Button, Spinner, Row, Col, Modal } from "react-bootstrap";
import UserNavBar from "../../../components/usernavbar/UserNavBar";
import { useModal } from "../../../hooks/useModal/useModal";
import TableComponent from "../../../components/table/TableComponent";
import { grupoService } from "../../../services/grupotg/grupoService";

const VisaoGruposOrientados = () => {
    // Estados dos Filtros da API (Ano e Semestre)
    const [filtroAno, setFiltroAno] = useState("");
    const [filtroSemestre, setFiltroSemestre] = useState("");

    // Estados da API
    const [data, setData] = useState([]);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);

    // Hook customizado para o Modal
    const {
        show,
        selectedData: selectedMembers,
        handleOpen,
        handleClose
    } = useModal();

    // Configuração das Colunas do TableComponent
    const columns = useMemo(() => [
        { header: "Tipo de TG", accessor: "tipoTg", filtravel: true, tipoFiltro: "select" },
        { header: "Tema", accessor: "tema", filtravel: true, tipoFiltro: "text" },
        { header: "Período", accessor: "periodo", filtravel: false }, // Não precisa filtro local, a API já faz
        {
            header: "Integrantes",
            accessor: "integrantes",
            filtravel: false,
            render: (row) => {
                return (
                    <Button
                        variant='primary'
                        size='sm'
                        className="px-2 fs-5"
                        onClick={() => handleOpen(row.nomesAlunos)} // Passa o array de nomes pro modal
                    >
                        Visualizar Integrantes
                    </Button>
                );
            }
        },
        { header: "Situação", accessor: "situacao", filtravel: true, tipoFiltro: "select" },
    ], [handleOpen]);

    // Dispara a busca toda vez que os filtros da API mudarem
    useEffect(() => {
        const carregarGrupos = async () => {
            try {
                setCarregando(true);
                setErro(null);

                // Converte string vazia para null antes de enviar para a service
                const anoParam = filtroAno || null;
                const semestreParam = filtroSemestre || null;

                const response = await grupoService.buscarGruposOrientados(anoParam, semestreParam);

                // Mapeia os dados do DTO do Java para o TableComponent
                const dadosFormatados = response.map(item => ({
                    idGrupo: item.idGrupo,
                    tipoTg: item.tipoTg,
                    tema: item.tema,
                    // Monta a string do período ou exibe um aviso se faltar
                    periodo: item.semestre && item.ano ? `${item.semestre}º/${item.ano}` : "Não definido",
                    // Guarda o array original para o botão repassar ao modal
                    nomesAlunos: item.nomesAlunos || [],
                    situacao: item.situacao
                }));

                setData(dadosFormatados);
            } catch (error) {
                console.error(error);
                setErro("Não foi possível carregar a lista de grupos orientados. Tente novamente.");
            } finally {
                setCarregando(false);
            }
        };

        carregarGrupos();
    }, [filtroAno, filtroSemestre]);

    return (
        <>
            <UserNavBar
                userName='Orientador'
                maxWidth="1500px"
            />

            <Container className="mt-3" style={{ maxWidth: '1500px' }}>

                <h2 className='text-black p-3 fs-1 rounded-top-4 text-center mb-3'>
                    Meus Grupos Orientados
                </h2>

                {/* Filtros Externos (Batem na API) */}
                <Row className="mb-4">
                    <Col md={3} sm={6} className="d-flex align-items-center gap-2">
                        <Form.Label className="fw-bold text-secondary mb-0 text-nowrap fs-5">Ano Letivo:</Form.Label>
                        <Form.Select
                            value={filtroAno}
                            onChange={(e) => setFiltroAno(e.target.value)}
                            className="fs-5 shadow-sm"
                        >
                            <option value="">Todos</option>
                            <option value="2026">2026</option>
                            <option value="2025">2025</option>
                            <option value="2024">2024</option>
                        </Form.Select>
                    </Col>

                    <Col md={3} sm={6} className="d-flex align-items-center gap-2">
                        <Form.Label className="fw-bold text-secondary mb-0 text-nowrap fs-5">Semestre:</Form.Label>
                        <Form.Select
                            value={filtroSemestre}
                            onChange={(e) => setFiltroSemestre(e.target.value)}
                            className="fs-5 shadow-sm"
                        >
                            <option value="">Todos</option>
                            <option value="1">1º Semestre</option>
                            <option value="2">2º Semestre</option>
                        </Form.Select>
                    </Col>
                </Row>

                {/* Área Principal de Conteúdo */}
                <div className="mt-4">
                    {carregando ? (
                        <div className="d-flex flex-column align-items-center justify-content-center py-5">
                            <Spinner animation="border" variant="primary" style={{ width: '4rem', height: '4rem' }} />
                            <h4 className="mt-3 text-secondary">Carregando grupos orientados...</h4>
                        </div>
                    ) : erro ? (
                        <Alert variant="danger" className="text-center fw-bold fs-5 shadow-sm">
                            {erro}
                        </Alert>
                    ) : (
                        <>
                            {/* O TableComponent monta a tabela e cuida dos filtros internos sozinhos */}
                            <TableComponent colunas={columns} dados={data} />
                        </>
                    )}
                </div>

                {/* Modal Integrantes (IDÊNTICO ao do VisaoGrupos) */}
                <Modal show={show} onHide={handleClose} centered contentClassName="custom-modal-content">
                    <Modal.Header closeButton>
                        <div className="custom-modal-title text-center w-100">
                            <h5 className="m-0">Integrantes do Grupo</h5>
                        </div>
                    </Modal.Header>
                    <Modal.Body className="p-0">
                        <ul className="m-0 p-0">
                            {selectedMembers && selectedMembers.length > 0 ? (
                                selectedMembers.map((member, index) => (
                                    <li key={index} className="custom-list-item">{member}</li>
                                ))
                            ) : (
                                <li className="custom-list-item text-muted text-center py-3">
                                    Nenhum integrante encontrado.
                                </li>
                            )}
                        </ul>
                    </Modal.Body>
                </Modal>

            </Container>
        </>
    );
};

export default VisaoGruposOrientados;