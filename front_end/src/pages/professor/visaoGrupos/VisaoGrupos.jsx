import { useState } from "react";
import UserNavBar from "../../../components/usernavbar/UserNavBar";
import { Button, Container, Modal } from "react-bootstrap";
import TableComponent from "../../../components/table/TableComponent";

const VisaoGrupos = () => {
    //TODO: Buscar do backend os grupos
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
        { header: "IdGrupo", accessor: "id" },
        { header: "Tipo de TG", accessor: "tipoTg" },
        { header: "Tema", accessor: "tema" },
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
        { header: "Orientador", accessor: "orientador" }
    ]
    //TODO: Lidar com casos onde orientador esteja vazio
    const data = [
        {
            id: 1,
            tipoTg: "Artigo",
            tema: "Ética no desenvolvimento de IA",
            grupo: ["Joe", "Miranda", "Nat"],
            orientador: "Cristina"
        },
        {
            id: 2,
            tipoTg: "Monografia",
            tema: "A importância da Cibersegurança",
            grupo: ["Ana Maria", "Ashlhey", "James"],
            orientador: "Luciano"
        },
        {
            id: 3,
            tipoTg: "Monografia",
            tema: "Os perigos do Vibe Coding",
            grupo: ["Mariana Silva", "Samuel", "Geraldo"],
            orientador: "Sem orientador"
        }
    ]
    return (
        <>
            <UserNavBar
                /*Deve verificar qual o nome do usuario logado para ser passado ao componente*/
                userName='Sam'
            />

            <Container className="mt-5" style={{ minWidth: '800px' }}>

                <h2 className='text-black p-3 fs-1 rounded-top-4 text-center mb-5'>Visão dos Grupos</h2>

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

export default VisaoGrupos