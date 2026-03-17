import { Alert, Button, Col, Container, Modal, Row, Stack } from 'react-bootstrap';
import TableComponent from '../../../components/table/TableComponent'
import UserNavBar from '../../../components/usernavbar/UserNavBar';
import "../../../styles/ModalIntegrantes.css"
import { useModal } from '../../../hooks/useModal/useModal';
import { useMemo, useState } from 'react';

const VisaoSolicitacaoOrientacao = () => {

    const [exibirAprovacao, setExibirAprovacao] = useState(false)
    const [grupoSelecionado, setGrupoSelecionado] = useState(null)
    const [resultadoPedido, setResultadoPedido] = useState(false)

    //Dados vem antes das funções que o usam
    const data = useMemo(() => [
        {
            id: 1,
            aluno: "Joe",
            contato: { email: "joe@fatec.sp.gov.br", linkedin: "http://linkedin/in/joe" },
            tema: "Ética no desenvolvimento de IA",
            tipoTg: "Artigo",
            grupo: ["Joe", "Miranda", "Nat"]
        },
        {
            id: 2,
            aluno: "Ana Maria",
            contato: { email: "ana.maria@fatec.sp.gov.br", linkedin: "" },
            tema: "A importância da Cibersegurança",
            tipoTg: "Monografia",
            grupo: ["Ana Maria", "Ashlhey", "James"]
        },
        {
            id: 3,
            aluno: "Mariana Silva",
            contato: { email: "mariana.silva@fatec.sp.gov.br", linkedin: "http://linkedin/in/mariana-silva" },
            tema: "Os perigos do Vibe Coding",
            tipoTg: "Monografia",
            grupo: ["Mariana Silva", "Samuel", "Geraldo"]
        }
    ], [])


    const handlePedidoOrientacao = (aluno, resultado) => {
        const dadosGrupo = data.filter(i => i.aluno == aluno).at(0)
        setGrupoSelecionado(dadosGrupo)
        setResultadoPedido(resultado)
        setExibirAprovacao(true)
    }

    //TODO: //columns = buscara do backend  //data = buscara do backend   
    //Modal integrantes
    //Usando hook do modal
    const {
        show,
        selectedData,
        handleOpen,
        handleClose
    } = useModal(null);

    //Mocks temporarios
    const columns = useMemo(() => [
        {
            header: "Aluno solicitante",
            accessor: "aluno", filtravel: true, tipoFiltro: "autocomplete"
        },
        { header: "Tema", accessor: "tema", filtravel: true, tipoFiltro: "text" },
        { header: "Tipo de TG", accessor: "tipoTg", filtravel: true, tipoFiltro: "select" },
        {
            header: "Grupo",
            accessor: "grupo",
            filtravel: true,
            tipoFiltro: "autocomplete",
            // Render customizado para o botão vermelho
            render: (row) => (
                <Button variant='primary'
                    size='lm'
                    className="px-2"
                    //Passa a lista dos integrantes do grupo da linha selecionada
                    onClick={() => handleOpen({ type: "INTEGRANTES", row })}
                >
                    Visualizar Integrantes
                </Button>
            )
        },
        {
            header: "Contato do aluno",
            accessor: "contato",
            render: (row) => (
                // O botão variant="link" parece texto normal, mas é clicável!
                <Button
                    variant="primary"
                    size='lm'
                    className="px-2  text-decoration-none"
                    onClick={() => handleOpen({ type: "CONTATO", row })}
                >
                    Visualizar meios de contato
                </Button>
            )
        },
        {
            header: "Ações",
            // Render customizado para os botões de Aceitar/Recusar
            render: (row) => (
                <Stack direction="horizontal" gap={5} className="justify-content-center">
                    <Button variant="success" size="lm"
                        className="fs-5"
                        onClick={() => handlePedidoOrientacao(row.aluno, true)}>
                        Aceitar
                    </Button>
                    <Button variant="danger" size="lm"
                        className="fs-5 text-black"
                        onClick={() => handlePedidoOrientacao(row.aluno, false)}>
                        Recusar
                    </Button>
                </Stack>
            )
        }
    ], [handleOpen]);


    // Função auxiliar para renderizar o conteúdo interno do Modal correto
    const renderModalContent = () => {
        if (!selectedData || !selectedData.type) return null;

        // Desestruturamos o objeto que enviamos no handleOpen
        const { type, row: data } = selectedData;

        if (type === 'CONTATO') {
            return (
                <>
                    <Modal.Header className="d-flex justify-content-center" closeButton>
                        <div className="custom-modal-title">
                            <h5>Contatos de {data.aluno}</h5>
                        </div>
                    </Modal.Header>

                    <Row className="m-0 text-center">
                        <Col xs={12} md={6} className="p-0 border-end border-dark">
                            <div className="fw-bold p-2 border-bottom border-dark fs-5" style={{ backgroundColor: '#ffe5cc' }}>
                                Email instucional
                            </div>
                            <ul className="list-group list-group-flush ">
                                <li className="list-group-item text-break fs-5" style={{ backgroundColor: '#ffecd9' }}>
                                    <a className="" href={`mailto:${data.contato.email}`}>{data.contato.email}</a>
                                </li>
                            </ul>
                        </Col>
                        <Col xs={12} md={6} className="p-0">
                            <div className="fw-bold p-2 border-bottom border-dark fs-5" style={{ backgroundColor: '#ffe5cc' }}>
                                LinkedIn
                            </div>
                            <ul className="list-group list-group-flush">
                                {data.contato.linkedin ? (
                                    <>
                                        <li className="list-group-item fs-5" style={{ backgroundColor: '#ffecd9' }}>
                                            <a href={data.contato.linkedin} target='_blank' rel='noopener noreferrer'>
                                                Acessar Perfil
                                            </a>
                                        </li>
                                    </>
                                ) : (
                                    <div className="fw-bold p-2 border-bottom border-dark" style={{ backgroundColor: '#ffe5cc' }}>
                                        Não informado
                                    </div>
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
                        {data.grupo.map((item, index) => (
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
                /*Deve verificar qual o nome do usuario logado para ser passado ao componente*/
                userName='Orientador'
            />

            <Container className="mt-5" style={{ minWidth: '800px' }}>
                <h2 className='text-black p-3 fs-1 rounded-top-4 text-center mb-5'>Solicitações de Orientações</h2>
                <TableComponent
                    colunas={columns}
                    dados={data}
                />
                {/* MODAL DE INTEGRANTES */}
                <Modal
                    show={show}
                    onHide={handleClose}

                    contentClassName="custom-modal-content"
                >
                    {renderModalContent()}
                </Modal>
                {/* Renderiza o alerta de sucesso após passar nas validações */}
                {exibirAprovacao && (
                    <Alert variant="success" onClose={() => setExibirAprovacao(false)} dismissible className="mt-3" >
                        {`Orientação do grupo do aluno: ${grupoSelecionado.aluno} foi ${resultadoPedido ? "aceita" : "recusada"} com sucesso`}
                    </Alert>
                )}
            </Container>
        </>
    )
}
export default VisaoSolicitacaoOrientacao;