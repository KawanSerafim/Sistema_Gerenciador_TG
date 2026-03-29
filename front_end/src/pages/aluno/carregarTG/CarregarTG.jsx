import { Alert, Button, Container, Form, FormControl, FormGroup, FormLabel } from "react-bootstrap"
import UserNavBar from "../../../components/usernavbar/UserNavBar"
import "./CarregarTG.css"
import { useState } from "react"

//RHF e Zod
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm, useWatch } from "react-hook-form"
import { camposSchema } from "./schema/carregarTGZodSchema"



//Mock: simulando que o aluno logado pertence ao grupo desse ID
const idGrupoSimulado = "123";

const CarregarTG = () => {

    const [exibirResultado, setExibirResultado] = useState({
        show: false, message: "", variant: ""
    })

    const {
        register,
        control,
        reset,
        handleSubmit,
        formState: {errors}
    } = useForm({
        resolver: zodResolver(camposSchema),
        defaultValues: {
            //TODO: Trocar mock por implementação real com o header do JWT
            grupoId: idGrupoSimulado,
            //Arquivos começam com undefined
            arquivo: undefined
        }
    })

    //Observa arquivo para pegar nome e exibir
    const arquivoSelecionado = useWatch({
        control,
        name: "arquivo"
    });
    const nomeArquivo = arquivoSelecionado && arquivoSelecionado.length > 0 ? arquivoSelecionado[0].name : null;

        // Simula a Camada de Serviço (Comunicação com Backend)
    const enviarParaBackend = async (dadosValidados) => {
        try {
            // Aqui você usaria o FormData para enviar o arquivo para o backend Java
            const formData = new FormData();
            formData.append("grupoId", dadosValidados.grupoId);
            formData.append("file", dadosValidados.arquivo[0]); // Pega o arquivo real

            console.log(`Enviando para o backend... ${dadosValidados.arquivo[0].name} dp grupo ${dadosValidados.grupoId}`);

            // Simula o delay da rede e a resposta do backend lendo o arquivo
            setTimeout(() => {
                setExibirResultado({ show: true, variant: "success", message: "Trabalho de Graduação enviado com sucesso" });
                // Limpa o formulário
                reset({grupoId: idGrupoSimulado, arquivo: undefined}); 
            }, 1500);

        } catch (e) {
            console.log(e)
            setExibirResultado({ show: true, variant: "danger", message: "Erro ao processar o arquivo. Verifique o documento e tente novamente" });
        }
    };
        
    return (
        <>
            <UserNavBar
                userName="Aluno"
                maxWidth="1000px"
            ></UserNavBar>
            <Container className="mt-5 text-center" style={{ maxWidth: "1000px" }}>

                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Envio de Trabalho de Graduação</h2>
                <Form
                    noValidate
                    className='form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm'
                    onSubmit={handleSubmit(enviarParaBackend)}>

                    {/* O input original */}
                    <FormControl
                        type="file"
                        id="input-arquivo-tg"
                        style={{ display: "none" }}
                        {...register("arquivo")}
                        accept=".pdf, .doc, .docx, .odt"
                    />
                    {/* Label personalizada como botão */}
                    <FormGroup className="mb-3 d-flex flex-column align-items-center">

                        <FormLabel
                            htmlFor="input-arquivo-tg"
                            className={`btn btn-lg w-100 w-md-75 py-3 fs-5 fs-md-4 fw-bold shadow ${errors.arquivo ? 'btn-outline-danger' : 'input-send'}`}
                            style={{ cursor: 'pointer' }}
                            >
                            {nomeArquivo ? `Arquivo selecionado: ${nomeArquivo}` : "Clique aqui para selecionar o trabalho de graduação (apenas .pdf, .doc, .docx ou .odt)"}
                        </FormLabel>

                            {/* Feedback visual */}
                            {errors.arquivo && <div className="text-danger fw-bold mt-2">{errors.arquivo?.message}</div>}
        
                            { nomeArquivo && !errors.arquivo && <p className="text-primary fw-bold mt-2">Arquivo pronto para envio!</p>}
                    </FormGroup>
                    <FormGroup className="text-center">
                        <Button
                            variant="primary"
                            type="submit"
                            id='btn-cadastro' className='my-3 fs-4 fw-medium w-25'
                            >
                            Enviar
                        </Button>
                    </FormGroup>

                </Form>
                {/* Renderiza o alerta de sucesso após passar nas validações */}
                    {exibirResultado.show && (
                    <Alert variant={exibirResultado.variant} onClose={() => setExibirResultado({...exibirResultado, show: false})} dismissible className="mt-3" >
                    {exibirResultado.message}
                    </Alert>
                )}
            </Container>
        </>
    )
}
export default CarregarTG