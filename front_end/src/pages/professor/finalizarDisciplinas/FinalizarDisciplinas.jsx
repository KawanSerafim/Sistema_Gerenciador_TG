import { useState, useEffect } from "react";
import { Button, Col, Container, Form, FormGroup, FormLabel, Row, Toast, ToastContainer, Spinner } from "react-bootstrap";
import UserNavBar from "../../../components/usernavbar/UserNavBar";

import { finalizarDisciplinasZodSchema } from "../../../schemas/professor/finalizarDisciplinas/finalizarDisciplinasZodSchema";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm, useWatch } from "react-hook-form";

// Importe a service responsável pelas turmas (ajuste o caminho conforme seu projeto)
//import { turmaService } from "../../../services/turma/turmaService";
import { turmasService } from "../../../services/turmas/turmasService"

const FinalizarDisciplinas = () => {

    const {
        reset,
        handleSubmit,
        setValue,
        control,
        formState: { errors, isSubmitting }
    } = useForm({
        resolver: zodResolver(finalizarDisciplinasZodSchema),
        defaultValues: {
            disciplinasSelecionadas: [] // Agora guardará um array de IDs (UUIDs)
        }
    });

    // Estados da aplicação
    const [turmasAtivas, setTurmasAtivas] = useState([]);
    const [ano, setAno] = useState("");
    const [semestre, setSemestre] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [exibirResultado, setExibirResultado] = useState({ show: false, variant: "", message: "" });

    // Busca os dados reais do backend ao carregar a página
    useEffect(() => {
        const carregarTurmas = async () => {
            try {
                setCarregando(true);
                // Busca apenas as turmas ativas vinculadas ao professor logado
                const dados = await turmasService.buscarMinhasTurmas();
                setTurmasAtivas(dados);

                // Extrai o ano e semestre da primeira turma retornada para exibir no cabeçalho
                if (dados && dados.length > 0) {
                    setAno(dados[0].ano);
                    setSemestre(dados[0].semestre);
                }
            } catch (error) {
                console.error("Erro ao carregar turmas:", error);
                setExibirResultado({
                    show: true,
                    variant: "danger",
                    message: "Não foi possível carregar as disciplinas ativas."
                });
            } finally {
                setCarregando(false);
            }
        };

        carregarTurmas();
    }, []);

    const disciplinasSelecionadas = useWatch({
        control,
        name: "disciplinasSelecionadas",
    });

    // Pega apenas os IDs de todas as turmas para o botão "Selecionar todas"
    const todasDisciplinasIds = turmasAtivas.map(turma => turma.id);

    // Função para o checkbox "Selecionar Todas"
    const handleSelecionarTodas = (event) => {
        if (event.target.checked) {
            setValue("disciplinasSelecionadas", todasDisciplinasIds, { shouldValidate: true });
        } else {
            setValue("disciplinasSelecionadas", [], { shouldValidate: true });
        }
    };

    // Função para os checkbox individuais (agora recebe o ID da turma)
    const handleSelecionarIndividual = (idTurma) => {
        if (disciplinasSelecionadas.includes(idTurma)) {
            setValue("disciplinasSelecionadas", disciplinasSelecionadas.filter(id => id !== idTurma), { shouldValidate: true });
        } else {
            setValue("disciplinasSelecionadas", [...disciplinasSelecionadas, idTurma], { shouldValidate: true });
        }
    };

    // Função que envia os dados para a API
    const enviarParaBackend = async (dadosValidados) => {
        try {
            setExibirResultado({ show: false, variant: "", message: "" });

            // Monta o payload exatamente como o FinalizarTurmasCaso.Comando espera
            const payload = {
                turmasIds: dadosValidados.disciplinasSelecionadas
            };

            await turmasService.finalizarTurmas(payload);

            setExibirResultado({
                show: true,
                variant: "success",
                message: "Disciplinas finalizadas com sucesso!"
            });

            reset();

            // Remove as turmas finalizadas da tela dinamicamente sem precisar recarregar a página
            setTurmasAtivas(turmasAtivas.filter(t => !dadosValidados.disciplinasSelecionadas.includes(t.id)));

        } catch (error) {
            console.error("Erro ao finalizar:", error);
            setExibirResultado({
                show: true,
                variant: "danger",
                message: error.message || "Ocorreu um erro ao tentar finalizar as disciplinas."
            });
        }
    };

    return (
        <>
            <UserNavBar
                userName="Professor de TG"
                maxWidth="1200px"
            />
            <Container className="mt-5 align-items-center" style={{ maxWidth: '1200px' }}>
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Finalizar Disciplinas</h2>
                <Form
                    noValidate
                    className='form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm'
                    id="formTurma"
                    onSubmit={handleSubmit(enviarParaBackend)}
                >
                    {carregando ? (
                        <div className="text-center py-5">
                            <Spinner animation="border" variant="primary" />
                            <p className="mt-2 text-secondary fw-bold">Carregando disciplinas...</p>
                        </div>
                    ) : (
                        <>
                            {/* Linha 1: Exibição do Semestre e Ano dinâmico */}
                            <div className="d-flex flex-column align-items-center mb-4">
                                {ano && semestre && (
                                    <h3 className="mb-2 fw-medium text-secondary fs-3">{ano} - {semestre}° Semestre</h3>
                                )}
                            </div>

                            {/* Selecionar Todas */}
                            <Row>
                                <Col className="d-flex flex-column justify-content-center align-items-center gap-3">
                                    <h3 className="text-secondary text-center fs-3">Selecione as disciplinas que deseja finalizar</h3>

                                    {turmasAtivas.length > 0 ? (
                                        <Form.Check
                                            label="Selecionar todas as disciplinas"
                                            name="SelecionarTodas"
                                            title="Clique aqui para selecionar todas as disciplinas"
                                            type="checkbox"
                                            id="SelectAllDisciplinas"
                                            checked={disciplinasSelecionadas.length === todasDisciplinasIds.length && todasDisciplinasIds.length > 0}
                                            onChange={handleSelecionarTodas}
                                            isInvalid={!!errors.disciplinasSelecionadas}
                                            className="mb-2 fw-bold text-primary fs-5"
                                        />
                                    ) : (
                                        <div className="text-muted fw-bold mt-3">Não há disciplinas ativas para finalizar neste semestre.</div>
                                    )}
                                </Col>
                            </Row>

                            {/* Renderização Condicional dos Turnos baseada no enum MANHA, TARDE, NOITE */}
                            {turmasAtivas.length > 0 && (
                                <FormGroup className="mt-4 d-flex justify-content-around flex-wrap">

                                    {/* Turno da Manhã */}
                                    <div className="gap-2 mb-3">
                                        <FormLabel className='text-secondary fs-4 fw-bold border-bottom border-primary pb-1'>
                                            Manhã
                                        </FormLabel>
                                        {turmasAtivas.some(t => t.turno === "MANHA") ?
                                            turmasAtivas.filter(t => t.turno === "MANHA").map((t) => (
                                                <Form.Check
                                                    key={t.id}
                                                    label={t.disciplina} // Ex: "TG1"
                                                    name={`Select${t.id}`}
                                                    title={`${t.disciplina} manhã`}
                                                    type="checkbox"
                                                    id={`Select${t.id}`}
                                                    className="mb-3 fw-medium text-secondary fs-5"
                                                    checked={disciplinasSelecionadas.includes(t.id)}
                                                    onChange={() => handleSelecionarIndividual(t.id)}
                                                    isInvalid={!!errors.disciplinasSelecionadas}
                                                />
                                            )) : (<p className="text-muted fs-6 fw-medium mt-2">Sem disciplinas</p>)
                                        }
                                    </div>

                                    {/* Turno da Tarde */}
                                    <div className="gap-2 mb-3">
                                        <FormLabel className='text-secondary fs-4 fw-bold border-bottom border-primary pb-1'>
                                            Tarde
                                        </FormLabel>
                                        {turmasAtivas.some(t => t.turno === "TARDE") ?
                                            turmasAtivas.filter(t => t.turno === "TARDE").map((t) => (
                                                <Form.Check
                                                    key={t.id}
                                                    label={t.disciplina}
                                                    name={`Select${t.id}`}
                                                    title={`${t.disciplina} tarde`}
                                                    type="checkbox"
                                                    id={`Select${t.id}`}
                                                    className="mb-3 fw-medium text-secondary fs-5"
                                                    checked={disciplinasSelecionadas.includes(t.id)}
                                                    onChange={() => handleSelecionarIndividual(t.id)}
                                                    isInvalid={!!errors.disciplinasSelecionadas}
                                                />
                                            )) : (<p className="text-muted fs-6 fw-medium mt-2">Sem disciplinas</p>)
                                        }
                                    </div>

                                    {/* Turno da Noite */}
                                    <div className="gap-2 mb-3">
                                        <FormLabel className='text-secondary fs-4 fw-bold border-bottom border-primary pb-1'>
                                            Noite
                                        </FormLabel>
                                        {turmasAtivas.some(t => t.turno === "NOITE") ?
                                            turmasAtivas.filter(t => t.turno === "NOITE").map((t) => (
                                                <Form.Check
                                                    key={t.id}
                                                    label={t.disciplina}
                                                    name={`Select${t.id}`}
                                                    title={`${t.disciplina} noite`}
                                                    type="checkbox"
                                                    id={`Select${t.id}`}
                                                    className="mb-3 fw-medium text-secondary fs-5"
                                                    checked={disciplinasSelecionadas.includes(t.id)}
                                                    onChange={() => handleSelecionarIndividual(t.id)}
                                                    isInvalid={!!errors.disciplinasSelecionadas}
                                                />
                                            )) : (<p className="text-muted fs-6 fw-medium mt-2">Sem disciplinas</p>)
                                        }
                                    </div>
                                </FormGroup>
                            )}

                            {/* Exibe erro de validação se a lista estiver vazia */}
                            {errors.disciplinasSelecionadas && (
                                <Row className="justify-content-center mb-4">
                                    <Col xs={12} md={8} lg={6}>
                                        <div className="text-danger fw-bold text-center bg-danger-subtle p-2 rounded">
                                            {errors.disciplinasSelecionadas?.message}
                                        </div>
                                    </Col>
                                </Row>
                            )}

                            {/* Botão de Finalizar */}
                            <Row className="mt-5">
                                <Col>
                                    <Button
                                        variant="primary"
                                        type="submit"
                                        title="Clique aqui para finalizar as disciplinas selecionadas"
                                        id='btn-cadastro'
                                        className='fs-4 fw-medium w-100 py-2'
                                        disabled={isSubmitting || turmasAtivas.length === 0}
                                    >
                                        {isSubmitting ? 'Finalizando...' : 'Finalizar Disciplinas'}
                                    </Button>
                                </Col>
                            </Row>
                        </>
                    )}
                </Form>

                {/* Toast Container Flutuante */}
                <ToastContainer
                    position="top-end"
                    className="p-3 mt-5"
                    style={{ position: "fixed", zIndex: 9999 }}
                >
                    <Toast
                        show={exibirResultado.show}
                        onClose={() => setExibirResultado({ show: false, variant: "", message: "" })}
                        bg={exibirResultado.variant}
                        delay={5000}
                        autohide
                    >
                        <Toast.Header>
                            <strong className="me-auto text-dark">
                                {exibirResultado.variant === "danger" ? "Atenção" : "Sucesso"}
                            </strong>
                        </Toast.Header>
                        <Toast.Body className="text-white fw-bold fs-6">
                            {exibirResultado.message}
                        </Toast.Body>
                    </Toast>
                </ToastContainer>

            </Container>
        </>
    );
};

export default FinalizarDisciplinas;