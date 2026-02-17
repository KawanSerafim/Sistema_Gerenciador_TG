import { Button, Container, Form, FormControl, FormGroup, FormLabel, FormSelect } from "react-bootstrap"
import UserNavBar from "../../../components/usernavbar/UserNavBar"
import { useState } from "react"
import TableComponent from "../../../components/table/TableComponent"
import "./EnviarTurma.css"

const EnviarTurma = () => {

    const [showSendInput, setShowSendInput] = useState(false)

    const handleSelectChange = (e) => {
        //Se diferente de vazio foi selecionado uma opção valida
        if (e.target.value !== "") {
            setShowSendInput(true)
        } else {
            setShowSendInput(false);
        }
    }

    const columns = [
        { header: "Nome do aluno", accessor: "nome" },
        { header: "RA", accessor: "ra" },
        { header: "Situação de Cadastro", accessor: "situacaoCadastro" }
    ]

    const data = [{}]

    return (
        <>
            <UserNavBar
                userName="Cristina"
            ></UserNavBar>
            <Container className="mt-5">
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Envio de planilha de alunos</h2>
                <Form
                    validated={true}
                    className='border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm'>

                    {/* Seleção de turma */}
                    <FormGroup className="mb-3 d-flex justify-content-center gap-3" controlId="formSelectTurma">
                        <FormSelect
                            required={true}
                            className='bg-primary text-white fw-bold fs-5 w-50 text-center'
                            onChange={(e) => handleSelectChange(e)}
                        >
                            <option value="" disabled selected>Selecione a turma que deseja exibir</option>
                            <option value="TG1N">TG1 - Noite</option>
                            <option value="TG2N">TG2 - Noite</option>
                            <option value="TG1T">TG1 - Tarde</option>
                            <option value="TG2T">TG2 - Tarde</option>
                        </FormSelect>
                    </FormGroup>
                    {showSendInput && (
                        <>
                            <FormGroup className="mb-3 d-flex justify-content-center gap-3" controlId="formSendTurma">
                                <FormControl type="file" title="Selecione o arquivo da turma .xlsx" placeholder="Selecione o arquivo da turma .xlsx" required={true} className='input-send text-black fw-bold fs-4 w-75' content="Selecione o arquivo da turma .xlsx" />
                            </FormGroup>
                            <FormGroup className="text-center">
                                <Button
                                    variant="primary"
                                    type="submit"
                                    id='btn-cadastro' className='mb-2 fs-4 fw-medium w-25'
                                >
                                    Cadastrar Turma
                                </Button>
                            </FormGroup>
                        </>
                    )}

                </Form>
                <div className="mt-5">
                    <TableComponent

                        columns={columns}
                        data={data}
                    />
                </div>
            </Container>
        </>
    )
}
export default EnviarTurma