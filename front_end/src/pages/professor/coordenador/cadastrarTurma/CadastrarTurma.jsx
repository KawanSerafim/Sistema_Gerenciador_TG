import { Container, Form, FormGroup, FormSelect, FormLabel, Button, FormControl, Row, Col } from "react-bootstrap";
import UserNavBar from "../../../../components/usernavbar/UserNavBar";
import { useForm } from "../../../../hooks/useForm";
import { useState } from "react";

//Função pura para validação dos campos
const validarCampos = (valores) => {
    let erros = {};
    // Ano e Semestre
    if (!valores.ano) erros.ano = "O ano é obrigatório.";
    if (!valores.semestre) erros.semestre = "Selecione o semestre.";

    // Validação das turmas: 
    // Verificamos se todas as 6 combinações (3 turnos x 2 disciplinas) foram preenchidas
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

    //Usar hook de validação

    const campos = {
        ano: new Date().getFullYear(),
        semestre: "",
        turmas: {}
    }

    const { values, errors, handleChange, handleSubmit } = useForm(campos, validarCampos)

    // Controlador do ano minimo 
    const anoAtual = new Date().getFullYear();
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
    const [profGlobal, setProfGlobal] = useState("")
    const [turmas, setTurmas] = useState({})
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
    const handleProfessorGlobal = (prof) => {
        setProfGlobal(prof);
        //Se esta ativo preenche todos os campos com o prof selecionado
        if (apenasUmProf) {
            const novaTurma = {}
            optionsTurnos.forEach(turno => {
                optionsDisciplinas.forEach(disciplina => {
                    novaTurma[`${disciplina}-${turno}`] = prof;
                })
            });
            setTurmas(novaTurma)
        }
    }

    //Função para o checkbox "Selecionar Um professor para todas as disciplinas"
    const handleUmProfessorParaTodas = (e) => {
        const checado = e.target.checked;
        setApenasUmProf(checado);
        // Se marcou e ja tem profGlobal aplica todos na hora
        if (checado && profGlobal) {
            handleProfessorGlobal(profGlobal);
        }
    }

    //Função para os selects individuais
    const handleSelecionarProfIndividual = (chaveTurma, prof) => {
        setTurmas(antiga => ({
            ...antiga,
            [chaveTurma]: prof
        }))
    }

    // A função mock que realmente envia os dados caso passe na validação do frontend
    const enviarParaBackend = (dadosValidados) => {
        // Aqui vai o seu fetch/axios enviando o JSON para a API em Java
        console.log("Enviando payload para a API:", dadosValidados);
    };



    return (
        <>
            <UserNavBar
                userName="Max"
            />
            <Container className="mt-5" style={{ minWidth: '800px' }}>
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Cadastro de Turmas</h2>
                <Form
                    noValidate
                    className='form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm'
                    id="formTurma"
                    onSubmit={handleSubmit(enviarParaBackend)}

                >
                    {/* Primeira linha: Ano e semestre */}
                    <Row className="mt-2 d-flex justify-content-center align-items-center">
                        {/* Coluna ano*/}
                        <Col md={3}>
                            <FormGroup controlId="formAno">
                                {/* Ano */}
                                <FormLabel className='text-secondary fs-6 fw-bold '>Ano </FormLabel>
                                <FormControl
                                    type="number"
                                    placeholder={anoAtual}
                                    id="ano"
                                    //Impede que o usuario seleione anos anteriores ao atual
                                    min={anoAtual}
                                    className="fw-medium bg-white border-secondary"
                                    style={{ maxWidth: '100px' }}
                                    required={true} />

                            </FormGroup>
                        </Col>
                        {/* Coluna do semestre */}
                        <Col md={3}>
                            <FormGroup controlId="formSemestre">
                                <FormLabel className='text-secondary fs-6 fw-bold d-block'>Semestre</FormLabel>
                                <div className="d-flex gap-3 pt-2">
                                    <Form.Check
                                        inline
                                        label="1"
                                        name="semestre"
                                        type="radio"
                                        id="semestre1"
                                        className="fw-bold"
                                    />
                                    <Form.Check
                                        inline
                                        label="2"
                                        name="semestre"
                                        type="radio"
                                        id="semestre2"
                                        className="fw-bold"
                                    />
                                </div>
                            </FormGroup>
                        </Col>
                    </Row>
                    {/* Linha 2: Seleção de um unico professor para a turma */}
                    <div className="d-flex flex-column align-items-center my-4">
                        <Form.Check
                            label="Unico professor para todas as turmas"
                            type="checkbox"
                            onChange={(e) => handleUmProfessorParaTodas(e)}
                            className="mb-2 fw-medium text-secondary"
                        />

                        {/* Selecionar um Professor para todas as disciplina */}
                        <FormSelect className={apenasUmProf ? 'bg-dark-subtle text-black fw-medium fs-5 w-75' : 'bg-dark-subtle text-muted fw-medium fs-5 w-75'}
                            value={profGlobal}
                            onChange={(e) => handleProfessorGlobal(e.target.value)}
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
                        <Row key={turno} className="mb-3">
                            {/* Dentro da linha itera as disciplinas (colunas) */}
                            {optionsDisciplinas.map((disciplina) => {
                                //id da turma para o estado
                                const chaveTurma = `${disciplina}-${turno}`;
                                return (
                                    <Col md={6} key={`${chaveTurma}`}>
                                        <FormGroup>
                                            {/* Selecionar Professor para disciplina */}
                                            <FormLabel className='text-secondary fs-5 fw-medium'>
                                                {disciplina} {turno}
                                            </FormLabel>
                                            <FormSelect
                                                required={true}
                                                className='bg-dark-subtle border-secondary-subtle text-black fw-medium fs-5'
                                                //Garante que o valor venha do estado
                                                value={turmas[chaveTurma] || ""}
                                                onChange={(e) => handleSelecionarProfIndividual(chaveTurma, e.target.value)}
                                                //Desativa os individuais se o "global" mandar
                                                disabled={apenasUmProf}
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


                    {/* Botão de Cadastrar */}
                    <Row className="mt-5">
                        <Col>
                            <Button
                                variant="primary"
                                type="submit"
                                id='btn-cadastro'
                                className='fs-5 fw-bold w-100 py-2'
                            >
                                Cadastrar
                            </Button>
                        </Col>
                    </Row>
                </Form>
            </Container >
        </>
    )
}
export default CadastrarTurma