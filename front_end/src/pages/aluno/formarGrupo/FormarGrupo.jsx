import addIcon from '../../../assets/add.svg'
import CancelIcon from '../../../assets/Cancel.svg'
import "./formarGrupo.css"
import { Alert, Col, Container, FormControl, FormLabel, FormSelect, ListGroup, Row, Stack, Table } from 'react-bootstrap';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import { FormGroup } from "react-bootstrap";
import { useState } from 'react';
import TableComponent from '../../../components/table/TableComponent';
import UserNavBar from '../../../components/usernavbar/UserNavBar';

// Zod e RHF para validações
import { useFieldArray, useForm } from 'react-hook-form';
import { z } from "zod";
import { zodResolver } from '@hookform/resolvers/zod';


//Schema de validação
const camposSchema = z.object({
    tema: z.string()
        .min(1, "O tema é obrigatório"),
    tipoTG: z.string()
        .min(1, "O Tipo de TG é um campo obrigatório"),
    integrantes: z.array(z.object({
        // Aceita string ou number
        id: z.union([z.string(), z.number()]),
        nome: z.string()
    })).min(1, "Adicione pelo menos um integrante ao grupo.")
})


const FormarGrupo = () => {
    const [exibirSucesso, setExibirSucesso] = useState(false)

    const {
        register,
        handleSubmit,
        control,
        formState: { errors }
    } = useForm({
        resolver: zodResolver(camposSchema),
        defaultValues: {
            tema: "",
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

    //Mock de alunos, TODO: pegar do backend

    const [listaAlunosDB] = useState([
        {
            id: 1,
            nome: "Joe",
        },
        {
            id: 2,
            nome: "Ana Maria",

        },
        {
            id: 3,
            nome: "Mariana Silva",
        },
        {
            id: 4,
            nome: "Leon Kennedy"
        },
        {
            id: 5,
            nome: "Jill Valentine"
        }
    ])

    // Estado do que esta escrito no input de aluno
    const [buscaAluno, setBuscaAluno] = useState("")
    //Lista filtrada de opções com base no input
    const [sugestoes, setSugestoes] = useState([]);
    const [alunoSelecionado, setAlunoSelecionado] = useState(null)

    // Filtra sugestões garantindo que o aluno não esteja no grupo (fields)
    const handleBuscaAluno = (e) => {
        const termo = e.target.value;
        setBuscaAluno(termo);
        setAlunoSelecionado(null); // Reseta a seleção se o usuário voltar a digitar

        const opcoesDisponiveis = listaAlunosDB.
            filter(aluno => !fields.some(i => i.id === aluno.id))

        if (termo.length > 1) { // Só filtra após digitar 2 letras
            const filtrados = listaAlunosDB.filter(aluno =>
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
            const opcoesDisponiveis = listaAlunosDB.
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
            render: (row, index) => (
                //index vem do mapeamento da tabela
                <Stack direction="horizontal" gap={5} className="justify-content-center">
                    <img
                        src={CancelIcon}
                        alt="Remover"
                        style={{ cursor: 'pointer', width: '3rem' }}
                        onClick={() => remove(index)}
                    />
                </Stack>
            )
        }
    ]

    const adicionarIntegrante = () => {
        // Só adiciona se houver um aluno selecionado do dropdown
        const alunoParaAdicionar = alunoSelecionado;

        if (alunoParaAdicionar) {
            //Adiciona no RHF, Zod fará a validação pelo array integrantes
            append({ id: alunoSelecionado.id, nome: alunoSelecionado.nome });
            //Limpa a busca
            setBuscaAluno("");
            setAlunoSelecionado(null);
            setSugestoes([]);
        }
    };


    // A função mock que realmente envia os dados caso passe na validação do frontend
    const enviarParaBackend = (dadosValidados) => {
        // Aqui vai o seu fetch/axios enviando o JSON para a API em Java
        console.log("Enviando payload para a API:", dadosValidados);
        //Ativa alerta de sucesso
        setExibirSucesso(true);
        //Esconde depois de alguns segundos
        setTimeout(() => setExibirSucesso(false), 5000);
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
                                placeholder="Digite ou selecione o nome do integrante"
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
                                            {aluno.nome}
                                        </ListGroup.Item>
                                    ))}
                                </ul>
                            )}
                        </Col>
                        <Col md={1}>
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
                        </Col>
                    </Row>


                    {/* tabela de integrantes */}
                    <FormGroup className="mb-3 d-flex flex-column" controlId="formBasicConfirmPassword">
                        <FormLabel className='text-secondary fs-4 fw-bold text-center'>Integrantes do Grupo</FormLabel>
                        <TableComponent
                            colunas={colunas}
                            dados={fields}
                        />
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
                        >
                            Criar Grupo
                        </Button>
                    </FormGroup>

                </Form>
                {/* Renderiza o alerta de sucesso após passar nas validações */}
                {exibirSucesso && (
                    <Alert variant="success" onClose={() => setExibirSucesso(false)} dismissible className="mt-3" >
                        Turma cadastrada com sucesso!
                    </Alert>
                )}
            </Container >
        </>
    );
}

export default FormarGrupo;