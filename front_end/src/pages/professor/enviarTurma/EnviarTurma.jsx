import { Alert, Button, Container, Form, FormControl, FormGroup, FormLabel, FormSelect, Spinner } from "react-bootstrap"
import UserNavBar from "../../../components/usernavbar/UserNavBar"
import TableComponent from "../../../components/table/TableComponent"
import "./EnviarTurma.css"

//RHF e Zod
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm, useWatch } from "react-hook-form"
import { enviarTurmaSchema } from "../../../schemas/professor/enviarTurma/enviarTurmaSchema"
import { useState } from "react"

import { alunoService } from "../../../services/aluno/alunoService"
import { turmasService } from "../../../services/turmas/turmasService"

//TODO: Substituir este mock por uma chamada GET à API para buscar as turmas do professor logado
const turmas = await turmasService.buscarMinhasTurmas();

const EnviarTurma = () => {

    // ======== ESTADOS ==========
    const [alunosCadastrados, setAlunosCadastrados] = useState([]);
    const [exibirResultado, setExibirResultado] = useState({ exibir: false, variante: "", mensagem: "" })
    const [carregando, setCarregando] = useState(false);

    //======== RHF =========
    const {
        register,
        control,
        reset,
        handleSubmit,
        formState: { errors }
    } = useForm({
        resolver: zodResolver(enviarTurmaSchema),
        defaultValues: {
            turmaId: '',
            //Arquivos começam com undefined
            arquivo: undefined
        }
    })

    // Observa se alguma turma foi selecionada, para liberar o input de envio
    const turmaSelecionada = useWatch({ control, name: "turmaId" })
    //Observa arquivo para pegar o nome e mostrar na tela 
    const arquivoSelecionado = useWatch({
        control,
        name: "arquivo"
    })
    const nomeArquivo = arquivoSelecionado && arquivoSelecionado.length > 0 ? arquivoSelecionado[0].name : null;

    const colunas = [
        { header: "Nome do aluno", accessor: "nome" },
        { header: "RA", accessor: "ra" }
    ]


    // Comunicação com Backend
    const enviarParaBackend = async (dadosValidados) => {
        try {
            setCarregando(true);
            setExibirResultado({ exibir: true, variante: "info", menssagem: "Enviando arquivo e processando alunos. Aguarde..." });
            setAlunosCadastrados([]); // Limpa a tabela anterior caso haja uma

            // Montagem do FormData
            const formData = new FormData();
            formData.append("idTurma", dadosValidados.turmaId);
            formData.append("arquivo", dadosValidados.arquivo[0]);

            console.log("Enviando para o backend...", dadosValidados.arquivo[0].name);

            // Chamada para o backend
            const respostaBackend = await alunoService.enviarPlanilhaAlunos(formData);

            // o Java devolva a lista de alunos [{ id, nome, ra }]
            setAlunosCadastrados(respostaBackend);

            setExibirResultado({ exibir: true, variante: "success", menssagem: "Turma enviada e alunos registrados com sucesso!" });

            // Limpa o formulário para um novo envio
            reset({ turmaId: '', arquivo: undefined });

        } catch (e) {
            console.error("Erro no envio:", e);
            setExibirResultado({
                show: true,
                variant: "danger",
                message: e.message || "Erro ao processar o arquivo. Verifique se a planilha não está corrompida e segue o padrão."
            });
        } finally {
            setCarregando(false);
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
                    <FormGroup className="mb-3" controlId="formSelectTurma">
                        <div className="d-flex justify-content-center">
                            <FormSelect
                                title="Selecionar turma de TG"
                                className='bg-white text-black fw-medium fs-4 w-50 text-center'
                                {...register("turmaId")}
                                isInvalid={!!errors.turmaId}
                                disabled={carregando}
                            >
                                <option value="" disabled selected>Selecione a turma que deseja exibir</option>
                                {turmas.map(t => (
                                    <option key={t.id} value={t.id}>{t.disciplina} - {t.turno}</option>
                                ))}

                            </FormSelect>
                        </div>
                        {errors.turmaId && <div className="text-danger text-center fw-bold mt-2">{errors.turmaId?.message}</div>}

                    </FormGroup>
                    {/* Exibe o input de arquivo apenas se a turma foi selecionada */}
                    {turmaSelecionada && (
                        <>
                            <FormGroup className="mb-3 gap-3" controlId="formSendTurma">
                                <div className="d-flex justify-content-center">

                                    {/* Input verdadeiro escondido */}
                                    <FormControl type="file"
                                        id="input-arquivo-turma"
                                        style={{ display: 'none' }}
                                        {...register("arquivo")}
                                        accept=".xlsx"
                                        disabled={carregando}
                                        className='input-send text-black fw-bold fs-4 w-75' />
                                    {/* Label personalizada como botão */}
                                    <FormLabel
                                        title="Clique aqui para escolher o arquivo .xlsx da turma"
                                        htmlFor="input-arquivo-turma"
                                        className={`btn btn-lg w-100 w-md-75 py-3 fs-5 fs-md-4 fw-bold shadow ${errors.arquivo ? 'btn-outline-danger' : 'input-send'}`}
                                        style={{ cursor: "pointer" }}
                                    >
                                        {nomeArquivo ? `Arquivo selecionado: ${nomeArquivo}` : "Clique aqui para selecionar a planilha de alunos (apenas .xlsx)"}
                                    </FormLabel>
                                </div>
                                {/* Feedback visual */}
                                {errors.arquivo && <div className="text-danger fw-bold mt-2 text-center">{errors.arquivo?.message}</div>}
                            </FormGroup>

                            <FormGroup className="text-center">
                                <Button
                                    variant="primary"
                                    type="submit"
                                    title="Enviar turma"
                                    id='btn-cadastro' className='mb-2 fs-4 fw-medium w-25'
                                    disabled={carregando}
                                >
                                    {carregando ? (
                                        <>
                                            <Spinner as="span" animation="border" size="sm" role="status" aria-hidden="true" className="me-2" />
                                            Enviando...
                                        </>
                                    ) : (
                                        "Enviar Turma"
                                    )}
                                </Button>
                            </FormGroup>
                            {nomeArquivo && !errors.arquivo && !carregando && <p className="text-primary mt-2 text-center">Arquivo pronto para envio!</p>}
                        </>
                    )}
                </Form>

                {/* Renderiza o alerta de sucesso após passar nas validações */}
                {exibirResultado.exibir && (
                    <Alert variant={exibirResultado.variante} onClose={() => setExibirResultado({ ...exibirResultado, exibir: false })} dismissible className="mt-3" >
                        {exibirResultado.mensagem}
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