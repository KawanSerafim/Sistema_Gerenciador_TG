import { Button, Col, Container, Form, Modal, Row, Stack } from "react-bootstrap";
import UserNavBar from "../../../components/usernavbar/UserNavBar";
import TableComponent from "../../../components/table/TableComponent";
import { useState } from "react";


const VisaoBancasArtigos = () => {
    // Estado para controlar qual modal está aberto e com quais dados
    const [modalState, setModalState] = useState({
        show: false,
        type: null, // 'INTEGRANTES', 'MEMBROS', 'NOTA'
        data: null
    });

    const handleClose = () => setModalState({ show: false, type: null, data: null });
    const handleOpen = (type, rowData) => setModalState({ show: true, type, data: rowData });

    //Mocks temporarios
    const columns = [
        { header: "Tema", accessor: "tema" },
        { header: "Tipo de TG", accessor: "tipoTG" },
        {
            header: "Grupo",
            // Render customizado para o botão vermelho
            render: (row) => (
                <Button variant='primary'
                    size='lm'
                    className="px-2"
                    //Passa a lista dos integrantes do grupo da linha selecionada
                    onClick={() => handleOpen("INTEGRANTES", row)}
                >
                    Visualizar Integrantes
                </Button>
            )
        },
        { header: "Data", accessor: "data" },
        {
            header: "Membros da Banca",
            // Render customizado para o botão vermelho
            render: (row) => (
                <Button variant='primary'
                    size='lm'
                    className="px-2"
                    disabled={!row.membros || row.membros.length === 0}
                    //Passa a lista dos membros da banca do grupo da linha selecionada
                    onClick={() => handleOpen("MEMBROS", row)}
                >
                    Visualizar Membros
                </Button>
            )
        },
        { header: "Situação", accessor: "situacao" },
        {
            header: "Ações",
            // Render customizado para os botões de Aceitar/Recusar
            render: (row) => {
                const isBancaRealizada = row.situacao === "Banca realizada";
                const isArtigo = row.tipoTg === "Artigo";

                return (
                    <Stack direction="horizontal" gap={5} className="justify-content-center">
                        <Button variant={isBancaRealizada ? "success" : "light"}
                            size="lm"
                            disabled={!isBancaRealizada}
                            className={isBancaRealizada ? "fw-bold text-black" : "text-muted border"}
                            onClick={() => handleOpen("NOTA", row)}>
                            Atribuir Nota
                        </Button>
                        <Button
                            variant={isArtigo ? "light" : "danger"}
                            size="lm"
                            disabled={isArtigo}
                            className={isArtigo ? "text-muted border" : "text-black"}
                            onClick={() => alert(`Cancelou banca do ID ${row.id}`)}>
                            Cancelar Banca
                        </Button>
                    </Stack>
                )
            }
        }
    ]
    const data = [
        {
            id: 1,
            tema: "Ética no desenvolvimento de IA",
            disciplina: "TG 1",
            tipoTG: "Artigo",
            grupo: ["Joe", "Miranda", "Nat"],
            data: "[Data de publicação]",
            membros: ["Professor 1", "Professor 2"],
            situacao: "Artigo a ser publicado"
        },
        {
            id: 2,
            tema: "A importância da Cibersegurança",
            disciplina: "TG 2",
            tipoTG: "Monografia",
            grupo: ["Ana Maria", "Ashlhey", "James"],
            data: "25/07/2026 18:30",
            membros: ["Membro 1 - Professor", "Membro 2 - Membro externo", "Membro 3 - Professor"],
            situacao: "Banca realizada"

        },
        {
            id: 3,
            tema: "Os perigos do Vibe Coding",
            disciplina: "TG 2",
            tipoTG: "Desenvolvimento de Software",
            grupo: ["Mariana Silva", "Samuel", "Geraldo"],
            data: "26/07/2026 19:00",
            membros: ["Professor Orientador", "Professor Convidado"],
            situacao: "Banca marcada"
        }
    ]

    // Função auxiliar para renderizar o conteúdo interno do Modal correto
    const renderModalContent = () => {
        if (!modalState.data) return null;
        const { type, data } = modalState;

        if (type === 'INTEGRANTES' || type === 'MEMBROS') {
            const listData = type === 'INTEGRANTES' ? data.grupo : data.membros;
            const title = type === 'INTEGRANTES' ? 'Integrantes do Grupo' : 'Membros da banca';

            return (
                <>
                    <div className="d-flex justify-content-between align-items-center p-2" style={{ backgroundColor: '#ffe5cc' }}>
                        <span className="fw-bold mx-auto">{title}</span>
                        <Button variant="danger" size="sm" onClick={handleClose}>Fechar</Button>
                    </div>
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
                    <div className="d-flex justify-content-between align-items-center p-2" style={{ backgroundColor: '#ffe5cc' }}>
                        <span className="fw-bold">{`[ ${data.tema} ] - [ ${data.disciplina} ]`}</span>
                        <Button variant="danger" size="sm" onClick={handleClose}>Fechar</Button>
                    </div>
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
                                    className="text-center w-50"
                                />
                                <Button variant="success" className="fw-bold text-black px-4">
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
                userName='Sam'
            />

            <Container className="mt-5" style={{ minWidth: '800px' }}>
                <TableComponent
                    columns={columns}
                    data={data}
                />
                <Modal
                    show={modalState.show}
                    onHide={handleClose}
                    centered
                    contentClassName="border-dark shadow-lg"
                    // Fundo bege claro
                    style={{ '--bs-modal-bg': '#ffecd9' }}
                >
                    {renderModalContent()}
                </Modal>
            </Container>
        </>
    )
}
export default VisaoBancasArtigos