import { Button, Container, Form, FormControl, FormGroup, FormLabel } from "react-bootstrap"
import UserNavBar from "../../../components/usernavbar/UserNavBar"
import "./CarregarTG.css"
import { useState } from "react"

const CarregarTG = () => {
    const [fileName, setFileName] = useState("");

    const handleFileChange = (e) => {
        if (e.target.files.length > 0) {
            setFileName(e.target.files[0].name);
        }
    }


    return (
        <>
            <UserNavBar
                userName="Aluno"
            ></UserNavBar>
            <Container className="mt-5 text-center">
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Envio de Trabalho de Graduação</h2>
                <Form
                    validated={true}
                    className='border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm'>

                    {/* O input original */}
                    <FormControl
                        type="file"
                        id="input-arquivo-tg"
                        style={{ display: "none" }}
                        onChange={handleFileChange}
                        accept=".pdf"
                    />
                    {/* Label personalizada como botão */}
                    <FormLabel
                        htmlFor="input-arquivo-tg"
                        className=" input-send btn btn-lg w-75 py-3 fs-4 fw-bold shadow "
                        style={{ cursor: 'pointer' }}
                    >
                        {/* Icone opcional (ex: Bootstrap Icons) */}
                        <i className="bi bi-cloud-upload me-2"></i>
                        {fileName ? `Arquivo selecionado: ${fileName}` : "Clique aqui para selecionar o trabalho de graduação (apenas .pdf)"}
                    </FormLabel>
                    {/* Feedback visual */}
                    {fileName && <p className="text-primary mt-2">Arquivo pronto para envio!</p>}

                    <FormGroup className="text-center">
                        <Button
                            variant="primary"
                            type="submit"
                            id='btn-cadastro' className='mb-2 fs-4 fw-medium w-25'
                        >
                            Enviar
                        </Button>
                    </FormGroup>
                </Form>
            </Container>
        </>
    )
}
export default CarregarTG