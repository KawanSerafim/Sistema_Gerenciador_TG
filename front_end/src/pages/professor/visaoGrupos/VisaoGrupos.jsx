import UserNavBar from "../../../components/usernavbar/UserNavBar";
import { Button, Col, Container, Form, Modal, Row } from "react-bootstrap";
import TableComponent from "../../../components/table/TableComponent";
import { useModal } from "../../../hooks/useModal/useModal";
import { useMemo, useState } from "react";

const VisaoGrupos = () => {

    // Estado para o nosso filtro específico da página
    const [somenteSemGrupo, setSomenteSemGrupo] = useState(false);

    //TODO: Buscar do backend os grupos
    //columns = buscara do backend
    //data = buscara do backend
    //Modal integrantes

    const {
        show,
        selectedData: selectedMembers,
        handleOpen,
        handleClose
    } = useModal();
    //Mocks temporarios
    const columns = useMemo(() => [
        { header: "IdGrupo", accessor: "id", filtravel: true, tipoFiltro: "text" },
        { header: "Tipo de TG", accessor: "tipoTg", filtravel: true, tipoFiltro: "select" },
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
                    // Como row.grupo é um array, pega a primeira posição [0]
                    return <span className="fw-medium text-black fs-6">{row.grupo[0]}</span>;
                }

                // Se for um grupo normal, exibe o botão
                return (
                    <Button
                        variant='primary'
                        size='sm'
                        className="px-2"
                        onClick={() => handleOpen(row.grupo)}
                    >
                        Visualizar Integrantes
                    </Button>
                );
            }
        },
        { header: "Orientador", accessor: "orientador", filtravel: true, tipoFiltro: "select" }
    ], [handleOpen]); // Depende do handleOpen do modal

    // TODO: Quando integrar com o backend, isso vai virar um: const [data, setData] = useState([])
    const data = useMemo(() => [
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
        },
        {
            id: "-",
            tipoTg: "",
            tema: "",
            grupo: ["Thiago Oliveira"], // O nome do aluno sozinho
            orientador: ""
        }
    ], []); // Array de dependências vazio = cria esse mock apenas 1 vez ao carregar a página

    const dadosFiltrados = useMemo(() => {
        if (somenteSemGrupo) {
            // Filtra apenas onde não tem tema ou não tem orientador definido formalmente
            return data.filter(item => !item.tema || item.tema.trim() === "");
        }
        return data; // Se o check estiver desligado, retorna todos (a tabela cuida dos outros filtros)
    }, [somenteSemGrupo, data])


    return (
        <>
            <UserNavBar
                /*Deve verificar qual o nome do usuario logado para ser passado ao componente*/
                userName='Professor de TG'
                maxWidth="1200px"
            />

            <Container className="mt-3" style={{ maxWidth: '1200px' }}>

                <h2 className='text-black p-3 fs-1 rounded-top-4 text-center mb-5'>Visão dos Grupos</h2>

                {/* Filtro específico da página (Fora da tabela) */}
                <Row className="mb-4">
                    <Col className="d-flex justify-content-end">
                        <Form.Check
                            type="switch"
                            id="switch-sem-grupo"
                            label="Exibir apenas alunos sem grupo"
                            className="fs-5 fw-bold text-secondary"
                            checked={somenteSemGrupo}
                            onChange={(e) => setSomenteSemGrupo(e.target.checked)}
                        />
                    </Col>
                </Row>

                <TableComponent
                    colunas={columns}
                    dados={dadosFiltrados}
                />
                {/* Modal integrantes */}
                <Modal show={show} onHide={handleClose} contentClassName="custom-modal-content">

                    <Modal.Header className="d-flex justify-content-center" closeButton>
                        <div className="custom-modal-title">
                            <h5>{somenteSemGrupo ? "Aluno:" : "Integrantes:"}</h5>
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
            </Container>
        </>
    )
}

export default VisaoGrupos