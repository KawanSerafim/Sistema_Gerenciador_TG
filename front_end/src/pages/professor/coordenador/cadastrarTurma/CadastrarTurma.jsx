import { Container, Form, FormGroup, FormSelect, FormLabel, Button, FormControl, Row, Col, Toast, ToastContainer, Spinner, Alert } from "react-bootstrap";
import UserNavBar from "../../../../components/usernavbar/UserNavBar";
import { useEffect, useState } from "react";
import { bloquearCaracteresInputNumber } from "../../../../utils/utils";


// RHF e Zod
import { useForm, useWatch } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { camposSchema } from "../../../../schemas/professor/coordenador/cadastrarTurma/cadastrarTurmaZodSchema";

// Services (Importe corretamente conforme sua estrutura)
import { turmasService } from "../../../../services/turmas/turmasService";
import { professorService } from "../../../../services/professor/professorService";


const CadastrarTurma = () => {
    // ======== Estados ========

    const [resultado, setResultado] = useState({ exibir: false, variante: "", mensagem: "" })
    const [carregandoDados, setCarregandoDados] = useState(true);

    //Estados vindos do backend
    const [cursoConfig, setCursoConfig] = useState({ turnos: [], disciplinas: [] })
    const [todosProfessores, setTodosProfessores] = useState([]);

    //========= Conf do RHF =========
    const {
        register,
        handleSubmit,
        setValue,
        control,
        formState: { errors },
        reset,
    } = useForm({
        resolver: zodResolver(camposSchema),
        defaultValues: {
            ano: new Date().getFullYear(),
            semestre: "",
            turmas: {},
            //Usado para evitar warnings do React
            checkUnico: false,
            profUnico: ""
        }
    });

    // ========== Observadores ==========
    // Checkbox
    const apenasUmProf = useWatch({
        control,
        name: "checkUnico"
    });
    //Select do topo
    const profUnico = useWatch({
        control,
        name: "profUnico"
    });


    //========= Effects para chamadas da service =====

    //Efeito para aplicar professor unico em tempo real, semnpre que os observadores forem acionados
    useEffect(() => {
        const inicializarDados = async () => {
            try {
                setCarregandoDados(true);
                // Busca as configurações (turnos, disciplinas) e os professores ao mesmo tempo
                const [turnos, disciplinas, professores] = await Promise.all([
                    turmasService.buscaTurnos(),
                    turmasService.buscaDisciplianas(),
                    professorService.buscaProfessoresPorCargo("PROFESSOR_TG")
                ]);

                setCursoConfig({ turnos: turnos, disciplinas: disciplinas });
                setTodosProfessores(professores);
            } catch (erro) {
                console.error("Erro ao carregar dados:", erro);
                setResultado({
                    exibir: true,
                    variante: "danger",
                    mensagem: "Erro ao carregar dados do servidor. Tente atualizar a página."
                });
            } finally {
                setCarregandoDados(false);
            }
        };

        inicializarDados();
    }, []);

    //Aplica prof unico
    useEffect(() => {
        if (apenasUmProf && profUnico && cursoConfig.turnos.length > 0) {
            cursoConfig.turnos.forEach(t => {
                cursoConfig.disciplinas.forEach(d => {
                    const chaveTurma = `${d}-${t}`;

                    // Seta o valor diretamente no caminho exato (ex: turmas.TG1-Noite)
                    setValue(`turmas.${chaveTurma}`, profUnico, {
                        shouldValidate: true, // Força o Zod a validar que agora tem valor
                        shouldDirty: true     // Avisa o RHF que o campo foi alterado
                    });
                });
            });
        }
    }, [apenasUmProf, profUnico, setValue, cursoConfig]);

    // ======= Funções =========

    // A função mock que realmente envia os dados caso passe na validação do frontend
    // A função que envia os dados caso passe na validação do frontend
    const enviarParaBackend = async (dadosValidados) => {
        try {
            const anoFormatado = parseInt(dadosValidados.ano, 10);
            const semestreFormatado = parseInt(dadosValidados.semestre, 10);

            // Mapeamos o formulário para um Array de PROMESSAS (requisições)
            const promessasDeCadastro = Object.entries(dadosValidados.turmas).map(([chave, matriculaProf]) => {
                const [disciplina, turnoFront] = chave.split('-');

                // Formata o turno para bater com o Enum do Java (ex: "MANhã" vira "MANHA")
                const turnoFormatado = turnoFront.toUpperCase().replace("Ã", "A");

                // Monta o payload exato que o backend espera
                const payloadUnico = {
                    matriculaProfessorTg: matriculaProf,
                    disciplina: disciplina,
                    turno: turnoFormatado,
                    ano: anoFormatado,
                    semestre: semestreFormatado
                };

                // Retorna a requisição para a lista (NÃO colocamos await aqui dentro do map)
                return turmasService.cadastrarTurmas(payloadUnico);
            });

            console.log(`Disparando ${promessasDeCadastro.length} requisições de cadastro...`);

            // 2. Dispara todas as requisições simultaneamente e aguarda todas terminarem
            await Promise.all(promessasDeCadastro);

            // Se chegou aqui, TODAS as turmas foram cadastradas com sucesso (Status 201 CREATED)
            setResultado({
                exibir: true,
                variante: "success",
                mensagem: "Turmas cadastradas com sucesso!"
            });

            // Limpa o form, mas mantém o ano atual
            reset({
                ano: new Date().getFullYear(),
                semestre: "",
                turmas: {},
                checkUnico: false,
                profUnico: ""
            });

        } catch (erro) {
            // Se QUALQUER uma das requisições der erro, ele cai aqui
            setResultado({
                exibir: true,
                variante: "danger",
                mensagem: erro.message || "Erro ao cadastrar algumas turmas. Verifique os dados."
            });
        }
    };
    // ======== Renderização ========

    // Se estiver carregando, mostra um spinner
    if (carregandoDados) {
        return (
            <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: "50vh" }}>
                <Spinner animation="border" variant="primary" />
            </Container>
        );
    }

    return (
        <>
            <UserNavBar
                userName="Coordenador"
                maxWidth="900px"
            />
            <Container className="mt-5 px-3 px-md-0" style={{ maxWidth: '900px' }}>

                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Cadastro de Turmas</h2>
                <Form
                    noValidate
                    className='form-bg border border-dark border-top-0 p-2 p-md-4 rounded-bottom-4 shadow-sm'
                    id="formTurma"
                    onSubmit={handleSubmit(enviarParaBackend)}

                >
                    {/* Primeira linha: Ano e semestre */}
                    <Row className="mt-2 d-flex justify-content-center align-items-center">
                        {/* Coluna ano*/}
                        {/* Mobile=1, tablet=2, desktop=4 */}
                        <Col xs={12} md={6} lg={3}>
                            <FormGroup controlId="formAno">
                                {/* Ano */}
                                <FormLabel className='text-secondary fs-4 fw-bold' title="Ano da turma">Ano</FormLabel>
                                <FormControl
                                    type="number"
                                    name="ano"
                                    title="Ano da turma"
                                    {...register("ano")}
                                    //Usa função do utils para bloquear caracteres
                                    onKeyDown={bloquearCaracteresInputNumber}
                                    isInvalid={!!errors.ano}
                                    className="fw-medium bg-white border-secondary fs-5"
                                    style={{ maxWidth: '6.6rem' }}
                                />
                                {/* Feedback de erro */}
                                <Form.Control.Feedback type="invalid">
                                    {errors.ano?.message}
                                </Form.Control.Feedback>


                            </FormGroup>
                        </Col>
                        {/* Coluna do semestre */}
                        {/* Mobile=1, tablet=2, desktop=4 */}
                        <Col xs={12} md={6} lg={3}>
                            <FormGroup >
                                <FormLabel className='text-secondary fs-4 fw-bold d-block' title="Semestre da turma">Semestre</FormLabel>
                                <div className="d-flex gap-3 pt-2">
                                    <Form.Check
                                        inline
                                        label="1"
                                        title="Opção: Primeiro semestre"
                                        name="semestre"
                                        type="radio"
                                        value="1"
                                        {...register("semestre")}
                                        isInvalid={!!errors.semestre}
                                        className="fw-bold fs-5"
                                    />
                                    <Form.Check
                                        inline
                                        title="Opção: Segundo semestre"
                                        label="2"
                                        name="semestre"
                                        type="radio"
                                        value="2"
                                        {...register("semestre")}
                                        isInvalid={!!errors.semestre}
                                        className="fw-bold fs-5"
                                    />
                                </div>
                                {errors.semestre && <small className="text-danger small mt-1">{errors.semestre?.message}</small>}

                            </FormGroup>
                        </Col>
                    </Row>
                    {/* Linha 2: Seleção de um unico professor para a turma */}
                    <div className="d-flex flex-column flex-md-row align-items-center my-4 gap-2 gap-md-3">
                        <Form.Check
                            label="Unico professor para todas as turmas"
                            title="Opção: Unico professor para todas as turmas"
                            type="checkbox"
                            id="checkUnicoProfessor"
                            {...register("checkUnico")}
                            className="mb-2 fw-medium text-secondary text-nowrap fs-5"
                        />

                        {/* Selecionar um Professor para todas as disciplina */}
                        <FormSelect className={apenasUmProf ? 'bg-white text-black fw-medium fs-5 w-100' : 'bg-dark-subtle text-muted fw-medium fs-5 w-100'}
                            {...register("profUnico")}
                            title={apenasUmProf ? "Selecionar professor unico para todas as turmas" : "Clique na opção ao lado para liberar a seleção"}
                            //Enquanto a opção de apenas 1 prof não for selecionada o select esta desativado
                            disabled={!apenasUmProf}
                        >
                            <option value="" disabled selected> Selecione o professor de TG</option>
                            {todosProfessores.map((professor) => (
                                <option key={professor.id} value={professor.matricula}>
                                    {professor.nome}
                                </option>
                            ))}
                        </FormSelect>

                    </div>
                    <hr className="my-4" />
                    {/* Linha 3: Opções de disciplinas e turnos */}
                    {cursoConfig.turnos.map((turno) => (
                        <Row key={turno} className="gy-3 mb-3">
                            {/* Dentro da linha itera as disciplinas (colunas) */}
                            {cursoConfig.disciplinas.map((disciplina) => {
                                //id da turma para o estado
                                const chaveTurma = `${disciplina}-${turno}`;

                                return (
                                    <Col md={6} key={`${chaveTurma}`}>
                                        <FormGroup>
                                            {/* Selecionar Professor para disciplina */}
                                            <FormLabel className='text-secondary fs-4 fw-medium'>
                                                {disciplina} {turno}
                                            </FormLabel>
                                            <FormSelect
                                                title={"Selecione o professor da turma " + disciplina + ' ' + turno}
                                                className='bg-white text-black border-secondary-subtle fw-medium fs-5'
                                                {...register(`turmas.${chaveTurma}`)}
                                                isInvalid={!!errors?.turmas?.[chaveTurma]}
                                            >
                                                <option value="" disabled selected>Selecione o professor de TG</option>
                                                {todosProfessores.map((professor) => (
                                                    <option key={professor.id} value={professor.matricula.trim()}>
                                                        {professor.nome}
                                                    </option>
                                                ))}
                                            </FormSelect>
                                        </FormGroup>
                                    </Col>
                                );
                            })}
                        </Row>
                    ))}
                    {/* Mensagem de Erro Geral para as Turmas */}
                    {errors.turmas && (
                        <Alert variant="danger" className="text-center py-2">
                            {errors.turmas.message}
                        </Alert>
                    )}

                    {/* Botão de Cadastrar */}
                    <Row className="mt-5">
                        <Col>
                            <Button
                                variant="primary"
                                type="submit"
                                id='btn-cadastro'
                                title="Cadastrar Turmas"
                                className='fs-4 fw-bold w-100 py-2'
                            >
                                Cadastrar
                            </Button>
                        </Col>
                    </Row>
                </Form>
                {/* Renderiza o alerta de sucesso após passar nas validações */}
                {resultado.exibir && (
                    <ToastContainer
                        position="top-end"
                        className="p-3"
                        style={{ position: "fixed", zIndex: 9999 }}
                    >
                        <Toast
                            show={resultado.exibir}
                            onClose={() => setResultado({ exibir: false, variante: "", mensagem: "" })}
                            bg={resultado.variante}
                        >
                            <Toast.Header>
                                <strong className="me-auto text-dark">
                                    {resultado.variante === "danger" ? "Atenção" : "Sucesso"}
                                </strong>
                            </Toast.Header>
                            <Toast.Body className="text-white fw-bold fs-6">
                                {resultado.mensagem}
                            </Toast.Body>
                        </Toast>
                    </ToastContainer>
                )}
            </Container >
        </>
    )
}
export default CadastrarTurma