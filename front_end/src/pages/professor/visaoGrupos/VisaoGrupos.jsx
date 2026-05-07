import UserNavBar from "../../../components/usernavbar/UserNavBar";
import { Button, Col, Container, Form, Modal, Row, Spinner } from "react-bootstrap";
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

    const {
        show,
        selectedData: selectedMembers,
        handleOpen,
        handleClose
    } = useModal();

    // Colunas da Tabela
    const columns = useMemo(() => [
        { header: "IdGrupo", accessor: "id", filtravel: true, tipoFiltro: "text" },
        { header: "Tipo de TG", accessor: "tipoTG", filtravel: true, tipoFiltro: "select" },
        { header: "Tema", accessor: "tema", filtravel: true, tipoFiltro: "text" },
        {
            header: "Grupo",
            accessor: "grupo",
            filtravel: true,
            tipoFiltro: "text",
            render: (row) => {
                // Se não tem tema definido, consideramos que é um aluno avulso
                const isSemGrupo = !row.tema || row.tema.trim() === "";

                if (isSemGrupo) {
                    // Exibe o nome direto em texto, sem botão
                    return <span className="fw-medium text-black fs-6">{row.grupo[0]}</span>;
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

    // Busca os dados no Backend ao montar a tela
    useEffect(() => {
        const carregarGrupos = async () => {
            try {
                setCarregando(true);
                setErro(null);

                // Pega a lista do backend (O service já retorna o array resposta.grupos)
                const listaDoBackend = await grupoService.listarVisaoGrupos();

                // Faz a tradução (De -> Para) do Backend para o Frontend
                const dadosFormatados = listaDoBackend.map(item => ({
                    id: item.idGrupo,

                    // Limpa os textos "feios" do backend para não aparecerem na UI
                    tipoTG: item.tipoTg === "NÃO_DEFINIDO" ? "" : item.tipoTg,
                    tema: item.tema === "Sem tema definido" ? "" : item.tema,
                    orientador: item.nomeOrientador,

                    // Transforma o array de objetos [{id, nome}] em um array de strings
                    // Isso faz o render customizado (que usa row.grupo[0]) e o Modal funcionarem
                    grupo: item.integrantes ? item.integrantes.map(integrante => integrante.nome) : []
                }));

                setData(dadosFormatados);

            } catch (error) {
                console.error(error);
                setErro("Não foi possível carregar a lista de grupos. Tente novamente.");
            } finally {
                setCarregando(false);
            }
        };

        carregarGrupos();
    }, []);

    const dadosFiltrados = useMemo(() => {
        if (somenteSemGrupo) {
            // Filtra apenas onde não tem tema ou não tem orientador definido formalmente
            return data.filter(item => !item.tema || item.tema.trim() === "");
        }
        // Se o check estiver desligado, retorna todos (a tabela cuida dos outros filtros)
        return data;
    }, [somenteSemGrupo, data])


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
                            onChange={(e) => setSomenteSemGrupo(e.target.checked)}
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
                        <TableComponent
                            colunas={columns}
                            dados={dadosFiltrados}

                        />
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