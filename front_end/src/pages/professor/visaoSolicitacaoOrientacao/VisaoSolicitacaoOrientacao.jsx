import { Alert, Button, Container, Modal, Stack } from 'react-bootstrap';
import TableComponent from '../../../components/table/TableComponent'
import UserNavBar from '../../../components/usernavbar/UserNavBar';
import "../../../styles/ModalIntegrantes.css"
import { useModal } from '../../../hooks/useModal/useModal';
import { useState } from 'react';

const VisaoSolicitacaoOrientacao = () => {

    const [exibirAprovacao, setExibirAprovacao] = useState(false)
    const [grupoSelecionado, setGrupoSelecionado] = useState(null)
    const [resultadoPedido, setResultadoPedido] = useState(false)

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
        selectedData: selectedMembers,
        handleOpen,
        handleClose
    } = useModal();

    //Mocks temporarios
    const columns = [
        { header: "Aluno solicitante", accessor: "aluno", filtravel: true, tipoFiltro: "autocomplete" },
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
                    onClick={() => handleOpen(row.grupo)}
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
                userName='Orientador'
            />

            <Container className="mt-5" style={{ minWidth: '800px' }}>
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
                    <Modal.Header className="d-flex justify-content-center" closeButton >
                        <div className="custom-modal-title">
                            <h5>Integrantes:</h5>
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
                            )
                            }

                        </ul>
                    </Modal.Body>
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