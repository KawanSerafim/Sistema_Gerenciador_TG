import { Button, Container, Form, FormControl, FormGroup, FormLabel, FormSelect } from "react-bootstrap"
import UserNavBar from "../../../components/usernavbar/UserNavBar"
import { useState } from "react"
import TableComponent from "../../../components/table/TableComponent"
import "./EnviarTurma.css"

const EnviarTurma = () => {
    //TODO: Add hook de validação

    const [fileName, setFileName] = useState("");

    const handleFileChange = (e) => {
        if (e.target.files.length > 0) {
            setFileName(e.target.files[0].name);
        }
    }

    const [showSendInput, setShowSendInput] = useState(false)

    const handleSelectChange = (e) => {
        //Se diferente de vazio foi selecionado uma opção valida
        if (e.target.value !== "") {
            setShowSendInput(true)
        } else {
            setShowSendInput(false);
        }
    }

    const colunas = [
        { header: "Nome do aluno", accessor: "nome" },
        { header: "RA", accessor: "ra" }
    ]

    const data = [{}]

    return (
        <>
            <UserNavBar
                userName="Professor de TG"
                maxWidth="1200px"

            ></UserNavBar>
            <Container className="mt-5" style={{ maxWidth: "1200px" }}>
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Envio de planilha de alunos</h2>
                <Form
                    noValidate
                    className='form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm'>

                    {/* Seleção de turma */}
                    <FormGroup className="mb-3 d-flex justify-content-center gap-3" controlId="formSelectTurma">
                        <FormSelect
                            required={true}
                            title="Selecionar turma de TG"
                            className='bg-white text-black fw-medium fs-4 w-50 text-center'
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
                                {/* Input verdadeiro escondido */}
                                <FormControl type="file"
                                    id="input-arquivo-turma"
                                    style={{ display: 'none' }}
                                    onChange={handleFileChange}
                                    accept=".xlsx"
                                    required={true} className='input-send text-black fw-bold fs-4 w-75' />
                                {/* Label personalizada como botão */}
                                <FormLabel
                                    title="Clique aqui para escolher o arquivo .xlsx da turma"
                                    htmlFor="input-arquivo-turma"
                                    className="btn btn-lg input-send py-3 fw-bold fs-4 w-75 fw-bold shadow"
                                    style={{ cursor: "pointer" }}
                                >
                                    {fileName ? `Arquivo selecionado: ${fileName}` : "Clique aqui para selecionar a planilha de alunos (apenas .xlsx)"}
                                </FormLabel>
                                {/* Feedback visual */}
                            </FormGroup>
                            <FormGroup className="text-center">
                                <Button
                                    variant="primary"
                                    type="submit"
                                    title="Enviar turma"
                                    id='btn-cadastro' className='mb-2 fs-4 fw-medium w-25'
                                >
                                    Enviar Turma
                                </Button>
                            </FormGroup>
                            {fileName && <p className="text-primary mt-2 text-center">Arquivo pronto para envio!</p>}
                        </>
                    )}

                </Form>
                <div className="mt-5">
                    <TableComponent
                        colunas={colunas}
                        dados={data}
                    />
                </div>
            </Container>
        </>
    )
}
export default EnviarTurma