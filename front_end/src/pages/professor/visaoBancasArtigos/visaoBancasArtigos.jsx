import { Button, Container, Modal, Stack } from "react-bootstrap";
import { useModal } from "../../../hooks/useModal"
import UserNavBar from "../../../components/usernavbar/UserNavBar";
import TableComponent from "../../../components/table/TableComponent";


const visaoBancasArtigos = () => {
    //TODO: //columns = buscara do backend  //data = buscara do backend

    //Modal integrantes
    //Usando hook do modal
    const {
        show,
        selectedData: selectedMembers,
        handleOpen,
        handleClose
    } = useModal;

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
                    onClick={() => handleOpen(row.grupo)}
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
                    //Passa a lista dos membros da banca do grupo da linha selecionada
                    onClick={() => handleOpen(row.grupo)}
                >
                    Visualizar Membros
                </Button>
            )
        },
        { header: "Situação", accessor: "situacao" },
        {
            header: "Ações",
            // Render customizado para os botões de Aceitar/Recusar
            render: (row) => (
                <Stack direction="horizontal" gap={5} className="justify-content-center">
                    <Button variant="success" size="lm"
                        className="fs-5"
                        onClick={() => alert(`Aceitou ${row.aluno}`)}>
                        Atribuir Nota
                    </Button>
                    <Button variant="danger" size="lm"
                        className="fs-5 text-black"
                        onClick={() => alert(`Recusou ${row.aluno}`)}>
                        Cancelar Banca
                    </Button>
                </Stack>
            )
        }
    ]
    const data = [
        {
            id: 1,
            tema: "Ética no desenvolvimento de IA",
            tipoTg: "Artigo",
            grupo: ["Joe", "Miranda", "Nat"],
            data: "",
            membros: [],
            situacao: "Artigo a ser publicado"
        },
        {
            id: 2,
            tema: "A importância da Cibersegurança",
            tipoTg: "Monografia",
            grupo: ["Ana Maria", "Ashlhey", "James"],
            data: "25/07/2026 18:30",
            membros: [],
            situacao: "Artigo a ser publicado"

        },
        {
            id: 3,
            tema: "Os perigos do Vibe Coding",
            tipoTg: "Monografia",
            grupo: ["Mariana Silva", "Samuel", "Geraldo"],
            data: "26/07/2026 19:00",
            membros: [],
            situacao: "Artigo a ser publicado"
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
                            <h5>Bancas e Artigos de TG:</h5>
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
export default visaoBancasArtigos