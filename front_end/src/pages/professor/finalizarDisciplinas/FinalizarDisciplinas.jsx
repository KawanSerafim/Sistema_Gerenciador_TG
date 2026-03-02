import { useState } from "react";
import { Button, Col, Container, Form, FormControl, FormGroup, FormLabel, Row } from "react-bootstrap";
import UserNavBar from "../../../components/usernavbar/UserNavBar";

const FinalizarDisciplinas = () => {
    //TODO: Trocar mocks pelos dados do backend

    const [validado, setValidado] = useState(false);

    // Disciplinas
    //TODO: buscar disciplinas do professor logado e colocar no estado
    const ano = 2026;
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

    const [disciplinas, setDisciplinas] = useState(mockDiscplinas)
    // Selecionando todas, TODO: atualizar de acordo com a resposta do backend
    const todasDisciplinas = ["SelectTG1Manha", "SelectTG2Manha", "SelectTG1Tarde", "SelectTG2Tarde", "SelectTG1Noite", "SelectTG2Noite"];


    const [disciplinasSelecionadas, setDisciplinasSelecionadas] = useState([])

    //Função para o checkbox "Selecionar Todas"
    const handleSelecionarTodas = (event) => {
        if (event.target.checked) {
            setDisciplinasSelecionadas(todasDisciplinas)
        } else {
            setDisciplinasSelecionadas([])
        }
    }

    //Função para os checkbox individuais
    const handleSelecionarIndividual = (disciplina) => {
        if (disciplinasSelecionadas.includes(disciplina)) {
            //Se ja esta na lista é por que o usuario quer tirar
            setDisciplinasSelecionadas(disciplinasSelecionadas.filter(item => item !== disciplina))
        } else {
            //Se não, adiciona na lista
            setDisciplinasSelecionadas([...disciplinasSelecionadas, disciplina]);
        }
    }


    const handleSubmit = (event) => {
        event.preventDefault();
        const form = event.currentTarget;
        if (form.checkValidity() === false) {
            event.stopPropagation();
        }
        setValidado(true);
    }



    return (
        <>
            <UserNavBar
                userName="Max"
            />
            <Container className="mt-5 align-items-center" style={{ minWidth: '800px' }}>
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Finalizar Disciplinas</h2>
                <Form
                    validated={validado}
                    className='border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm'
                    id="formTurma"
                    onSubmit={handleSubmit}

                >
                    {/* Linha 1: Seleção de um unico professor para a turma */}
                    <div className="d-flex flex-column align-items-center mb-4">
                        {ano && (
                            <>
                                {semestre && (
                                    <h3 key={semestre} className="mb-2 fw-medium text-secondary">{ano} - {semestre}° Semestre</h3>
                                )}

                            </>
                        )}
                    </div>
                    <Row>
                        <Col className="d-flex flex-column justify-content-center align-items-center gap-3">
                            <h3 className="text-secondary text-center">Selecionar todas as disciplinas que deseja finalizar</h3>
                            <Form.Check
                                label="Selecionar todas as disciplinas que deseja finalizar"
                                name="Selecionar todas as disciplinas que deseja finalizar"
                                type="checkbox"
                                id="SelectAllDisciplinas"
                                checked={disciplinasSelecionadas.length === todasDisciplinas.length}
                                onChange={handleSelecionarTodas}
                                className="mb-2 fw-medium text-secondary"

                            />
                        </Col>
                    </Row>
                    <FormGroup className="mt-3 d-flex justify-content-around">
                        {/* Turnos*/}
                        <div className="gap-2">
                            <FormLabel className='text-secondary fs-5 fw-medium'>
                                Manhã
                            </FormLabel>
                            {/* Caso tenha disciplinas de manhã */}
                            {disciplinas ? disciplinas.map((d) => {
                                if (d.turnos.includes("Manhã"))
                                    return (
                                        <Form.Check
                                            label={d.nome}
                                            name={`${d.nome}-manha`}
                                            type="checkbox"
                                            id={`Select${d.nome}Manha`}
                                            className="mb-2 fw-medium text-secondary"
                                            checked={disciplinasSelecionadas.includes(`Select${d.nome}Manha`)}
                                            onChange={() => handleSelecionarIndividual(`Select${d.nome}Manha`)}

                                        />
                                    )
                            }) : (<h3>Curso sem disciplinas do périodo da Manhã</h3>)}
                        </div>
                        <div className="gap-2">
                            <FormLabel className='text-secondary fs-5 fw-medium'>
                                Tarde
                            </FormLabel>
                            {/* Caso tenha disciplinas de tarde */}
                            {disciplinas ? disciplinas.map((d) => {
                                if (d.turnos.includes("Tarde"))
                                    return (
                                        <Form.Check
                                            label={d.nome}
                                            name={`${d.nome}-tarde`}
                                            type="checkbox"
                                            id={`Select${d.nome}Tarde`}
                                            className="mb-2 fw-medium text-secondary"
                                            checked={disciplinasSelecionadas.includes(`Select${d.nome}Tarde`)}
                                            onChange={() => handleSelecionarIndividual(`Select${d.nome}Tarde`)}
                                        />
                                    )
                            }) : (<h3>Curso sem disciplinas do périodo da Tarde</h3>)}
                        </div>
                        <div className="gap-2">
                            <FormLabel className='text-secondary fs-5 fw-medium'>
                                Noite
                            </FormLabel>
                            {/* Caso tenha disciplinas de noite */}
                            {disciplinas ? disciplinas.map((d) => {
                                if (d.turnos.includes("Noite"))
                                    return (
                                        <Form.Check
                                            label={d.nome}
                                            name={`${d.nome}-noite`}
                                            type="checkbox"
                                            id={`Select${d.nome}Noite`}
                                            className="mb-2 fw-medium text-secondary"
                                            checked={disciplinasSelecionadas.includes(`Select${d.nome}Noite`)}
                                            onChange={() => handleSelecionarIndividual(`Select${d.nome}Noite`)}
                                        />
                                    )
                            }) : (<h3>Curso sem disciplinas do périodo da Noite</h3>)}
                        </div>
                    </FormGroup>

                    {/* Botão de Cadastrar */}
                    <Row className="mt-5">
                        <Col>
                            <Button
                                variant="primary"
                                type="submit"
                                id='btn-cadastro'
                                className='fs-5 fw-bold w-100 py-2'
                            >
                                Finalizar Disciplinas
                            </Button>
                        </Col>
                    </Row>
                </Form>
            </Container >
        </>
    )
}
export default FinalizarDisciplinas