import { Alert, Button, Col, Container, Form, Modal, Row, Stack } from "react-bootstrap";
import UserNavBar from "../../../components/usernavbar/UserNavBar";
import TableComponent from "../../../components/table/TableComponent";
import { useMemo, useState } from "react";
import { useModal } from "../../../hooks/useModal/useModal";
import { bloquearCaracteresInputNumber } from "../../../utils/utils";

// const validarCampos = (valores) => {
//     let erros = {}
//     const nota = parseInt(valores.nota)
//     //TODO: Lidar com validacao do campo nota, em casos que não é enviado nota
// }

const VisaoBancasArtigos = () => {
    //TODO: usar hook de validação

    const [exibirResultado, setExibirResultado] = useState(false)
    const [temaSelecionado, setTemaSelecionado] = useState(null)
    const [resultado, setResultado] = useState(false)

    const { show, selectedData, handleOpen, handleClose } = useModal(null)


    //Mocks temporarios
    const columns = [
        { header: "Tema", accessor: "tema", filtravel: true, tipoFiltro: "text" },
        { header: "Tipo de TG", accessor: "tipoTG", filtravel: true, tipoFiltro: "select" },
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
                    //Passa um objeto com o tipo do modal que será renderizado ao abrir
                    onClick={() => handleOpen({ type: "INTEGRANTES", row })}
                >
                    Visualizar Integrantes
                </Button>
            ),

        },
        {
            header: "Data",
            accessor: "data",
            filtravel: true,
            tipoFiltro: "select"
        },
        {
            header: "Membros da Banca",
            accessor: "membros",
            filtravel: true,
            tipoFiltro: "autocomplete",
            // Render customizado para o botão vermelho
            render: (row) => (
                <Button variant='primary'
                    size='lm'
                    className="px-2"
                    disabled={!row.membros || row.membros.length === 0}
                    //Passa um objeto com o tipo do modal que será aberto
                    onClick={() =>
                        handleOpen({ type: "MEMBROS", row })
                    }
                >
                    Visualizar Membros
                </Button>
            ),

        },
        {
            header: "Situação",
            accessor: "situacao",
            filtravel: true,
            tipoFiltro: "select"
        },
        {
            header: "Ações",
            // Render customizado para os botões de Aceitar/Recusar
            render: (row) => {
                const isBancaRealizada = row.situacao === "Pré Banca realizada" || row.situacao === "Banca realizada";
                const isArtigo = row.tipoTg === "Artigo";

                return (
                    <Stack direction="horizontal" gap={5} className="justify-content-center">
                        <Button variant={isBancaRealizada ? "success" : "light"}
                            size="lm"
                            disabled={!isBancaRealizada}
                            className={isBancaRealizada ? "fw-bold text-black" : "text-muted border"}
                            //Passa um objeto com o tipo do modal que será aberto
                            onClick={() => {
                                handleOpen({ type: "NOTA", row })
                                setTemaSelecionado(row.tema)
                                setResultado(true)
                            }}>
                            Atribuir Nota
                        </Button>
                        <Button
                            variant={isArtigo ? "light" : "danger"}
                            size="lm"
                            disabled={isArtigo}
                            className={isArtigo ? "text-muted border" : "text-black"}
                            onClick={() => {
                                setTemaSelecionado(row.tema)
                                setResultado(false)
                                setExibirResultado(true)
                            }}>
                            Cancelar Avaliação
                        </Button>
                    </Stack>
                )
            }
        }
    ]
    const data = useMemo(() => [
        {
            id: 1,
            tema: "Ética no desenvolvimento de IA",
            disciplina: "TG2",
            tipoTG: "Artigo",
            grupo: ["Joe", "Miranda", "Nat"],
            data: "[Data de publicação]",
            membros: ["Professor 1", "Professor 2"],
            situacao: "Artigo a ser publicado"
        },
        {
            id: 2,
            tema: "A importância da Cibersegurança",
            disciplina: "TG1",
            tipoTG: "Monografia",
            grupo: ["Ana Maria", "Ashlhey", "James"],
            data: "25/07/2026 18:30",
            membros: ["Membro 1 - Professor", "Membro 2 - Membro externo", "Membro 3 - Professor"],
            situacao: "Pré Banca realizada"

        },
        {
            id: 3,
            tema: "Os perigos do Vibe Coding",
            disciplina: "TG2",
            tipoTG: "Desenvolvimento de Software",
            grupo: ["Mariana Silva", "Samuel", "Geraldo"],
            data: "26/07/2026 19:00",
            membros: ["Professor Orientador", "Professor Convidado"],
            situacao: "Banca marcada"
        }
    ], [])

    // Função auxiliar para renderizar o conteúdo interno do Modal correto
    const renderModalContent = () => {
        if (!selectedData || !selectedData.type) return null;

        // Desestruturamos o objeto que enviamos no handleOpen
        const { type, row: data } = selectedData;

        if (type === 'INTEGRANTES' || type === 'MEMBROS') {
            const listData = type === 'INTEGRANTES' ? data.grupo : data.membros;
            const title = type === 'INTEGRANTES' ? 'Integrantes do Grupo' : 'Membros da banca';

            return (
                <>
                    <Modal.Header className="d-flex justify-content-center" closeButton>
                        <div className="custom-modal-title">
                            <h5>{title}</h5>
                        </div>
                    </Modal.Header>
                    <ul className="list-group list-group-flush text-center">
                        {listData.map((item, index) => (
                            <li key={index} className="list-group-item" style={{ backgroundColor: '#ffecd9' }}>
                                {item}
                            </li>
                        ))}
                    </ul>
                </>
            );
        }
        if (type === 'NOTA') {
            return (
                <>
                    <Modal.Header className="d-flex justify-content-center" closeButton>
                        <div className="custom-modal-title">
                            <span className="fw-bold">{`${data.tema} - ${data.disciplina.toUpperCase()}`}</span>
                        </div>
                    </Modal.Header>

                    <Row className="m-0 text-center">
                        <Col xs={6} className="p-0 border-end border-dark">
                            <div className="fw-bold p-2 border-bottom border-dark" style={{ backgroundColor: '#ffe5cc' }}>
                                Integrantes
                            </div>
                            <ul className="list-group list-group-flush">
                                {data.grupo.map((aluno, idx) => (
                                    <li key={idx} className="list-group-item" style={{ backgroundColor: '#ffecd9' }}>{aluno}</li>
                                ))}
                            </ul>
                        </Col>
                        <Col xs={6} className="p-0 d-flex flex-column align-items-center justify-content-center" style={{ backgroundColor: '#ffecd9' }}>
                            <div className="fw-bold p-2 border-bottom border-dark w-100" style={{ backgroundColor: '#ffe5cc' }}>
                                Nota do Grupo
                            </div>
                            <div className="p-4 d-flex flex-column align-items-center gap-3 w-100 h-100">
                                <Form.Control
                                    type="number"
                                    defaultValue={0}
                                    min={0}
                                    max={10}
                                    onKeyDown={bloquearCaracteresInputNumber}
                                    className="text-center w-50"
                                />
                                <Button variant="success" className="fw-bold text-black px-4" onClick={() => {
                                    setExibirResultado(true)
                                    // Fecha o modal após dar a nota
                                    handleClose();
                                }}>
                                    Confirmar Nota
                                </Button>
                            </div>
                        </Col>
                    </Row>
                </>
            );
        }
    };

    return (
        <>
            <UserNavBar
                /*Deve verificar qual o nome do usuario logado para ser passado ao componente*/
                userName='Orientador'
                maxWidth="1250px"
            />

            <Container className="mt-5" style={{ maxWidth: '1250px' }}>
                <h2 className='text-black p-3 fs-1 rounded-top-4 text-center mb-5'>Visão das Bancas</h2>
                <TableComponent
                    colunas={columns}
                    dados={data}
                />
                <Modal
                    show={show}
                    onHide={handleClose}
                    centered
                    contentClassName="custom-modal-content"
                    // Fundo bege claro
                    style={{ '--bs-modal-bg': '#ffecd9' }}
                >
                    {renderModalContent()}
                </Modal>
                {/* Renderiza o alerta de sucesso após passar nas validações */}
                {exibirResultado && (
                    <Alert variant={resultado ? "success" : "warning"} onClose={() => setExibirResultado(false)} dismissible className="mt-3" >
                        {resultado ? `Nota do grupo de tema: ${temaSelecionado} salva!`
                            : `Avaliação do grupo de tema: ${temaSelecionado} foi cancelada`}
                    </Alert>
                )}
            </Container>
        </>
    )
}
export default VisaoBancasArtigos