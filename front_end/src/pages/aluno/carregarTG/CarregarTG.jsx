import { Button, Container, Form, FormControl, FormGroup, FormLabel, Toast, ToastContainer } from "react-bootstrap"
import UserNavBar from "../../../components/usernavbar/UserNavBar"
import "./CarregarTG.css"
import { useState } from "react"

//RHF e Zod
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm, useWatch } from "react-hook-form"
import { camposSchema } from "../../../schemas/aluno/carregarTG/carregarTGZodSchema"
import { grupoService } from "../../../services/grupotg/grupoService"


const CarregarTG = () => {
    const [resultado, setResultado] = useState({
        exibir: false, mensagem: "", variante: ""
    })

    const {
        register,
        control,
        reset,
        handleSubmit,
        // isSubmitting adicionado para bloquear duplo clique
        formState: { errors, isSubmitting }
    } = useForm({
        resolver: zodResolver(camposSchema),
        defaultValues: {
            // Arquivos começam com undefined
            arquivo: undefined
        }
    })

    // Observa arquivo para pegar nome e exibir
    const arquivoSelecionado = useWatch({
        control,
        name: "arquivo"
    });
    const nomeArquivo = arquivoSelecionado && arquivoSelecionado.length > 0 ? arquivoSelecionado[0].name : null;

    // Comunicação com Backend
    const enviarParaBackend = async (dadosValidados) => {
        try {
            // Limpa qualquer mensagem de erro anterior
            setResultado({ exibir: false, mensagem: "", variante: "" });

            // Extrai o arquivo real (o [0] pega o arquivo dentro da FileList gerada pelo input type="file")
            const arquivoFisico = dadosValidados.arquivo[0];

            // Chama a service passando o arquivo
            await grupoService.enviarTrabalhoGraduacao(arquivoFisico);

            // Sucesso
            setResultado({
                exibir: true,
                variante: "success",
                mensagem: "Trabalho de Graduação enviado com sucesso!"
            });

            // Limpa o formulário
            reset({ arquivo: undefined });

        } catch (e) {
            console.error(e);
            setResultado({
                exibir: true,
                variante: "danger",
                mensagem: e.message || "Erro ao processar o arquivo. Verifique o documento e tente novamente."
            });
        }
    };

    return (
        <>
            <UserNavBar
                userName="Aluno"
                maxWidth="1000px"
            ></UserNavBar>

            <Container className="mt-5 text-center" style={{ maxWidth: "1000px" }}>

                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>
                    Envio de Trabalho de Graduação
                </h2>

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

                        {nomeArquivo && !errors.arquivo && <p className="text-primary fw-bold mt-2">Arquivo pronto para envio!</p>}
                    </FormGroup>

                    <FormGroup className="text-center">
                        <Button
                            variant="primary"
                            type="submit"
                            id='btn-cadastro'
                            className='my-3 fs-4 fw-medium w-25'
                            disabled={isSubmitting} // Desabilita enquanto envia
                        >
                            {isSubmitting ? 'Enviando...' : 'Enviar'}
                        </Button>
                    </FormGroup>

                </Form>

                {/* Renderiza o toast de sucesso após passar nas validações */}
                {resultado.exibir && (
                    <ToastContainer
                        position="top-end"
                        className="p-3"
                        style={{ position: "fixed", zIndex: 9999 }}
                    >
                        <Toast
                            show={resultado.exibir}
                            onClose={() => setResultado({ exibir: false, variante: "", mensagem: "" })}
                            bg={resultado.variante} // Aproveitamos a string "success" ou "danger"
                        >
                            <Toast.Header>
                                <strong className="me-auto text-dark">
                                    {resultado.variante === "danger" ? "Atenção" : "Sucesso"}
                                </strong>
                            </Toast.Header>
                            <Toast.Body className="text-white fw-bold fs-6">
                                {resultado.mensagem}
                            </Toast.Body>
                        </Toast>
                    </ToastContainer>
                )}
            </Container>
        </>
    )
}
export default CarregarTG