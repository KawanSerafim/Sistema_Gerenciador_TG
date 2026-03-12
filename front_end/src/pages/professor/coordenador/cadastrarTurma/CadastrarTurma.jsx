import { useState } from "react";
import { Container, Form, FormGroup, FormSelect, FormLabel, Button, FormControl, Row, Col } from "react-bootstrap";
import UserNavBar from "../../../../components/usernavbar/UserNavBar";

const CadastrarTurma = () => {
    //TODO: Trocar mocks pelos dados do backend

    const [validado, setValidado] = useState(false);


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

    //Mock de professores
    const professoresTG = ["Cristina", "Luciano", "Antonio", "Rogerio", "Colevati"]


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
            <Container className="mt-5" style={{ minWidth: '800px' }}>
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Cadastro de Turmas</h2>
                <Form
                    validated={validado}
                    className='form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm'
                    id="formTurma"
                    onSubmit={handleSubmit}

                >
                    {/* Linha 1: Seleção de um unico professor para a turma */}
                    <div className="d-flex flex-column align-items-center mb-4">
                        <Form.Check
                            label="Unico professor para todas as turmas"
                            name="Unico professor para todas as turmas"
                            type="radio"
                            id="SelectOneProf"
                            className="mb-2 fw-medium text-secondary"
                        />

                        {/* Selecionar um Professor para todas as disciplina */}
                        <FormSelect className='bg-dark-subtle text-black fw-medium fs-5 w-75'>
                            <option key='' disabled selected> Selecione o professor de TG</option>
                            {professoresTG.map((professor) => (
                                <option key={professor} value={professor}>{professor}</option>
                            ))}
                        </FormSelect>

                    </div>
                    <hr className="my-4" />
                    {/* Linha 2: Opções de disciplinas e turnos */}
                    {optionsTurnos.map((turno) => (
                        <Row key={turno} className="mb-3">
                            {/* Dentro da linha itera as disciplinas (colunas) */}
                            {optionsDisciplinas.map((disciplina) => (
                                <Col md={6} key={`${disciplina}-${turno}`}>
                                    <FormGroup>
                                        {/* Selecionar Professor para disciplina */}
                                        <FormLabel className='text-secondary fs-5 fw-medium'>
                                            {disciplina} {turno}
                                        </FormLabel>
                                        <FormSelect required={true} className='bg-dark-subtle border-secondary-subtle text-black fw-medium fs-5'>
                                            <option value="" disabled selected>Selecione o professor de TG</option>
                                            {professoresTG.map((professor) => (
                                                <option key={professor} value={professor}>{professor}</option>
                                            ))}
                                        </FormSelect>
                                    </FormGroup>
                                </Col>
                            ))}
                        </Row>
                    ))}
                    {/* Seção inferior: ano e semestre */}
                    <Row className="mt-4 align-items-center">
                        {/* Coluna ano*/}
                        <Col md={3}>
                            <FormGroup controlId="formAno">
                                {/* Ano */}
                                <FormLabel className='text-secondary fs-6 fw-bold '>Ano </FormLabel>
                                <FormControl
                                    type="number"
                                    placeholder="2026"
                                    id="ano"
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