import { Alert, Button, Container, Form, FormControl, FormGroup, FormLabel, FormSelect } from "react-bootstrap"
import UserNavBar from "../../../components/usernavbar/UserNavBar"
import TableComponent from "../../../components/table/TableComponent"
import "./EnviarTurma.css"

//RHF e Zod
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm, useWatch } from "react-hook-form"
import { enviarTurmaSchema } from "./schema/enviarTurmaSchema"
import { useState } from "react"

//Mock de turmas
const turmas = [
    {id: "TG1N", nome: "TG1 - Noite"},
    {id: "TG2N", nome: "TG2 - Noite"},
    {id: "TG1T", nome: "TG1 - Tarde"},
    {id: "TG2T", nome: "TG2 - Tarde"}
]

const EnviarTurma = () => {
// Estados exclusivos da interface (resultado da operação)
    const [alunosCadastrados, setAlunosCadastrados] = useState([]);
    const [exibirResultado, setExibirResultado] = useState({show: false, variant: "", message: ""})

    const {
        register,
        control,
        reset,
        handleSubmit,
        formState: errors
    } = useForm({
        resolver: zodResolver(enviarTurmaSchema),
        defaultValues: {
            turmaId: '',
            //Arquivos começam com undefined
            arquivo: undefined
        }
    })
    // Observa se alguma turma foi selecionada, para liberar o input de envio
    const turmaSelecionada = useWatch({control, name: "turmaId"})
    //Observa arquivo para pegar o noem e mostrar na tela 
    const arquivoSelecionado = useWatch({
        control,
        name: "arquivo"
    })
    const nomeArquivo = arquivoSelecionado && arquivoSelecionado.length > 0 ? arquivoSelecionado[0].name : null;

    const colunas = [
        { header: "Nome do aluno", accessor: "nome" },
        { header: "RA", accessor: "ra" }
    ]
        
    
    // Simula a Camada de Serviço (Comunicação com Backend)
    const enviarParaBackend = async (dadosValidados) => {
        try {
            // Aqui você usaria o FormData para enviar o arquivo para o backend Java
            const formData = new FormData();
            formData.append("turmaId", dadosValidados.turmaId);
            formData.append("file", dadosValidados.arquivo[0]); // Pega o arquivo real

            console.log("Enviando para o backend...", dadosValidados.arquivo[0].name);

            // Simula o delay da rede e a resposta do backend lendo o CSV/XLSX
            setTimeout(() => {
                const respostaBackend = [
                    { id: 1, nome: "Ana Costa", ra: "111222333" },
                    { id: 2, nome: "Bruno Silva", ra: "444555666" }
                ];
                
                setAlunosCadastrados(respostaBackend);
                setExibirResultado({ show: true, variant: "success", message: "Turma enviada e alunos registrados com sucesso!" });
                reset(); // Limpa o formulário
            }, 1500);

        } catch (e) {
            console.log(e)
            setExibirResultado({ show: true, variant: "danger", message: "Erro ao processar o arquivo. Verifique se a planilha não está corrompida." });
        }
    };
        
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
                    onSubmit={handleSubmit(enviarParaBackend)}
                    className='form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm'>

                    {/* Seleção de turma */}
                    <FormGroup className="mb-3 d-flex justify-content-center gap-3" controlId="formSelectTurma">
                        <FormSelect
                            title="Selecionar turma de TG"
                            className='bg-white text-black fw-medium fs-4 w-50 text-center'
                            {...register("turmaId")}
                            isInvalid={!!errors.turmaId}
                            >
                            <option value="" disabled selected>Selecione a turma que deseja exibir</option>
                            {turmas.map(t => (
                                
                                <option key={t.id} value={t.id}>{t.nome}</option>
                            ))}
                            
                        </FormSelect>
                        {errors.turmaId && <div className="text-danger text-center fw-bold mt-1">{errors.turmaId?.message}</div>}

                    </FormGroup>
                    {/* Exibe o input de arquivo apenas se a turma foi selecionada */}
                    {turmaSelecionada && (
                        <>

                            <FormGroup className="mb-3 d-flex justify-content-center gap-3" controlId="formSendTurma">
                                {/* Input verdadeiro escondido */}
                                <FormControl type="file"
                                    id="input-arquivo-turma"
                                    style={{ display: 'none' }}
                                    {...register("arquivo")}
                                    accept=".xlsx"
                                    required={true} className='input-send text-black fw-bold fs-4 w-75' />
                                {/* Label personalizada como botão */}
                                <FormLabel
                                    title="Clique aqui para escolher o arquivo .xlsx da turma"
                                    htmlFor="input-arquivo-turma"
                                    className="btn btn-lg input-send py-3 fw-bold fs-4 w-75 fw-bold shadow"
                                    style={{ cursor: "pointer" }}
                                    >
                                    {nomeArquivo ? `Arquivo selecionado: ${nomeArquivo}` : "Clique aqui para selecionar a planilha de alunos (apenas .xlsx)"}
                                </FormLabel>
                                {/* Feedback visual */}
                                {errors.arquivo && <div className="text-danger fw-bold mt-2">{errors.arquivo.message}</div>}
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
                            {nomeArquivo && <p className="text-primary mt-2 text-center">Arquivo pronto para envio!</p>}
                        </>
                    )}

                </Form>
                {/* Renderiza o alerta de sucesso após passar nas validações */}
                {exibirResultado.show && (
                    <Alert variant={exibirResultado.variant} onClose={() => setExibirResultado({...exibirResultado, show: false})} dismissible className="mt-3" >
                        {exibirResultado.message}
                    </Alert>
                )}
                {/* Renderiza a tabela se o backend retornar dados */}
                {alunosCadastrados.length > 0 && (

                <div className="mt-5">
                    <TableComponent
                        colunas={colunas}
                        dados={alunosCadastrados}
                        />
                </div>
                    )}
            </Container>
        </>
    )
}
export default EnviarTurma