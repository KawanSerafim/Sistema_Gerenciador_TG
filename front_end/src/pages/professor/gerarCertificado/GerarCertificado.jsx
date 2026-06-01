import { Button, Container, Modal, ModalBody, Stack } from "react-bootstrap"
import UserNavBar from "../../../components/usernavbar/UserNavBar"
import TableComponent from "../../../components/table/TableComponent"
import "../../../styles/ModalIntegrantes.css"
import { useModal } from "../../../hooks/useModal/useModal"
const GerarCertificado = () => {

    //Instancia o hook do modal
    const {
        show,
        selectedData: selectedMembers,
        handleOpen,
        handleClose
    } = useModal();

    const columns = [
        { header: "Tema", accessor: "tema", filtravel: true, tipoFiltro: "text" },
        { header: "Tipo de TG", accessor: "tipoTG", filtravel: true, tipoFiltro: "select" },
        {
            header: "Grupo",
            accessor: "grupo",
            filtravel: true, tipoFiltro: "autocomplete",
            render: (row) => (

                <Button variant="primary"
                    size="lm"
                    className="px-2"
                    onClick={() => handleOpen(row.grupo)}
                >
                    Visualizar Integrantes
                </Button>

            )
        },
        {
            header: "Ações",
            accessor: "acoes",
            render: () => (
                <Stack direction="horizontal" gap={5} className="justify-content-center">
                    <Button variant="success" size="lm"
                        className="fs-5 fw-bold"
                        onClick={() => alert(`Imprimindo certificado...`)}>
                        Imprimir
                    </Button>
                </Stack>

            )
        }
    ]

    const grupos = [
        {
            id: 1, tema: "Tema 1", tipoTG: "Artigo",
            grupo: ["Aluno1", "Aluno2", "Aluno3"]
        },
        {
            id: 2, tema: "Tema 2", tipoTG: "Monografia",
            grupo: ["Aluno1", "Aluno2", "Aluno3", "Aluno4"]
        },
        {
            id: 3, tema: "Tema 3", tipoTG: "Monografia",
            grupo: ["Aluno1", "Aluno2", "Aluno3"]
        },
        {
            id: 4, tema: "Tema 4", tipoTG: "Desenvolvimento de Software",
            grupo: ["Aluno1", "Aluno2", "Aluno3", "Aluno4"]
        },
    ]

    return (
        <>
            <UserNavBar
                userName="Orientador"
                maxWidth="1500px"
            />
            <Container className="mt-3" style={{ maxWidth: "1500px" }}>
                <h2 className='text-black p-3 fs-1 text-center mb-3'>Certificados</h2>
                {/* Tabela */}
                <TableComponent
                    colunas={columns}
                    dados={grupos}
                />
                {/* Modal */}
                <Modal
                    show={show}
                    onHide={handleClose}
                    centered
                    contentClassName="custom-modal-content">
                    <Modal.Header closeButton>
                        <div className="custom-modal-title">
                            <h5>Integrantes:</h5>
                        </div>
                    </Modal.Header>
                    <ModalBody className="p-0">
                        <ul className="m-0 p-0">
                            {selectedMembers && selectedMembers.length > 0 ? (
                                selectedMembers?.map((member, index) => (
                                    <li key={index} className="custom-list-item">{member}</li>
                                ))
                            ) : (
                                <li className="custom-list-item text-muted">
                                    Nenhum integrante encontrado.
                                </li>
                            )
                            }
                        </ul>
                    </ModalBody>
                </Modal>
            </Container>
        </>
    )
}
export default GerarCertificado