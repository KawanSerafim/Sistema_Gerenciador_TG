import { Button, Container, Modal, Stack } from 'react-bootstrap';
import TableComponent from '../../../components/table/TableComponent'
import UserNavBar from '../../../components/usernavbar/UserNavBar';
import { useState } from 'react';
import "./VisaoSolicitacaoOrientacao.css"

const VisaoSolicitacaoOrientacao = () => {
    //columns = buscara do backend
    //data = buscara do backend
    //Modal integrantes
    const [show, setShow] = useState(false);
    const [selectedMembers, setSelectedMembers] = useState([]);
    const handleClose = () => setShow(false);
    const handleShowMembers = (members) => {
        //Salva os integrantes a serem exibidos
        setSelectedMembers(members);
        //Exibe o modal
        setShow(true);
    }
    //Mocks temporarios
    const columns = [
        { header: "Aluno solicitante", accessor: "aluno" },
        { header: "Tema", accessor: "tema" },
        { header: "Tipo de TG", accessor: "tipoTg" },
        {
            header: "Grupo",
            // Render customizado para o botão vermelho
            render: (row) => (
                <Button variant='primary'
                    size='lm'
                    className="px-2"
                    //Passa a lista dos integrantes do grupo da linha selecionada
                    onClick={() => handleShowMembers(row.grupo)}
                >
                    Visualizar Integrantes
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
                        onClick={() => alert(`Aceitou ${row.aluno}`)}>
                        Aceitar
                    </Button>
                    <Button variant="danger" size="lm"
                        className="fs-5 text-black"
                        onClick={() => alert(`Recusou ${row.aluno}`)}>
                        Recusar
                    </Button>
                </Stack>
            )
        }
    ]
    const data = [
        {
            id: 1,
            aluno: "Joe",
            tema: "Ética no desenvolvimento de IA",
            tipoTg: "Artigo",
            grupo: ["Joe", "Miranda", "Nat"]
        },
        {
            id: 2,
            aluno: "Ana Maria",
            tema: "A importância da Cibersegurança",
            tipoTg: "Monografia",
            grupo: ["Ana Maria", "Ashlhey", "James"]
        },
        {
            id: 3,
            aluno: "Mariana Silva",
            tema: "Os perigos do Vibe Coding",
            tipoTg: "Monografia",
            grupo: ["Mariana Silva", "Samuel", "Geraldo"]
        }
    ]

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
                    show={show}
                    onHide={handleClose}

                    contentClassName="custom-modal-content"
                >
                    <Modal.Header className="custom-modal-header" >
                        <div className="custom-modal-title">
                            <h5>Integrantes:</h5>
                        </div>
                        <Button variant="secondary"
                            onClick={handleClose}
                            className="custom-close-btn"
                        >
                            Fechar
                        </Button>
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
                            )
                            }

                        </ul>
                    </Modal.Body>
                </Modal>
            </Container>
        </>
    )
}
export default VisaoSolicitacaoOrientacao;