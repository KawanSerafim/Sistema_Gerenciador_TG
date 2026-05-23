import UserNavBar from "../../../components/usernavbar/UserNavBar";
import { Button, Col, Container, Form, Modal, Pagination, Row, Spinner } from "react-bootstrap";
import TableComponent from "../../../components/table/TableComponent";
import { useModal } from "../../../hooks/useModal/useModal";
import { useEffect, useMemo, useState } from "react";

import { grupoService } from "../../../services/grupotg/grupoService";

const VisaoGrupos = () => {

    // Estado para o nosso filtro específico da página
    const [somenteSemGrupo, setSomenteSemGrupo] = useState(false);

    /// Estados da API
    const [data, setData] = useState([]);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);

    // Estados de Paginação
    const [paginaAtual, setPaginaAtual] = useState(0);
    const [totalPaginas, setTotalPaginas] = useState(0);
    const tamanhoPagina = 10;

    const {
        show,
        selectedData: selectedMembers,
        handleOpen,
        handleClose
    } = useModal();

    // Colunas da Tabela
    const columns = useMemo(() => [
        { header: "Tipo de TG", accessor: "tipoTG", filtravel: true, tipoFiltro: "select" },
        { header: "Tema", accessor: "tema", filtravel: true, tipoFiltro: "text" },
        {
            header: "Grupo / Aluno Sem Grupo",
            accessor: "grupo",
            filtravel: true,
            tipoFiltro: "text",
            render: (row) => {
                // Verifica se é aluno ou grupo
                const isSemGrupo = row.tema === "Sem grupo";

                if (isSemGrupo) {
                    // Exibe o nome direto em texto, sem botão
                    return <span className="fw-bold text-black fs-6">{row.grupo[0]}</span>;
                }

                // Se for um grupo normal, exibe o botão
                return (
                    <Button
                        variant='primary'
                        size='sm'
                        className="px-2 fs-5"
                        onClick={() => handleOpen(row.grupo)}
                    >
                        Visualizar Integrantes
                    </Button>
                );
            }
        },
        { header: "Orientador", accessor: "orientador", filtravel: true, tipoFiltro: "select" }
        // Executa toda vez que handleOpen executar
    ], [handleOpen]);

    // Dispara a busca toda vez que mudar a página OU o switch de "somente sem grupo"
    useEffect(() => {
        const carregarGrupos = async () => {
            try {
                setCarregando(true);
                setErro(null);

                const responsePagina = await grupoService.listarVisaoGrupos(paginaAtual, tamanhoPagina, somenteSemGrupo);

                //acessa o conteudo
                const conteudoArray = responsePagina?.conteudo || [];

                // Mapeia os dados do DTO do Java para o TableComponent
                const dadosFormatados = conteudoArray.map(item => {
                    // O backend manda "" (string vazia) para os alunos avulsos
                    const isAlunoSemGrupo = item.tema === "" || item.tema === "Sem tema definido";

                    return {
                        tipoTG: isAlunoSemGrupo ? "Sem grupo" : item.tipoTg,
                        tema: isAlunoSemGrupo ? "Sem grupo" : item.tema,
                        orientador: isAlunoSemGrupo ? "Sem grupo" : (item.nomeOrientador || "Sem orientador"),
                        grupo: item.integrantes ? item.integrantes.map(i => i.nome) : []
                    }
                });

                setData(dadosFormatados);
                setTotalPaginas(responsePagina.totalPaginas || 0);

            } catch (error) {
                console.error(error);
                setErro("Não foi possível carregar a lista de grupos. Tente novamente.");
            } finally {
                setCarregando(false);
            }
        };

        carregarGrupos();
    }, [paginaAtual, somenteSemGrupo]);

    // Reseta a paginação ao ligar/desligar o switch
    const handleSwitchChange = (e) => {
        setSomenteSemGrupo(e.target.checked);
        setPaginaAtual(0); // Volta para a página 1 ao mudar o filtro
    };

    return (
        <>
            <UserNavBar
                /*Deve verificar qual o nome do usuario logado para ser passado ao componente*/
                userName='Professor de TG'
                maxWidth="1500px"
            />

            <Container className="mt-3" style={{ maxWidth: '1500px' }}>

                <h2 className='text-black p-3 fs-1 rounded-top-4 text-center mb-3'>Visão dos Grupos</h2>

                {/* Filtro específico da página (Fora da tabela) */}
                <Row className="mb-4">
                    <Col className="d-flex justify-content-start">
                        <Form.Check
                            type="switch"
                            id="switch-sem-grupo"
                            label="Exibir apenas alunos sem grupo"
                            className="fs-4 fw-bold text-secondary"
                            checked={somenteSemGrupo}
                            onChange={handleSwitchChange}
                            disabled={carregando || !!erro}
                        />
                    </Col>
                </Row>
                {/* Área Principal de Conteúdo */}
                <div className="mt-5">
                    {carregando ? (
                        <div className="d-flex flex-column align-items-center justify-content-center py-5">
                            <Spinner animation="border" variant="primary" style={{ width: '4rem', height: '4rem' }} />
                            <h4 className="mt-3 text-secondary">Carregando grupos...</h4>
                        </div>
                    ) : erro ? (
                        <Alert variant="danger" className="text-center fw-bold fs-5 shadow-sm">
                            {erro}
                        </Alert>
                    ) : (
                        <>
                            {/* A prop de dados recebe 'data' direto, sem o filtro frontend */}
                            <TableComponent colunas={columns} dados={data} />

                            {/* Controles de Paginação */}
                            {totalPaginas > 1 && (
                                <div className="d-flex justify-content-center mt-4">
                                    <Pagination>
                                        <Pagination.Prev
                                            disabled={paginaAtual === 0}
                                            onClick={() => setPaginaAtual(prev => prev - 1)}
                                        />
                                        {[...Array(totalPaginas)].map((_, idx) => (
                                            <Pagination.Item
                                                key={idx}
                                                active={idx === paginaAtual}
                                                onClick={() => setPaginaAtual(idx)}
                                            >
                                                {idx + 1}
                                            </Pagination.Item>
                                        ))}
                                        <Pagination.Next
                                            disabled={paginaAtual === totalPaginas - 1}
                                            onClick={() => setPaginaAtual(prev => prev + 1)}
                                        />
                                    </Pagination>
                                </div>
                            )}
                        </>
                    )}
                </div>

                {/* Modal integrantes */}
                <Modal show={show} onHide={handleClose} centered contentClassName="custom-modal-content">

                    <Modal.Header closeButton>
                        <div className="custom-modal-title text-center">
                            <h5>Integrantes</h5>
                        </div>
                    </Modal.Header>
                    <Modal.Body className="p-0">
                        <ul className="m-0 p-0">
                            {selectedMembers && selectedMembers.length > 0 ? (
                                selectedMembers.map((member, index) => (
                                    <li key={index} className="custom-list-item">{member}</li>
                                ))
                            ) : (
                                <li className="custom-list-item text-muted">
                                    Nenhum integrante encontrado.
                                </li>
                            )}

                        </ul>
                    </Modal.Body>
                </Modal>
            </Container>
        </>
    )
}

export default VisaoGrupos