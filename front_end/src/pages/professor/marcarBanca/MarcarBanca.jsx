import { Alert, Button, Container, Form } from "react-bootstrap"
import TableComponent from "../../../components/table/TableComponent"
import AddIcon from "../../../assets/add.svg"
import CancelIcon from "../../../assets/Cancel.svg"
import { useState } from "react"
import UserNavBar from "../../../components/usernavbar/UserNavBar"
import { useForm } from "../../../hooks/useForm"


// Função de validação fora do componente
const validarCampos = (valores) => {
    let erros = {};
    if (!valores.grupoId) erros.grupoId = "Selecione um grupo.";
    if (valores.membros.length === 0) erros.membros = "Adicione pelo menos um membro à banca.";
    if (!valores.data) erros.data = "A data é obrigatória.";
    if (!valores.hora) erros.hora = "A hora é obrigatória.";
    if (!valores.local.trim()) erros.local = "O local é obrigatório.";
    return erros;
};

const MarcarBanca = () => {

    // Validações
    const [exibirResultado, setExibirResultado] = useState(false)
    const [resultado, setResultado] = useState("");
    // Configuração do Hook de Validação
    const camposIniciais = {
        grupoId: "",
        membros: [],
        data: "",
        hora: "",
        local: ""
    };

    const { values, errors, handleChange, handleSubmit } = useForm(camposIniciais, validarCampos)

    // Mocks
    const grupos = [
        { grupoId: 1, tema: "Sistemas Distribuídos", alunos: [{ nome: "João" }, { nome: "Maria" }] },
        { grupoId: 2, tema: "Inteligência Artificial", alunos: [{ nome: "Carlos" }, { nome: "Ana" }] },
        { grupoId: 3, tema: "Cibersegurança", alunos: [{ nome: "Pedro" }, { nome: "Lucas" }] }
    ];

    const professores = [
        { id: 1, nome: "Prof. Luciano" },
        { id: 2, nome: "Profa. Cristina" },
        { id: 3, nome: "Prof. Colevati" }
    ];

    // Estados locais temporários (apenas para digitar antes de adicionar)
    const [profSelecionado, setProfSelecionado] = useState("");
    const [nomeExterno, setNomeExterno] = useState("");
    const [emailExterno, setEmailExterno] = useState("");
    const [telExterno, setTelExterno] = useState("");

    // Deriva o grupo selecionado a partir do valor no hook
    const selectedGrupo = grupos.find(g => g.grupoId === parseInt(values.grupoId));

    // Colunas das Tabelas
    const colunaTabelaGrupo = [{ header: "Alunos do Grupo", accessor: "nome" }];
    const colunaTabelaMembro = [
        { header: "Membro da Banca", accessor: "nome" },
        { header: "Tipo", accessor: "tipoLabel" },
        {
            header: "Remover",
            render: (row) => (
                <div className="text-center">
                    <img
                        src={CancelIcon}
                        alt="Remover"
                        style={{ cursor: 'pointer', width: '30px' }}
                        onClick={() => handleRemoverMembro(row.id)}
                    />
                </div>
            )
        }
    ];

    const handleAddProfessor = () => {
        if (profSelecionado) {
            const prof = professores.find(i => i.id === parseInt(profSelecionado));
            //Evita duplicatas
            if (prof && !values.membros.some(membro => membro.id === prof.id)) {
                const novosMembros = [...values.membros, {
                    id: prof.id,
                    tipo: "professor",
                    tipoLabel: "Professor Interno",
                    nome: prof.nome
                }];
                handleChange({ target: { name: "membros", value: novosMembros } });
                //Limpa o select
                setProfSelecionado("")

            }
        }
    }

    const handleAddMembroExterno = () => {
        if (nomeExterno.trim() && emailExterno.trim() !== "") {
            const jaExiste = values.membros.some(membro => membro.email === emailExterno);
            if (!jaExiste) {
                const novosMembros = [...values.membros, {
                    id: emailExterno, // Usando email como ID temporário
                    tipo: "membroExterno",
                    tipoLabel: "Membro Externo",
                    nome: nomeExterno,
                    email: emailExterno,
                    telefone: telExterno
                }];
                handleChange({ target: { name: 'membros', value: novosMembros } });

                // Limpa os inputs
                setNomeExterno("");
                setEmailExterno("");
                setTelExterno("");
            }
        } else {
            setResultado("Nome e e-mail do membro externo são obrigatórios.")
            setExibirResultado(true)
        }
    }


    const handleRemoverMembro = (id) => {
        const novosMembros = values.membros.filter((membro) => membro.id !== id);
        handleChange({ target: { name: "membros", value: novosMembros } })
    }

    const enviarParaBackend = (dadosValidados) => {
        console.log(`Enviando payload ao backend: ${dadosValidados}`)
        setExibirResultado(true)
        //Esconde depois de alguns segundos
        setTimeout(() => setExibirResultado(false), 5000);
    }

    return (
        <>
            <UserNavBar
                userName="Orientador"
                maxWidth="800px"
            />
            <Container className="mt-5" style={{ minWidth: "800px" }}  >
                <h2 className='bg-primary text-white p-3 fs-2 rounded-top-4 text-center m-0'>Marcar banca</h2>
                <Form
                    noValidate
                    onSubmit={handleSubmit(enviarParaBackend)}
                    className='form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm px-5'>

                    {/* Grupo */}
                    <Form.Group className="mb-4">
                        <div className="d-flex justify-content-center align-items-center mb-4 gap-2">
                            <Form.Label className="m-0 fw-bold fs-5 text-secondary">Grupo:</Form.Label>
                            <Form.Select
                                defaultValue=""
                                name="grupoId"
                                value={values.grupoId}
                                onChange={handleChange}
                                isInvalid={!!errors.grupoId}
                                className="w-50 bg-white text-black fw-normal fs-5 fs-5"
                            >
                                <option value="" disabled selected>Selecione o grupo</option>
                                {
                                    grupos.map((grupo) => (
                                        <option key={grupo.grupoId} value={grupo.grupoId}>{grupo.grupoId}</option>
                                    ))
                                }
                            </Form.Select>
                        </div>

                        {errors.grupoId && <div className="text-danger text-center fw-bold mt-1">{errors.grupoId}</div>}

                    </Form.Group>

                    {/*Após selecionado exibir o tema e preencher a tabela com os nomes dos alunos*/}
                    {selectedGrupo && (
                        <div className="text-center mb-4">
                            <h5 className="text-secondary fs-5 fw-bold">[{selectedGrupo.tema}]</h5>
                            <div className="mt-3">
                                {/* Tabela de Integrantes do grupo */}
                                <TableComponent
                                    colunas={colunaTabelaGrupo}
                                    dados={selectedGrupo.alunos}
                                />
                            </div>
                        </div>
                    )}

                    {/* <h6 className="fw-bold text-secondary fs-5 mt-4 mb-3 text-center">Membros da banca: </h6> */}
                    <hr className="my-4 border-secondary" />
                    <h6 className="fw-bold text-secondary fs-4 mb-4 text-center">Composição da Banca</h6>
                    {/* Professor */}
                    <div className="d-flex align-items-center mb-3 gap-2">
                        <Form.Label className="m-0 fw-bold text-secondary fs-5" style={{ width: "130px" }}>Professor: </Form.Label>
                        <Form.Select
                            value={profSelecionado}
                            onChange={(e) => setProfSelecionado(e.target.value)}
                            className="bg-white text-black fw-normal fs-5 flex-grow-1"
                        >
                            <option value="" disabled selected>Selecione o professor</option>
                            {
                                professores.map((professor) => (
                                    <option key={professor.id} value={professor.id}>{professor.nome}</option>
                                ))
                            }
                        </Form.Select>
                        <Button
                            variant="link"
                            className="p-0 text-primary"
                            onClick={handleAddProfessor}
                        >
                            <img src={AddIcon} alt="Adicionar professor" width={'55rem'} />
                        </Button>
                    </div>
                    {/* Membro Externo */}
                    <div className="d-flex align-items-center mb-4 gap-2">
                        {/* TODO: Verificar se é necessario pegar o nome do membro externo ou se será aceito apenas quem esta no sistema */}
                        <Form.Label className="m-0 fw-bold text-secondary fs-5" style={{ width: "130px" }}>Membro Externo: </Form.Label>
                        <Form.Control
                            type="text"
                            placeholder="Nome completo"
                            value={nomeExterno}
                            onChange={(e) => setNomeExterno(e.target.value)}
                            className="bg-white text-black fw-normal fs-5 flex-grow-1 "
                        />
                        <Form.Control
                            type="email"
                            placeholder="Email"
                            value={emailExterno}
                            onChange={(e) => setEmailExterno(e.target.value)}
                            className="bg-white text-black fw-normal fs-5 flex-grow-1 "
                        />
                        <Form.Control
                            type="tel"
                            placeholder="Telefone"
                            value={telExterno}
                            onChange={(e) => setTelExterno(e.target.value)}
                            className="bg-white text-black fw-normal fs-5 flex-grow-1 "
                        />
                        <Button variant="link" className="p-0 text-primary"
                            onClick={handleAddMembroExterno}>
                            <img src={AddIcon} alt="Adicionar membro" width={'55rem'} />
                        </Button>
                    </div>

                    {/* Tabela membros */}
                    <div className="mb-4">

                        {values.membros.length > 0 ? (
                            <div className="mb-4">
                                <TableComponent
                                    colunas={colunaTabelaMembro}
                                    dados={values.membros}
                                />
                            </div>
                        ) : (
                            <div className="text-center p-3 border rounded bg-light text-muted">
                                Nenhum membro adicionado à banca ainda.
                            </div>
                        )}
                        {errors.membros && <div className="text-danger text-center fw-bold mt-1">{errors.membros}</div>}
                    </div>
                    <hr className="my-4 border-secondary" />
                    {/* Horario e Local */}
                    <div className="d-flex align-items-center justify-content-between mb-4 gap-2 fw-bold text-secondary">
                        <div className="d-flex align-items-center gap-2">
                            <Form.Group>

                                <Form.Label className="m-0 fw-bold fs-5">Data:</Form.Label>
                                <Form.Control
                                    type="date"
                                    name="data"
                                    value={values.data}
                                    onChange={handleChange}
                                    isInvalid={!!errors.data}
                                />
                                <Form.Control.Feedback type="invalid">{errors.data}</Form.Control.Feedback>
                            </Form.Group>
                        </div>
                        <div className="d-flex align-items-center gap-2">
                            <Form.Group>

                                <Form.Label className="m-0  fw-bold fs-5">Hora:</Form.Label>
                                <Form.Control
                                    type="time"
                                    name="hora"
                                    value={values.hora}
                                    onChange={handleChange}
                                    isInvalid={!!errors.hora}
                                />
                                <Form.Control.Feedback type="invalid">{errors.hora}</Form.Control.Feedback>
                            </Form.Group>
                        </div>
                        <div className="d-flex align-items-center gap-2">
                            <Form.Group>

                                <Form.Label className="m-0 fw-bold fs-5">Local:</Form.Label>
                                <Form.Control
                                    type="text"
                                    name="local"
                                    placeholder="Ex: Sala 111 ou Link Teams"
                                    value={values.local}
                                    onChange={handleChange}
                                    isInvalid={!!errors.local}
                                    className="bg-white text-black fw-normal fs-5 flex-grow-1 "
                                />
                                <Form.Control.Feedback type="invalid">{errors.local}</Form.Control.Feedback>
                            </Form.Group>
                        </div>
                    </div>
                    {/* Btn de envio */}
                    <div className="d-flex justify-content-center mt-5">
                        <Button type="submit" variant="primary" className="w-50 fw-bold fs-5 py-2">
                            Marcar banca
                        </Button>
                    </div>
                </Form>
                {/* Renderiza o alerta de sucesso após passar nas validações */}
                {exibirResultado && (
                    <Alert variant={resultado === "" ? "success" : "danger"} onClose={() => setExibirResultado(false)} dismissible className="mt-3" >
                        Banca marcada com sucesso!                    </Alert>
                )}
            </Container>
        </>
    )
}
export default MarcarBanca