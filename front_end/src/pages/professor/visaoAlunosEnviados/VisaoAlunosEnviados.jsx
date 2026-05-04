import { Alert, Container, Form, FormGroup, FormSelect, Spinner } from "react-bootstrap"
import UserNavBar from "../../../components/usernavbar/UserNavBar"
import TableComponent from "../../../components/table/TableComponent"

import { turmasService } from "../../../services/turmas/turmasService"
import { alunoService } from "../../../services/aluno/alunoService"
import { useEffect, useState } from "react"

const VisaoAlunosEnviados = () => {
    // ======== ESTADOS ==========
    const [turmas, setTurmas] = useState([]);
    const [turmaSelecionada, setTurmaSelecionada] = useState("");
    const [alunos, setAlunos] = useState([]);

    // Estados de UI (Carregamento e Erros)
    const [carregandoTurmas, setCarregandoTurmas] = useState(false);
    const [carregandoAlunos, setCarregandoAlunos] = useState(false);
    const [erro, setErro] = useState("");

    //TODO: Criar logica para preencher a tabela com os alunos quando a turma for selecionada

    //Colunas da tabela
    const columns = [
        { header: "Nome do aluno", accessor: "nome", filtravel: true, tipoFiltro: "autocomplete" },
        { header: "RA", accessor: "matricula", filtravel: true, tipoFiltro: "text" },
        { header: "Situação de Cadastro", accessor: "estado", filtravel: true, tipoFiltro: "select" }
    ]

    // ======== EFEITOS ==========

    // Busca as turmas assim que a tela abre
    useEffect(() => {
        const carregarTurmas = async () => {
            try {
                setCarregandoTurmas(true);
                const minhasTurmas = await turmasService.buscarMinhasTurmas();

                console.log("Retorno da API de Turmas:", minhasTurmas)
                if (minhasTurmas) {
                    setTurmas(minhasTurmas || []);
                }
            } catch (error) {
                console.error("Erro ao carregar turmas:", error);
                setErro("Não foi possível carregar suas turmas.");
            } finally {
                setCarregandoTurmas(false);
            }
        };

        carregarTurmas();
    }, []);

    //Busca os alunos sempre que o professor escolher uma turma diferente no Select
    useEffect(() => {
        //Sem turma selecionada, não faz nada
        if (!turmaSelecionada) return;

        const carregarAlunosDaTurma = async () => {
            try {
                setCarregandoAlunos(true);
                setErro(""); // Limpa erros anteriores
                setAlunos([]); // Limpa a tabela anterior

                // Chama o backend pedindo os alunos desta turma específica
                const resposta = await alunoService.buscarAlunosPorTurmaId(turmaSelecionada);

                // Formata os dados para a tabela exibir bonito
                const alunosFormatados = resposta.map(aluno => ({
                    id: aluno.id, // Importante ter um id único se a tabela precisar de key
                    nome: aluno.nome,
                    matricula: aluno.matricula,
                    // Traduz o Enum do Java para texto amigável
                    estado: traduzirStatus(aluno.estado)
                }));

                setAlunos(alunosFormatados);
            } catch (error) {
                console.error("Erro ao buscar alunos:", error);
                setErro("Erro ao buscar a lista de alunos desta turma.");
            } finally {
                setCarregandoAlunos(false);
            }
        };
        carregarAlunosDaTurma();
        //Sempre que a turmaSelecionada mudar aciona o efeito
    }, [turmaSelecionada])


    // ======== FUNÇÕES AUXILIARES ==========

    const traduzirStatus = (statusJava) => {
        switch (statusJava) {
            case 'PRE_CADASTRO':
                return "Email não confirmado";
            case 'AGUARDANDO_CONFIRMACAO':
                return "Aguardando confirmação do email";
            case 'CADASTRADO':
                return "Cadastro completo";
            default:
                return statusJava || "Desconhecido";
        }
    };

    return (
        <>
            <UserNavBar
                userName="Professor de TG"
                maxWidth="1500px"
            ></UserNavBar>
            <Container className="mt-5" style={{ maxWidth: "1500px" }}>
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Alunos enviados: </h2>

                <Form
                    noValidate
                    className='form-bg border border-dark border-top-0 p-2 p-md-4 rounded-bottom-4 shadow-sm'>

                    {/* Seleção de turma */}
                    <FormGroup className="my-3 d-flex justify-content-center gap-3" controlId="formSelectTurma">
                        <FormSelect
                            required={true}
                            title="Selecione a turma que deseja exibir"
                            value={turmaSelecionada}
                            onChange={(e) => setTurmaSelecionada(e.target.value)}
                            className='bg-white text-black fw-medium fs-4 w-50 text-center'
                        >
                            <option value="" disabled selected>
                                {carregandoTurmas ? "Carregando turmas..."
                                    : "Selecione a turma que deseja exibir"}
                            </option>
                            {/* Renderiza as turmas dinamicamente */}
                            {turmas.map(t => (
                                <option key={t.id} value={t.id}>
                                    {t.disciplina} - {t.turno}
                                </option>
                            ))}
                        </FormSelect>

                        {/* Carregamento visual dos alunos */}
                        {carregandoAlunos && (
                            <div className="text-primary fw-bold mt-2 d-flex align-items-center gap-2">
                                <Spinner animation="border" size="sm" />
                                <span>Buscando alunos...</span>
                            </div>
                        )}
                    </FormGroup>
                </Form>

                {/* Exibição de Erros */}
                {erro && (
                    <Alert variant="danger" className="mt-4 text-center fw-bold">
                        {erro}
                    </Alert>
                )}

                {/* Tabela de alunos, só exibe se tiver dados e não estiver carregando */}
                {!carregandoAlunos && alunos.length > 0 && (
                    <div className="mt-5">
                        <TableComponent
                            colunas={columns}
                            dados={alunos}
                        />
                    </div>
                )}
                {!carregandoAlunos && turmaSelecionada && alunos.length === 0 && !erro && (
                    <Alert variant="info" className="mt-4 text-center fw-bold">
                        Nenhum aluno encontrado para esta turma.
                    </Alert>
                )}

            </Container>
        </>
    )
}
export default VisaoAlunosEnviados