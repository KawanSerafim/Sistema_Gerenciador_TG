import { Container, Form, FormGroup, FormSelect, FormLabel, Button, FormControl, Row, Col, Alert } from "react-bootstrap";
import UserNavBar from "../../../../components/usernavbar/UserNavBar";
import { useEffect, useState } from "react";
import { bloquearCaracteresInputNumber } from "../../../../utils/utils";


// RHF e Zod
import { useForm, useWatch } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { camposSchema } from "../../../../schemas/professor/coordenador/cadastrarTurma/cadastrarTurmaZodSchema";



//Mock de curso, fica de fora pois é dado constante
const curso = {
    turnos: ["Noite", "Tarde", "Manhã"],
    disciplinas: ["TG1", "TG2"]
};

const CadastrarTurma = () => {
    //TODO: Trocar mocks pelos dados do backend

    //Estado para o sucesso
    const [exibirSucesso, setExibirSucesso] = useState(false)

    const todosProfessores = ["Cristina", "Luciano", "Antonio", "Rogerio", "Colevati"];

    //Conf do RHF
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
            turmas: {}
        }
    })

    //Observadores
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
    //Valores dos selects individuais
    const turmasWatched = useWatch({
        control,
        name: "turmas"
    });

    //Efeito para aplicar professor unico em tempo real, semnpre que os observadores forem acionados
    useEffect(() => {
        if (apenasUmProf && profUnico) {
            const novaConfiguracao = {};
            curso.turnos.forEach(t => {
                curso.disciplinas.forEach(d => {
                    novaConfiguracao[`${d}-${t}`] = profUnico
                })
            })
            //Atualiza o RHF
            setValue("turmas", novaConfiguracao, { shouldValidate: true });
        }
    }, [apenasUmProf, profUnico, setValue])

    // A função mock que realmente envia os dados caso passe na validação do frontend
    const enviarParaBackend = (dadosValidados) => {
        // Aqui vai o seu fetch/axios enviando o JSON para a API em Java
        console.log("Enviando payload para a API:", dadosValidados);
        //Ativa alerta de sucesso
        setExibirSucesso(true);
        //Limpa o form após sucesso
        reset()
        //Esconde depois de alguns segundos
        setTimeout(() => setExibirSucesso(false), 5000);
    };


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
                                <option id={professor} key={professor} value={professor}>
                                    {professor}
                                </option>
                            ))}
                        </FormSelect>

                    </div>
                    <hr className="my-4" />
                    {/* Linha 3: Opções de disciplinas e turnos */}
                    {curso.turnos.map((turno) => (
                        <Row key={turno} className="gy-3 mb-3">
                            {/* Dentro da linha itera as disciplinas (colunas) */}
                            {curso.disciplinas.map((disciplina) => {
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
                                                //Garante que o valor venha do observador
                                                value={turmasWatched?.[chaveTurma] || ""}
                                                isInvalid={!!errors.turmas}
                                            >
                                                <option value="" disabled>Selecione o professor de TG</option>
                                                {todosProfessores.map((professor) => (
                                                    <option key={professor} value={professor}>{professor}</option>
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
                {exibirSucesso && (
                    <Alert variant="success" onClose={() => setExibirSucesso(false)} dismissible className="mt-3" >
                        Turma cadastrada com sucesso!
                    </Alert>
                )}
            </Container >
        </>
    )
}
export default CadastrarTurma