import { Alert, Button, Col, Container, Form, Row, Spinner } from "react-bootstrap";
import TableComponent from "../../../components/table/TableComponent";
import AddIcon from "../../../assets/add.svg";
import CancelIcon from "../../../assets/Cancel.svg";
import { useState, useEffect } from "react";
import UserNavBar from "../../../components/usernavbar/UserNavBar";

// RHF e Zod
import { useFieldArray, useForm, useWatch } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { camposSchema } from "../../../schemas/professor/marcarBanca/marcarBancaSchema";

// Importa a service com as chamadas da API
import { bancaService } from "../../../services/banca/bancaService";

const MarcarBanca = () => {

    // Estados da API e carregamento inicial
    const [grupos, setGrupos] = useState([]);
    const [professores, setProfessores] = useState([]);
    const [carregando, setCarregando] = useState(true);
    const [enviando, setEnviando] = useState(false);

    // Estado unificado de feedback (sucesso ou erro)
    const [resultadoPedido, setResultadoPedido] = useState({ exibir: false, variante: "", mensagem: "" });

    // Configuração do React Hook Form
    const {
        register,
        control,
        formState: { errors },
        handleSubmit,
        getValues,
        setValue,
        reset
    } = useForm({
        resolver: zodResolver(camposSchema),
        defaultValues: {
            grupoId: "",
            membros: [],
            data: "",
            hora: "",
            local: ""
        }
    });

    // Gerenciamento do array dinâmico de membros da banca
    const { fields: membrosFields, append, remove } = useFieldArray({
        control,
        name: "membros"
    });

    // Observa o grupo selecionado para atualizar a UI em tempo real
    const grupoId = useWatch({
        control,
        name: "grupoId",
    });

    // Busca os dados necessários ao carregar a página
    useEffect(() => {
        const carregarDadosIniciais = async () => {
            try {
                setCarregando(true);
                setResultadoPedido({ exibir: false, variante: "", mensagem: "" });

                // Dispara as duas requisições em paralelo para maior performance
                const [gruposData, professoresData] = await Promise.all([
                    bancaService.buscarGruposOrientados(),
                    bancaService.buscaProfessoresPorCargo("ORIENTADOR")
                ]);

                setGrupos(gruposData);

                // Padroniza a chave id para o select de professores
                const professoresFormatados = professoresData.map(prof => ({
                    id: prof.idProfessor || prof.id,
                    nome: prof.nome
                }));
                setProfessores(professoresFormatados);

            } catch (error) {
                console.error("Erro ao carregar dados:", error);
                setResultadoPedido({
                    exibir: true,
                    variante: "danger",
                    mensagem: "Não foi possível carregar os grupos ou professores. Atualize a página."
                });
            } finally {
                setCarregando(false);
            }
        };

        carregarDadosIniciais();
    }, []);

    // Deriva o grupo selecionado comparando como String (UUID)
    const selectedGrupo = grupos.find(g => String(g.idGrupo) === String(grupoId));

    // Formata a lista de alunos para o TableComponent que espera um array de objetos { nome: "..." }
    const alunosFormatados = selectedGrupo?.nomesAlunos?.map(nomeAluno => ({ nome: nomeAluno })) || [];

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
                        title={"Clique aqui para remover " + row.nome + " da banca"}
                        style={{ cursor: 'pointer', width: '30px' }}
                        onClick={() => {
                            const indexReal = membrosFields.findIndex(membro => membro.id === row.id);
                            if (indexReal !== -1) {
                                remove(indexReal);
                            }
                        }}
                    />
                </div>
            )
        }
    ];

    // Adiciona professor interno à banca
    const handleAddProfessor = () => {
        const profId = getValues("profSelecionado");

        if (profId) {
            // Compara IDs como string
            const prof = professores.find(i => String(i.id) === String(profId));
            const membrosAtuais = getValues("membros");

            // Evita duplicatas
            if (prof && !membrosAtuais.some(membro => String(membro.id) === String(prof.id))) {
                append({
                    id: prof.id,
                    tipo: "professor",
                    tipoLabel: "Professor Interno",
                    nome: prof.nome
                });
                setValue("profSelecionado", "");
            }
        }
    };

    // Adiciona membro externo à banca
    const handleAddMembroExterno = () => {
        const nome = getValues("nomeExterno") || "";
        const email = getValues("emailExterno") || "";
        const telefone = getValues("telExterno") || "";

        if (nome.trim() && email.trim() !== "") {
            const membrosAtuais = getValues("membros");
            const jaExiste = membrosAtuais.some(membro => membro.email === email);

            if (!jaExiste) {
                append({
                    id: email, // Usando email como ID temporário para listagem local
                    tipo: "membroExterno",
                    tipoLabel: "Membro Externo",
                    nome: nome,
                    email: email,
                    telefone: telefone
                });

                setValue("nomeExterno", "");
                setValue("emailExterno", "");
                setValue("telExterno", "");
                setResultadoPedido({ exibir: false, variante: "", mensagem: "" }); // Limpa erros anteriores
            }
        } else {
            setResultadoPedido({
                exibir: true,
                variante: "warning",
                mensagem: "Nome e e-mail do membro externo são obrigatórios para adição."
            });
        }
    };

    // Orquestra o envio do formulário ao backend
    const enviarParaBackend = async (dadosValidados) => {
        try {
            setEnviando(true);
            setResultadoPedido({ exibir: false, variante: "", mensagem: "" });

            // Filtra e formata as listas de membros separadamente conforme DTO Java
            const idsProfessoresConvidados = dadosValidados.membros
                .filter(m => m.tipo === "professor")
                .map(m => String(m.id));

            const convidadosExternos = dadosValidados.membros
                .filter(m => m.tipo === "membroExterno")
                .map(m => ({
                    nome: m.nome,
                    email: m.email,
                    telefone: m.telefone || ""
                }));

            // Monta o payload exato esperado pelo backend
            const payload = {
                idGrupo: dadosValidados.grupoId,
                data: dadosValidados.data,
                hora: dadosValidados.hora,
                local: dadosValidados.local,
                idsProfessoresConvidados,
                convidadosExternos
            };

            // Certifique-se de ter este método na sua professorService
            await bancaService.marcarBanca(payload);

            setResultadoPedido({
                exibir: true,
                variante: "success",
                mensagem: "Banca marcada com sucesso! Os convites foram enviados."
            });

            // Reseta o formulário após o sucesso
            reset();

        } catch (error) {
            console.error("Erro ao marcar banca:", error);
            setResultadoPedido({
                exibir: true,
                variante: "danger",
                mensagem: error.message || "Ocorreu um erro ao tentar marcar a banca. Tente novamente."
            });
        } finally {
            setEnviando(false);
        }
    };

    return (
        <>
            <UserNavBar
                userName="Orientador"
                maxWidth="1500px"
            />
            <Container className="mt-5" style={{ maxWidth: "1500px" }}  >
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Marcar banca</h2>
                <Form
                    noValidate
                    onSubmit={handleSubmit(enviarParaBackend)}
                    className='form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm px-5'>

                    {carregando ? (
                        <div className="d-flex flex-column align-items-center justify-content-center py-5">
                            <Spinner animation="border" variant="primary" style={{ width: '3rem', height: '3rem' }} />
                            <p className="mt-3 text-secondary fs-5">Carregando dados para agendamento...</p>
                        </div>
                    ) : (
                        <>
                            {/* Seleção de Grupo */}
                            <Form.Group className="mb-4">
                                <div className="d-flex justify-content-center align-items-center mb-4 gap-2">
                                    <Form.Label className="m-0 fw-bold fs-4 text-secondary">Grupo:</Form.Label>
                                    <Form.Select
                                        title="Selecione o grupo"
                                        name="grupoId"
                                        {...register("grupoId")}
                                        isInvalid={!!errors.grupoId}
                                        className="w-50 bg-white text-black fw-normal fs-5 fs-5"
                                        defaultValue=""
                                    >
                                        <option value="" disabled>Selecione o grupo</option>
                                        {
                                            grupos.map((grupo) => (
                                                <option key={grupo.idGrupo} value={grupo.idGrupo}>
                                                    {grupo.tema}
                                                </option>
                                            ))
                                        }
                                    </Form.Select>
                                </div>

                                {errors.grupoId && <div className="text-danger text-center fw-bold mt-1">{errors.grupoId?.message}</div>}

                            </Form.Group>

                            {/* Detalhes do Grupo Selecionado */}
                            {selectedGrupo && (
                                <div className="text-center mb-4">
                                    <h5 className="text-secondary fs-4 fw-bold">
                                        {selectedGrupo.tema} - {selectedGrupo.tipoTg}
                                    </h5>
                                    <div className="mt-3">
                                        <TableComponent
                                            colunas={colunaTabelaGrupo}
                                            dados={alunosFormatados}
                                        />
                                    </div>
                                </div>
                            )}

                            <hr className="my-4 border-secondary" />
                            <h6 className="fw-bold text-secondary fs-4 mb-4 text-center">Composição da Banca</h6>

                            {/* Adição de Professor Interno */}
                            <div className="d-flex align-items-center mb-3 gap-2">
                                <Form.Label className="m-0 fw-bold text-secondary fs-5" style={{ width: "130px" }}>Professor: </Form.Label>
                                <Form.Select
                                    title="Selecione os professores que participaram da banca"
                                    {...register("profSelecionado")}
                                    className="bg-white text-black fw-normal fs-5 flex-grow-1"
                                    defaultValue=""
                                >
                                    <option value="" disabled>Selecione o professor</option>
                                    {
                                        professores.map((professor) => (
                                            <option key={professor.id} value={professor.id}>{professor.nome}</option>
                                        ))
                                    }
                                </Form.Select>
                                <Button
                                    variant="link"
                                    title="Clique aqui para adicionar o professor a banca"
                                    className="p-0 text-primary"
                                    onClick={handleAddProfessor}
                                >
                                    <img src={AddIcon} alt="Adicionar professor" width={'55rem'} />
                                </Button>
                            </div>

                            {/* Adição de Membro Externo */}
                            <div className="d-flex align-items-center mb-2 gap-2">
                                <Form.Label className="m-0 fw-bold text-secondary fs-5" style={{ width: "130px" }}>Membro Externo: </Form.Label>
                                <Form.Control
                                    type="text"
                                    title="Digite o nome completo do membro externo"
                                    placeholder="Nome completo"
                                    {...register("nomeExterno")}
                                    className="bg-white text-black fw-normal fs-5"
                                />
                                <Form.Control
                                    type="email"
                                    title="Digite o email do membro externo"
                                    placeholder="Email"
                                    {...register("emailExterno")}
                                    className="bg-white text-black fw-normal fs-5 flex-grow-1 "
                                />
                                <Form.Control
                                    type="tel"
                                    title="Digite o nome telefone do membro externo"
                                    placeholder="Telefone"
                                    {...register("telExterno")}
                                    className="bg-white text-black fw-normal fs-5 flex-grow-1 "
                                />
                                <Button variant="link" className="p-0 text-primary"
                                    onClick={handleAddMembroExterno}
                                    title="Clique aqui para adicionar o membro externo a banca"
                                >
                                    <img src={AddIcon} alt="Adicionar membro" width={'55rem'} />
                                </Button>
                            </div>

                            {/* Tabela de Membros Adicionados */}
                            <div className="mb-4">
                                {membrosFields.length > 0 ? (
                                    <div className="mb-4">
                                        <TableComponent
                                            colunas={colunaTabelaMembro}
                                            dados={membrosFields}
                                        />
                                    </div>
                                ) : (
                                    <div className="text-center p-3 border rounded bg-light text-muted">
                                        Nenhum membro adicionado à banca ainda.
                                    </div>
                                )}
                                {errors.membros && <div className="text-danger text-center fw-bold mt-1">{errors.membros?.message}</div>}
                            </div>

                            <hr className="my-4 border-secondary" />

                            {/* Data, Hora e Local */}
                            <Row className="mb-4 g-3 align-items-start">
                                <Col md={4}>
                                    <Form.Group>
                                        <Form.Label className="m-0 fw-bold text-secondary fs-4">Data:</Form.Label>
                                        <Form.Control
                                            type="date"
                                            name="data"
                                            title="Digite a data da banca"
                                            {...register("data")}
                                            isInvalid={!!errors.data}
                                            className="bg-white text-black fw-normal fs-5 "
                                        />
                                        <Form.Control.Feedback type="invalid">{errors.data?.message}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                                <Col md={3}>
                                    <Form.Group>
                                        <Form.Label className="m-0 fw-bold text-secondary fs-4">Hora:</Form.Label>
                                        <Form.Control
                                            type="time"
                                            name="hora"
                                            title="Digite a hora da banca"
                                            {...register("hora")}
                                            isInvalid={!!errors.hora}
                                            className="bg-white text-black fw-normal fs-5 "
                                        />
                                        <Form.Control.Feedback type="invalid">{errors.hora?.message}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>

                                <Col md={5}>
                                    <Form.Group>
                                        <Form.Label className="m-0 fw-bold text-secondary fs-4">Local:</Form.Label>
                                        <Form.Control
                                            type="text"
                                            name="local"
                                            title="Digite a o local da banca"
                                            placeholder="Ex: Sala 111 ou Link Teams"
                                            {...register("local")}
                                            isInvalid={!!errors.local}
                                            className="bg-white text-black fw-normal fs-5 "
                                        />
                                        <Form.Control.Feedback type="invalid">{errors.local?.message}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                            </Row>

                            {/* Botão de Submissão */}
                            <div className="d-flex justify-content-center mt-5">
                                <Button
                                    type="submit"
                                    variant="primary"
                                    className="w-50 fw-bold fs-4 py-2 d-flex justify-content-center align-items-center gap-2"
                                    title="Clique aqui para marcar a banca"
                                    disabled={enviando}
                                >
                                    {enviando ? (
                                        <>
                                            <Spinner as="span" animation="border" size="sm" role="status" aria-hidden="true" />
                                            Marcando...
                                        </>
                                    ) : "Marcar banca"}
                                </Button>
                            </div>
                        </>
                    )}
                </Form >

                {/* Alerta global para retorno de sucesso ou erros */}
                {resultadoPedido.exibir && (
                    <Alert
                        variant={resultadoPedido.variante}
                        onClose={() => setResultadoPedido({ exibir: false, variante: "", mensagem: "" })}
                        dismissible
                        className="mt-3 fs-5 fw-medium text-center shadow-sm"
                    >
                        {resultadoPedido.mensagem}
                    </Alert>
                )}
            </Container >
        </>
    );
};

export default MarcarBanca;