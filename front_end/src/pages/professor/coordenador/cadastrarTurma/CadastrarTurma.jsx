import { Container, Form, FormGroup, FormSelect, FormLabel, Button, FormControl, Row, Col, Alert } from "react-bootstrap";
import UserNavBar from "../../../../components/usernavbar/UserNavBar";
import { useForm } from "../../../../hooks/useForm";
import { useState } from "react";
import { bloquearCaracteresInputNumber } from "../../../../utils/utils";

// Controlador do ano minimo 
const anoAtual = new Date().getFullYear();
//Função pura para validação dos campos
const validarCampos = (valores) => {
    let erros = {};
    const anoDigitado = parseInt(valores.ano);
    // Ano e Semestre
    if (!valores.ano) {
        erros.ano = "O ano é obrigatório.";
    }
    else if (valores.ano.length !== 4) {
        erros.ano = "O ano deve ter 4 dígitos.";
    }
    else if (anoDigitado < anoAtual || anoDigitado > (anoAtual + 2)) {
        erros.ano = `O ano deve estar entre ${anoAtual} e ${anoAtual + 2}`
    }
    if (!valores.semestre) {
        erros.semestre = "Selecione o semestre.";
    }


    // Validação das turmas: 
    // Verifica se todas as 6 combinações (3 turnos x 2 disciplinas) foram preenchidas
    const disciplinas = ["TG1", "TG2"];
    const turnos = ["Noite", "Tarde", "Manhã"];
    let faltamProfessores = false;

    turnos.forEach(t => {
        disciplinas.forEach(d => {
            const chave = `${d}-${t}`;
            if (!valores.turmas[chave]) {
                faltamProfessores = true;
            }
        });
    });

    if (faltamProfessores) {
        erros.turmas = "Selecione professores para todas as turmas listadas.";
    }

    return erros;
}


const CadastrarTurma = () => {
    //TODO: Trocar mocks pelos dados do backend

    //Estado para o sucesso
    const [exibirSucesso, setExibirSucesso] = useState(false)


    //Usar hook de validação

    const campos = {
        ano: anoAtual.toString(),
        semestre: "",
        turmas: {}
    }

    const { values, errors, handleChange, handleSubmit } = useForm(campos, validarCampos)

    //Mock de curso
    const curso = {
        id: 1,
        nome: "Analise e desenvolvimento de sistemas",
        coordenador: "Luciano",
        turnos: ["Noite", "Tarde", "Manhã"],
        disciplinas: ["TG1", "TG2"]
    }

    const optionsTurnos = curso.turnos
    const optionsDisciplinas = curso.disciplinas

    // Professores e turmas
    const [profUnico, setProfUnico] = useState("")
    //Controla o estado do btn radio de 1 prof apenas
    const [apenasUmProf, setApenasUmProf] = useState(false)
    //Mock de professores, dados reais viram do backend
    const todosProfessores = ["Cristina", "Luciano", "Antonio", "Rogerio", "Colevati"]

    // --- FUNÇÕES DE ATUALIZAÇÃO ---
    const atualizarTurmasNoHook = (novoObjetoTurmas) => {
        // Simula o evento que o hook de validação do form espera
        handleChange({
            target: {
                name: "turmas",
                value: novoObjetoTurmas
            }
        });
    };
    //Quando um usuário muda o prof no select do topo
    const handleProfessorUnico = (prof) => {
        setProfUnico(prof);
        //Se esta ativo preenche todos os campos com o prof selecionado
        if (apenasUmProf) {
            const novaTurma = {}
            optionsTurnos.forEach(turno => {
                optionsDisciplinas.forEach(disciplina => {
                    novaTurma[`${disciplina}-${turno}`] = prof;
                })
            });
            atualizarTurmasNoHook(novaTurma);
        }
    }

    //Função para o checkbox "Selecionar Um professor para todas as disciplinas"
    const handleUmProfessorParaTodas = (e) => {
        const checado = e.target.checked;
        setApenasUmProf(checado);
        // Se marcou e ja tem profGlobal aplica todos na hora
        if (checado && profUnico) {
            handleProfessorUnico(profUnico);
        }
    }

    //Função para os selects individuais
    const handleSelecionarProfIndividual = (chaveTurma, prof) => {
        const novaTurma = {
            ...values.turmas,
            [chaveTurma]: prof
        }
        atualizarTurmasNoHook(novaTurma)
    }

    // A função mock que realmente envia os dados caso passe na validação do frontend
    const enviarParaBackend = (dadosValidados) => {
        // Aqui vai o seu fetch/axios enviando o JSON para a API em Java
        console.log("Enviando payload para a API:", dadosValidados);
        //Ativa alerta de sucesso
        setExibirSucesso(true);
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
                                    value={values.ano}
                                    onChange={handleChange}
                                    //Usa função do utils para bloquear caracteres
                                    onKeyDown={bloquearCaracteresInputNumber}
                                    isInvalid={!!errors.ano}
                                    //Impede que o usuario seleione anos anteriores ao atual
                                    min={anoAtual}
                                    max={anoAtual + 3}
                                    className="fw-medium bg-white border-secondary fs-5"
                                    style={{ maxWidth: '6.6rem' }}
                                />
                                {/* Feedback de erro */}
                                <Form.Control.Feedback type="invalid">
                                    {errors.ano}
                                </Form.Control.Feedback>


                            </FormGroup>
                        </Col>
                        {/* Coluna do semestre */}
                        {/* Mobile=1, tablet=2, desktop=4 */}
                        <Col xs={12} md={6} lg={3}>
                            <FormGroup controlId="formSemestre"
                                value={values.semestre}
                            >
                                <FormLabel className='text-secondary fs-4 fw-bold d-block' title="Semestre da turma">Semestre</FormLabel>
                                <div className="d-flex gap-3 pt-2">
                                    <Form.Check
                                        inline
                                        label="1"
                                        title="Opção: Primeiro semestre"
                                        name="semestre"
                                        type="radio"
                                        value="1"
                                        onChange={handleChange}
                                        isInvalid={!!errors.semestre}
                                        checked={values.semestre === "1"}
                                        className="fw-bold fs-5"
                                    />
                                    <Form.Check
                                        inline
                                        title="Opção: Segundo semestre"
                                        label="2"
                                        name="semestre"
                                        type="radio"
                                        value="2"
                                        onChange={handleChange}
                                        isInvalid={!!errors.semestre}
                                        checked={values.semestre === "2"}
                                        className="fw-bold fs-5"
                                    />
                                </div>
                                {errors.semestre && <small className="text-danger small mt-1">{errors.semestre}</small>}

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
                            onChange={(e) => handleUmProfessorParaTodas(e)}
                            className="mb-2 fw-medium text-secondary text-nowrap fs-5"
                        />

                        {/* Selecionar um Professor para todas as disciplina */}
                        <FormSelect className={apenasUmProf ? 'bg-white text-black fw-medium fs-5 w-100' : 'bg-dark-subtle text-muted fw-medium fs-5 w-100'}
                            value={profUnico}
                            title={apenasUmProf ? "Selecionar professor unico para todas as turmas" : "Clique na opção ao lado para liberar a seleção"}
                            onChange={(e) => handleProfessorUnico(e.target.value)}
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
                    {optionsTurnos.map((turno) => (
                        <Row key={turno} className="gy-3 mb-3">
                            {/* Dentro da linha itera as disciplinas (colunas) */}
                            {optionsDisciplinas.map((disciplina) => {
                                //id da turma para o estado
                                const chaveTurma = `${disciplina}-${turno}`;
                                //Verifica se existe erro em turma e se falta esse professor em especifico
                                const campoEstaInvalido = !!errors.turmas && !values.turmas[chaveTurma];

                                return (
                                    <Col md={6} key={`${chaveTurma}`}>
                                        <FormGroup>
                                            {/* Selecionar Professor para disciplina */}
                                            <FormLabel className='text-secondary fs-4 fw-medium'>
                                                {disciplina} {turno}
                                            </FormLabel>
                                            <FormSelect
                                                isInvalid={campoEstaInvalido}
                                                title={"Selecione o professor da turma " + disciplina + ' ' + turno}
                                                className='bg-white text-black border-secondary-subtle fw-medium fs-5'
                                                //Garante que o valor venha do estado
                                                value={values.turmas[chaveTurma] || ""}
                                                onChange={(e) => handleSelecionarProfIndividual(chaveTurma, e.target.value)}
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
                        <div className="text-danger text-center mt-2 small fw-bold">
                            {errors.turmas}
                        </div>
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