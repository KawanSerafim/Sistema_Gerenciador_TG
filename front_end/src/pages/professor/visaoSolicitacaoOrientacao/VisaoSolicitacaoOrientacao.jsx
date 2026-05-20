import { Alert, Button, Col, Container, Modal, Row, Spinner, Stack } from 'react-bootstrap';
import TableComponent from '../../../components/table/TableComponent'
import UserNavBar from '../../../components/usernavbar/UserNavBar';
import "../../../styles/ModalIntegrantes.css"
import { useModal } from '../../../hooks/useModal/useModal';
import { useEffect, useMemo, useState } from 'react';

import { professorService } from '../../../services/professor/professorService';

const VisaoSolicitacaoOrientacao = () => {

    // Estados da API
    const [data, setData] = useState([]);
    const [carregando, setCarregando] = useState(true);
    const [processandoAcaoId, setProcessandoAcaoId] = useState(null);

    // Estado unificado para controle de alertas de sucesso e erro
    const [resultadoPedido, setResultadoPedido] = useState({
        exibir: false,
        variante: "",
        mensagem: ""
    });

    // Hook customizado do modal
    const {
        show,
        selectedData,
        handleOpen,
        handleClose
    } = useModal(null);

    // Busca os dados das solicitações pendentes no backend
    const carregarSolicitacoes = async () => {
        try {
            setCarregando(true);
            setResultadoPedido({ exibir: false, variante: "", mensagem: "" });

            const response = await professorService.listarSolicitacoesPendentes();
            setData(response);
        } catch (error) {
            console.error("Erro ao listar pendentes:", error);
            setResultadoPedido({
                exibir: true,
                variante: "danger",
                mensagem: "Não foi possível carregar a lista de solicitações. Tente novamente."
            });
        } finally {
            setCarregando(false);
        }
    };

    // Executa a busca assim que a tela é montada
    useEffect(() => {
        carregarSolicitacoes();
    }, []);

    // Envia a resposta (aceitar/recusar) para o backend
    const handlePedidoOrientacao = async (row, resultado) => {
        try {
            setProcessandoAcaoId(row.idSolicitacao);
            setResultadoPedido({ exibir: false, variante: "", mensagem: "" });

            // Chama a rota de resposta passando o id e a decisão
            await professorService.responderSolicitacao(row.idSolicitacao, resultado);

            // Remove a solicitação processada da tabela visual
            setData(prevData => prevData.filter(item => item.idSolicitacao !== row.idSolicitacao));

            setResultadoPedido({
                exibir: true,
                variante: "success",
                mensagem: `Orientação do grupo do aluno: ${row.nomeAlunoRepresentante} foi ${resultado ? "aceita" : "recusada"} com sucesso`
            });

            // Oculta o alerta após alguns segundos
            setTimeout(() => setResultadoPedido({ exibir: false, variante: "", mensagem: "" }), 5000);

        } catch (error) {
            console.error("Erro ao processar resposta:", error);
            setResultadoPedido({
                exibir: true,
                variante: "danger",
                mensagem: error.message || "Ocorreu um erro ao processar a solicitação."
            });
        } finally {
            setProcessandoAcaoId(null);
        }
    };

    // Define as colunas da tabela mapeando para o DTO do backend
    const columns = useMemo(() => [
        {
            header: "Aluno solicitante",
            accessor: "nomeAlunoRepresentante", filtravel: true, tipoFiltro: "autocomplete"
        },
        { header: "Tema", accessor: "tema", filtravel: true, tipoFiltro: "text" },
        { header: "Tipo de TG", accessor: "tipoTg", filtravel: true, tipoFiltro: "select" },
        {
            header: "Grupo",
            accessor: "nomesIntegrantes",
            filtravel: true,
            tipoFiltro: "autocomplete",
            render: (row) => (
                <Button variant='primary'
                    size='lm'
                    className="px-2"
                    onClick={() => handleOpen({ type: "INTEGRANTES", row })}
                >
                    Visualizar Integrantes
                </Button>
            )
        },
        {
            header: "Contato do aluno",
            accessor: "emailContato",
            render: (row) => (
                <Button
                    variant="primary"
                    size='lm'
                    className="px-2"
                    onClick={() => handleOpen({ type: "CONTATO", row })}
                >
                    Visualizar meios de contato
                </Button>
            )
        },
        {
            header: "Ações",
            render: (row) => {
                const isProcessando = processandoAcaoId === row.idSolicitacao;
                return (
                    <Stack direction="horizontal" gap={5} className="justify-content-center">
                        <Button variant="success" size="lm"
                            className="fs-5"
                            disabled={isProcessando}
                            onClick={() => handlePedidoOrientacao(row, true)}>
                            Aceitar
                        </Button>
                        <Button variant="danger" size="lm"
                            className="fs-5 text-black"
                            disabled={isProcessando}
                            onClick={() => handlePedidoOrientacao(row, false)}>
                            Recusar
                        </Button>
                    </Stack>
                );
            }
        }
    ], [handleOpen, processandoAcaoId]);

    // Função auxiliar para renderizar o conteúdo interno do Modal com base nos dados do backend
    const renderModalContent = () => {
        if (!selectedData || !selectedData.type) return null;

        const { type, row: data } = selectedData;

        if (type === 'CONTATO') {
            // Busca a URL do LinkedIn dentro do mapa de redes sociais de forma segura
            const linkedinUrl = data.redesSociais && (data.redesSociais["LINKEDIN"] || Object.values(data.redesSociais)[0]);

            return (
                <>
                    <Modal.Header className="d-flex justify-content-center" closeButton>
                        <div className="custom-modal-title">
                            <h5>Contatos de {data.nomeAlunoRepresentante}</h5>
                        </div>
                    </Modal.Header>

                    <Row className="m-0 text-center">
                        <Col xs={12} md={6} className="p-0 border-end border-dark">
                            <div className="fw-bold p-2 border-bottom border-dark fs-5" style={{ backgroundColor: '#ffe5cc' }}>
                                Email instucional
                            </div>
                            <ul className="list-group list-group-flush ">
                                <li className="list-group-item text-break fs-5" style={{ backgroundColor: '#ffecd9' }}>
                                    <a className="" href={`mailto:${data.emailContato}`}>{data.emailContato}</a>
                                </li>
                            </ul>
                        </Col>
                        <Col xs={12} md={6} className="p-0">
                            <div className="fw-bold p-2 border-bottom border-dark fs-5" style={{ backgroundColor: '#ffe5cc' }}>
                                LinkedIn
                            </div>
                            <ul className="list-group list-group-flush">
                                {linkedinUrl ? (
                                    <>
                                        <li className="list-group-item fs-5" style={{ backgroundColor: '#ffecd9' }}>
                                            <a href={linkedinUrl} target='_blank' rel='noopener noreferrer'>
                                                Acessar Perfil
                                            </a>
                                        </li>
                                    </>
                                ) : (
                                    <li className="list-group-item fs-5" style={{ backgroundColor: '#ffecd9' }}>
                                        <div className="fw-bold p-2 border-dark" style={{ backgroundColor: '#ffe5cc' }}>
                                            Não informado
                                        </div>
                                    </li>
                                )}
                            </ul>
                        </Col>
                    </Row >
                </>
            );
        }
        if (type === 'INTEGRANTES') {
            return (
                <>
                    <Modal.Header className="d-flex justify-content-center" closeButton>
                        <div className="custom-modal-title">
                            <h5>Integrantes do Grupo</h5>
                        </div>
                    </Modal.Header>
                    <ul className="list-group list-group-flush text-center">
                        {data.nomesIntegrantes?.map((item, index) => (
                            <li key={index} className="list-group-item fs-5" style={{ backgroundColor: '#ffecd9' }}>
                                {item}
                            </li>
                        ))}
                    </ul>
                </>
            );
        }
    };

    return (
        <>
            <UserNavBar
                userName='Orientador'
                maxWidth='1500px'
            />

            <Container className="mt-5" style={{ maxWidth: '1500px' }}>
                <h2 className='text-black p-3 fs-1 rounded-top-4 text-center mb-5'>Solicitações de Orientações</h2>

                {carregando ? (
                    <div className="d-flex flex-column align-items-center justify-content-center py-5">
                        <Spinner animation="border" variant="primary" style={{ width: '4rem', height: '4rem' }} />
                        <h4 className="mt-3 text-secondary">Carregando solicitações...</h4>
                    </div>
                ) : (
                    <TableComponent
                        colunas={columns}
                        dados={data}
                    />
                )}

                {/* MODAL DE INTEGRANTES E CONTATO */}
                <Modal
                    show={show}
                    onHide={handleClose}
                    centered
                    contentClassName="custom-modal-content"
                >
                    {renderModalContent()}
                </Modal>

                {/* Renderiza o alerta de erro ou sucesso na base da página */}
                {resultadoPedido.exibir && (
                    <Alert
                        variant={resultadoPedido.variante}
                        onClose={() => setResultadoPedido({ exibir: false, variante: "", mensagem: "" })}
                        dismissible
                        className="mt-3 fs-5 fw-medium text-center shadow-sm"
                    >
                        {resultadoPedido.mensagem}
                    </Alert>
                )}
            </Container>
        </>
    )

}
export default VisaoSolicitacaoOrientacao;