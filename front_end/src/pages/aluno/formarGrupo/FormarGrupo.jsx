import addIcon from '../../../assets/add.svg'
import CancelIcon from '../../../assets/Cancel.svg'
import "./formarGrupo.css"
import { Alert, Col, Container, FormControl, FormLabel, FormSelect, ListGroup, Row, Spinner, Stack, Table } from 'react-bootstrap';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import { FormGroup } from "react-bootstrap";
import { useEffect, useState } from 'react';
import TableComponent from '../../../components/table/TableComponent';
import UserNavBar from '../../../components/usernavbar/UserNavBar';

// Zod e RHF para validações
import { useFieldArray, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { camposSchema } from '../../../schemas/aluno/formarGrupo/formarGrupoZodSchema';


import { grupoService } from '../../../services/grupotg/grupoService';
import { alunoService } from '../../../services/aluno/alunoService';

const FormarGrupo = () => {
    // Estado para exibição do resultado de requisições
    const [resultado, setResultado] = useState({
        exibir: false,
        variante: "",
        mensagem: "",
    });

    const {
        register,
        handleSubmit,
        control,
        formState: { errors },
        reset
    } = useForm({
        resolver: zodResolver(camposSchema),
        defaultValues: {
            tema: "",
            descricaoTema: "",
            tipoTG: "",
            aluno: "", // Usado apenas para o input de digitação
            integrantes: [] // Referência para validação
        }
    });


    //Gerenciamento de arrays dinâmicos(substitui useStates manuais)
    const { fields, append, remove } = useFieldArray({
        control,
        //Nome do array no Schema
        name: "integrantes"
    });

    // ======== ESTADOS DA API ========
    const [alunosDisponiveis, setAlunosDisponiveis] = useState([]);
    const [carregandoAlunos, setCarregandoAlunos] = useState(false);
    const [enviando, setEnviando] = useState(false);

    // ======== ESTADOS DO AUTOCOMPLETE ========
    // Estado do que esta escrito no input de aluno
    const [buscaAluno, setBuscaAluno] = useState("")
    //Lista filtrada de opções com base no input
    const [sugestoes, setSugestoes] = useState([]);
    const [alunoSelecionado, setAlunoSelecionado] = useState(null)


    // ======== EFEITOS ========
    useEffect(() => {
        const carregarAlunosElegiveis = async () => {
            try {
                setCarregandoAlunos(true);
                const resposta = await alunoService.buscarAlunosElegiveisParaGrupo();

                const listaAlunos = resposta.conteudo || resposta || [];

                const formatados = listaAlunos.map(aluno => ({
                    // Usa a matrícula como ID único se o backend não mandar um ID próprio
                    id: aluno.id || aluno.matricula,
                    nome: aluno.nome,
                    matricula: aluno.matricula
                }));

                setAlunosDisponiveis(formatados);
            } catch (error) {
                console.error("Erro ao carregar lista de alunos:", error);
                setResultado({
                    exibir: true,
                    variante: "danger",
                    mensagem: "Não foi possível carregar a lista de alunos disponíveis."
                });
            } finally {
                setCarregandoAlunos(false);
            }
        };

        carregarAlunosElegiveis();
    }, []);


    // ======== LÓGICA DE AUTOCOMPLETE ========
    // Filtra sugestões garantindo que o aluno não esteja no grupo (fields)
    const handleBuscaAluno = (e) => {
        const termo = e.target.value;
        setBuscaAluno(termo);
        // Reseta a seleção se o usuário voltar a digitar
        setAlunoSelecionado(null);

        const opcoesDisponiveis = alunosDisponiveis.
            filter(aluno => !fields.some(i => i.id === aluno.id))

        if (termo.length > 1) { // Só filtra após digitar 2 letras
            const filtrados = alunosDisponiveis.filter(aluno =>
                aluno.nome.toLowerCase().includes(termo.toLowerCase()) &&
                !fields.some(jaAdicionado => jaAdicionado.id === aluno.id) // Não sugere quem já está no grupo
            );
            setSugestoes(filtrados);
        } else {
            //Se apagar tudo exibe as 3 primeiras opcoes
            setSugestoes(opcoesDisponiveis.slice(0, 3));
        }
    };

    const selecionarSugestao = (aluno) => {
        setBuscaAluno(aluno.nome);
        setAlunoSelecionado(aluno);
        setSugestoes([]);
    };

    const handleSugestoesFocus = () => {
        // Quando clica no input, se estiver vazio, mostra os 5 primeiros disponíveis
        if (buscaAluno.length === 0) {
            const opcoesDisponiveis = alunosDisponiveis.
                filter(aluno => !fields.some((i) => i.id === aluno.id))
                .slice(0, 3)
            //Exibe as primeiras 3 opções
            setSugestoes(opcoesDisponiveis)
        }
    }

    const handleSugestoesBlur = () => {
        setTimeout(() => {
            setSugestoes([])
        }, 200)

    }
    // Tabela
    const colunas = [
        { header: "Nome do aluno", accessor: "nome" },
        {
            header: "Remover",
            // Render customizado para os botões de Aceitar/Recusar
            render: (row) => (
                <Stack direction="horizontal" gap={5} className="justify-content-center" >
                    <img
                        src={CancelIcon}
                        alt="Remover"
                        style={{ cursor: 'pointer', width: '3rem' }}
                        onClick={() => {
                            // Encontra o índice exato deste aluno específico no array do formulário
                            const indexExato = fields.findIndex(integrante => integrante.id === row.id);
                            // Se encontrou, remove só ele
                            if (indexExato !== -1) {
                                remove(indexExato);
                            }
                        }}
                    />

                </Stack >
            )

        }
    ]

    const adicionarIntegrante = () => {
        // Só adiciona se houver um aluno selecionado do dropdown
        if (alunoSelecionado) {
            //Adiciona no RHF, Zod fará a validação pelo array integrantes
            append({
                id: alunoSelecionado.matricula,
                nome: alunoSelecionado.nome,
                matricula: alunoSelecionado.matricula
            });
            //Limpa a busca
            setBuscaAluno("");
            setAlunoSelecionado(null);
            setSugestoes([]);
        }
    };


    // ======== INTEGRAÇÃO COM BACK-END ========
    const enviarParaBackend = async (dadosValidados) => {
        setResultado({ exibir: false, variante: "", mensagem: "" });
        //Inicia o carregamento visual
        setEnviando(true);
        try {
            const payloadJava = {
                //TODO: Tirar do backend o cursoId e a disciplina, pegar do próprio aluno logado com o jwt
                tema: dadosValidados.tema,
                descricaoTema: dadosValidados.descricaoTema,
                tipoTg: dadosValidados.tipoTG.toUpperCase().replace("-", "_"),
                matriculasAlunos: dadosValidados.integrantes.map(aluno => aluno.matricula)
            };

            await grupoService.criarGrupo(payloadJava);

            setResultado({
                exibir: true,
                variante: "success",
                mensagem: "Grupo Criado com sucesso!"
            });
            // Limpa o formulário após criar o grupo
            reset();
            // Limpa o autocomplete
            setBuscaAluno("");
            setAlunoSelecionado(null);

        } catch (erro) {
            console.error("Erro ao gerar grupo:", erro);
            setResultado({
                exibir: true,
                variante: "danger",
                mensagem: erro.message || "Erro ao criar grupo. Verifique as informações."
            });
        } finally {
            //Finaliza o carregamento visual
            setEnviando(false);
        }
    };
    return (
        <>
            <UserNavBar
                userName='Aluno'
                maxWidth='1200px'
            />
            <Container className="mt-5 text-center" style={{ maxWidth: "1200px" }}>
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Formar Grupo</h2>
                <Form
                    noValidate
                    onSubmit={handleSubmit(enviarParaBackend)}
                    className='form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm px-5'>

                    {/* Tema */}
                    <Row className="mb-4 justify-content-center align-items-center">
                        <Col md={2} className="text-end">
                            <FormLabel className='text-secondary fs-4 fw-bold m-0'>Tema:</FormLabel>
                        </Col>
                        <Col md={6}>
                            <FormControl
                                name="tema"
                                {...register("tema")}
                                isInvalid={!!errors.tema}
                                type="text"
                                placeholder="Digite o tema do grupo"
                                className='bg-white text-black fw-bold fs-5'
                            />
                            <Form.Control.Feedback type="invalid">{errors.tema?.message}</Form.Control.Feedback>
                        </Col>
                        <Col md={1}></Col> {/* Coluna vazia para compensar o ícone do aluno abaixo */}
                    </Row>
                    {/* Descrição do Tema */}
                    <Row className="mb-4 justify-content-center align-items-center">
                        <Col md={2} className="text-end">
                            <FormLabel className='text-secondary fs-4 fw-bold m-0'>Descrição:</FormLabel>
                        </Col>
                        <Col md={6}>
                            <FormControl
                                as="textarea"
                                rows={3}
                                name="descricaoTema"
                                {...register("descricaoTema")}
                                isInvalid={!!errors.descricaoTema}
                                placeholder="Descreva brevemente o tema do grupo (mínimo de 50 caracteres)"
                                className='bg-white text-black fw-bold fs-5'
                            />
                            <Form.Control.Feedback type="invalid">{errors.descricaoTema?.message}</Form.Control.Feedback>
                        </Col>
                        <Col md={1}></Col>
                    </Row>
                    {/* Tipo de TG */}
                    <Row className="mb-4 justify-content-center align-items-center">
                        <Col md={2} className="text-end">
                            <FormLabel className='text-secondary fs-4 fw-bold m-0'>Tipo de TG:</FormLabel>
                        </Col>
                        <Col md={6}>
                            <FormSelect
                                name="tipoTG"
                                {...register("tipoTG")}
                                isInvalid={!!errors.tipoTG}
                                className='bg-white text-black fw-bolder fs-5'
                            >
                                <option value="" disabled>Selecione o tipo de trabalho</option>
                                <option value="monografia">Monografia</option>
                                <option value="desenvolvimento">Desenvolvimento</option>
                                <option value="artigo">Artigo</option>
                                <option value="plano-de-negocio">Plano de negocio</option>
                            </FormSelect>
                            <Form.Control.Feedback type="invalid">{errors.tipoTG?.message}</Form.Control.Feedback>
                        </Col>
                        <Col md={1}></Col>
                    </Row>

                    {/* Aluno */}
                    <Row className="mb-5 d-flex align-items-center">
                        <Col md={3} className="text-end">
                            <FormLabel className='text-secondary fs-4 fw-bold m-0'>Aluno:</FormLabel>
                        </Col>
                        <Col md={6} style={{ position: 'relative' }}> {/* Importante: relative para o dropdown */}
                            <FormControl
                                value={buscaAluno}
                                onChange={handleBuscaAluno}
                                onFocus={handleSugestoesFocus}
                                onBlur={handleSugestoesBlur}
                                autoComplete="off"
                                disabled={carregandoAlunos}
                                placeholder={carregandoAlunos ? "Carregando alunos disponíveis..." : "Digite ou selecione o nome"}
                                className='bg-white text-black fw-bold fs-5 '
                                isInvalid={!!errors.integrantes}
                            />

                            {/* Lista de Sugestões */}
                            {sugestoes.length > 0 && (
                                <ul className="list-group position-absolute shadow-lg" style={{ zIndex: 1000, top: '100%', width: "95%" }}>
                                    {sugestoes.map(aluno => (
                                        <ListGroup.Item
                                            key={aluno.id}
                                            action
                                            className="list-group-item list-group-item-action cursor-pointer py-2 fs-6"
                                            onClick={() => selecionarSugestao(aluno)}
                                        >
                                            <div className="fw-bold">{aluno.nome}</div>
                                        </ListGroup.Item>
                                    ))}
                                </ul>
                            )}
                        </Col>
                        <Col md={1} className="text-start p-0 d-flex align-items-center" >
                            {carregandoAlunos ? (
                                <Spinner animation="border" variant="primary" size="sm" className="ms-2" />
                            ) : (
                                <img
                                    src={addIcon} alt="Add"
                                    style={{
                                        cursor: alunoSelecionado ? 'pointer' : 'not-allowed',
                                        width: '45px',
                                        opacity: alunoSelecionado ? 1 : 0.4
                                    }}
                                    // Só clica se tiver selecionado
                                    onClick={alunoSelecionado ? adicionarIntegrante : undefined}
                                    title="Adicionar integrante"
                                />
                            )}
                        </Col>
                    </Row>


                    {/* tabela de integrantes */}
                    <FormGroup className="mb-3 d-flex flex-column" controlId="formBasicConfirmPassword">
                        <FormLabel className='text-secondary fs-4 fw-bold text-center'>Integrantes do Grupo</FormLabel>
                        <div className="mx-auto w-75">
                            <TableComponent
                                colunas={colunas}
                                dados={fields}
                            />
                        </div>
                        {/* Exibe erro de validação se a lista estiver vazia */}
                        {errors.integrantes && (
                            <div className="text-danger text-center fw-bold mt-2">
                                {errors.integrantes.message || errors.integrantes.root?.message}
                            </div>
                        )}
                    </FormGroup>

                    {/* Botão de Criar Grupo */}
                    <FormGroup className="text-center">
                        <Button
                            variant="primary"
                            type="submit"
                            id='btn-cadastro' className='mb-2 fs-4 fw-medium w-100'
                            disabled={enviando}
                        >
                            Criar Grupo
                        </Button>
                    </FormGroup>

                </Form>

                {/* Renderiza o alerta de resultado após passar nas validações */}
                {resultado.exibir && (
                    <Alert
                        variant={resultado.variante}
                        onClose={() => setResultado({ ...resultado, exibir: false })}
                        dismissible
                        className="mt-4 fw-bold fs-5 shadow-sm"
                    >
                        {resultado.mensagem}
                    </Alert>
                )}
            </Container >
        </>
    );
}

export default FormarGrupo;