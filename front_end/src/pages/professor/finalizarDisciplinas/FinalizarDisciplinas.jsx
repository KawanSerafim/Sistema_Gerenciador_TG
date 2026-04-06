import { useState } from "react";
import { Alert, Button, Col, Container, Form, FormControl, FormGroup, FormLabel, Row } from "react-bootstrap";
import UserNavBar from "../../../components/usernavbar/UserNavBar";


import { finalizarDisciplinasZodSchema } from "../../../schemas/professor/finalizarDisciplinas/finalizarDisciplinasZodSchema";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm, useWatch } from "react-hook-form";

//Mocks, TODO: trocar pelos dados do backend
const ano = new Date().getFullYear();
const semestre = 1;
const mockDiscplinas = [{
    id: 1,
    nome: "TG1",
    turnos: ["Manhã", "Tarde", "Noite"]
},
{
    id: 2,
    nome: "TG2",
    turnos: ["Manhã", "Tarde", "Noite"]
},]

const FinalizarDisciplinas = () => {
    //TODO: Trocar mocks pelos dados do backend

    const {
        reset,
        handleSubmit,
        setValue,
        control,
        formState: { errors }
    } = useForm({
        resolver: zodResolver(finalizarDisciplinasZodSchema),
        defaultValues: {
            disciplinasSelecionadas: []
        }
    })


    const [exibirResultado, setExibirResultado] = useState({ show: false, variant: "", message: "" })


    // Disciplinas
    //TODO: buscar disciplinas do professor logado e colocar no estado
    //Devido ao uso do mock não usa setDisciplinas, por enquanto
    const [disciplinas] = useState(mockDiscplinas)

    // Pega todas as disciplinas dinamicamente com base no backend (TODO: atualizar de acordo com backend)
    const todasDisciplinas = disciplinas.reduce((acc, disciplina) => {
        disciplina.turnos.forEach(turno => {
            //remove acentos do turno para padronizar o nome do checkbox
            const turnoFormatado = turno.replace('ã', 'a');
            acc.push(`Select${disciplina.nome}${turnoFormatado}`)
        })
        //Se tudo certo retorna a lista completa, se não, retorna lista vazia
        return acc;
    }, [])

    const disciplinasSelecionadas = useWatch({
        control,
        name: "disciplinasSelecionadas",
    })

    //Função para o checkbox "Selecionar Todas"
    const handleSelecionarTodas = (event) => {
        if (event.target.checked) {
            setValue("disciplinasSelecionadas", todasDisciplinas, { shouldValidate: true })
        } else {
            setValue("disciplinasSelecionadas", [], { shouldValidate: true })
        }
    }

    //Função para os checkbox individuais
    const handleSelecionarIndividual = (disciplina) => {
        if (disciplinasSelecionadas.includes(disciplina)) {
            //Se ja esta na lista é por que o usuario quer tirar
            setValue("disciplinasSelecionadas", disciplinasSelecionadas.filter(item => item !== disciplina), { shouldValidate: true })
        } else {
            //Se não, adiciona na lista
            setValue("disciplinasSelecionadas", [...disciplinasSelecionadas, disciplina], { shouldValidate: true });
        }
    }


    // A função mock que realmente envia os dados caso passe na validação do frontend
    const enviarParaBackend = (dadosValidados) => {
        // Aqui vai o seu fetch/axios enviando o JSON para a API em Java
        console.log("Enviando payload para a API:", dadosValidados);
        //Ativa alerta de sucesso
        setExibirResultado({ show: true, variant: "success", message: "Disciplinas finalizadas com sucesso!" });
        //Limpa RHF
        reset();
        //Esconde depois de alguns segundos
        setTimeout(() => setExibirResultado({ show: false }), 5000);
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
                    {/* Linha 1: Seleção de um unico professor para a turma */}
                    <div className="d-flex flex-column align-items-center mb-4">
                        {ano && (
                            <>
                                {semestre && (
                                    <h3 key={semestre} className="mb-2 fw-medium text-secondary fs-3">{ano} - {semestre}° Semestre</h3>
                                )}

                            </>
                        )}
                    </div>
                    <Row>
                        <Col className="d-flex flex-column justify-content-center align-items-center gap-3">
                            <h3 className="text-secondary text-center fs-3">Selecione todas as disciplinas que deseja finalizar</h3>
                            <Form.Check
                                label="Selecionar todas as disciplinas que deseja finalizar"
                                name="Selecionar todas as disciplinas que deseja finalizar"
                                title="Clique aqui para selecionar todas as disciplinas"
                                type="checkbox"
                                id="SelectAllDisciplinas"
                                checked={disciplinasSelecionadas.length === todasDisciplinas.length}
                                onChange={handleSelecionarTodas}

                                isInvalid={!!errors.disciplinasSelecionadas}
                                className="mb-2 fw-medium text-secondary fs-5"

                            />
                        </Col>
                    </Row>
                    <FormGroup className="mt-3 d-flex justify-content-around">
                        {/* Turnos*/}
                        <div className="gap-2">
                            <FormLabel className='text-secondary fs-4 fw-medium'>
                                Manhã
                            </FormLabel>
                            {/* Caso tenha disciplinas de manhã */}
                            {disciplinas ?
                                //Filtra primeiro para depois mapear 
                                disciplinas.filter(disciplina => disciplina.turnos.includes("Manhã"))
                                    .map((d) => {
                                        return (
                                            <Form.Check
                                                key={`${d.id}-manha`}
                                                label={d.nome}
                                                name={`${d.nome}-manha`}
                                                title={d.nome + " manhã"}
                                                type="checkbox"
                                                id={`Select${d.nome}Manha`}
                                                className="mb-3 fw-medium text-secondary fs-5"
                                                checked={disciplinasSelecionadas.includes(`Select${d.nome}Manha`)}
                                                onChange={() => handleSelecionarIndividual(`Select${d.nome}Manha`)}
                                                isInvalid={!!errors.disciplinasSelecionadas}
                                            />
                                        )
                                    }) : (<h3 className="text-secondary fs-4 fw-medium">Curso sem disciplinas do périodo da Manhã</h3>)}
                        </div>
                        <div className="gap-2">
                            <FormLabel className='text-secondary fs-4 fw-medium'>
                                Tarde
                            </FormLabel>
                            {/* Caso tenha disciplinas de tarde */}
                            {disciplinas ?
                                disciplinas.filter(disciplina => disciplina.turnos.includes("Tarde"))
                                    .map((d) => {
                                        return (
                                            <Form.Check
                                                key={`${d.id}-tarde`}
                                                label={d.nome}
                                                name={`${d.nome}-tarde`}
                                                title={d.nome + " tarde"}
                                                type="checkbox"
                                                id={`Select${d.nome}Tarde`}
                                                className="mb-3 fw-medium text-secondary fs-5"
                                                checked={disciplinasSelecionadas.includes(`Select${d.nome}Tarde`)}
                                                onChange={() => handleSelecionarIndividual(`Select${d.nome}Tarde`)}
                                                isInvalid={!!errors.disciplinasSelecionadas}
                                            />
                                        )
                                    }) : (<h3 className="text-secondary fs-4 fw-medium">Curso sem disciplinas do périodo da Tarde</h3>)}
                        </div>
                        <div className="gap-2">
                            <FormLabel className='text-secondary fs-4 fw-medium'>
                                Noite
                            </FormLabel>
                            {/* Caso tenha disciplinas de noite */}
                            {disciplinas ?
                                disciplinas.filter(disciplina => disciplina.turnos.includes("Noite"))
                                    .map((d) => {
                                        return (
                                            <Form.Check
                                                key={`${d.id}-noite`}
                                                label={d.nome}
                                                name={`${d.nome}-noite`}
                                                title={d.nome + " noite"}
                                                type="checkbox"
                                                id={`Select${d.nome}Noite`}
                                                className="mb-3 fw-medium text-secondary fs-5"
                                                checked={disciplinasSelecionadas.includes(`Select${d.nome}Noite`)}
                                                onChange={() => handleSelecionarIndividual(`Select${d.nome}Noite`)}
                                                isInvalid={!!errors.disciplinasSelecionadas}
                                            />
                                        )
                                    }) : (<h3 className="text-secondary fs-4 fw-medium">Curso sem disciplinas do périodo da Noite</h3>)}
                        </div>
                    </FormGroup>
                    {/* Exibe erro de validação se a lista estiver vazia */}
                    {errors.disciplinasSelecionadas && (
                        <Row className="justify-content-center mb-4">
                            <Col xs={12} md={8} lg={6}>
                                <div className="text-danger fw-bold text-center">
                                    {errors.disciplinasSelecionadas?.message}
                                </div>
                            </Col>
                        </Row>
                    )}
                    {/* Botão de Cadastrar */}
                    <Row className="mt-5">
                        <Col>
                            <Button
                                variant="primary"
                                type="submit"
                                title="Clique aqui para finalizar as disciplinas selecionadas"
                                id='btn-cadastro'
                                className='fs-4 fw-bold w-100 py-2'
                            >
                                Finalizar Disciplinas
                            </Button>
                            {/* Renderiza o alerta de sucesso após passar nas validações */}
                            {exibirResultado.show && (
                                <Alert variant={exibirResultado.variant} onClose={() => setExibirResultado({ ...exibirResultado, show: false })} dismissible className="mt-3" >
                                    {exibirResultado.message}
                                </Alert>
                            )}
                        </Col>
                    </Row>
                </Form>
            </Container >
        </>
    )
}
export default FinalizarDisciplinas