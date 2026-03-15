import addIcon from '../../../assets/add.svg'
import CancelIcon from '../../../assets/Cancel.svg'
import "./formarGrupo.css"
import { Col, Container, FormControl, FormLabel, FormSelect, ListGroup, Row, Stack, Table } from 'react-bootstrap';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import { FormGroup } from "react-bootstrap";
import { useForm } from '../../../hooks/useForm';
import { useState } from 'react';
import TableComponent from '../../../components/table/TableComponent';

const validarGrupo = (valores) => {
    let erros = {};
    if (!valores.tema.trim()) erros.tema = "O tema é obrigatório.";
    if (!valores.tipoTG) erros.tipoTG = "Selecione o tipo de TG.";

    // verificar se há pelo menos um integrante na lista
    if (valores.integrantes.length === 0) {
        erros.aluno = "Adicione pelo menos um integrante ao grupo.";
    }
    return erros;
};
const FormarGrupo = () => {

    const campos = {
        tema: "",
        tipoTG: "",
        aluno: "", // Usado apenas para o input de digitação
        integrantes: [] // Referência para validação
    };

    const { values, errors, handleChange, handleSubmit } = useForm(campos, validarGrupo);

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

    //Alunos que estão na tabela
    const [integrantes, setIntegrantes] = useState([]);
    // Estado do que esta escrito no input de aluno
    const [buscaAluno, setBuscaAluno] = useState("")
    //Lista filtrada de opções com base no input
    const [sugestoes, setSugestoes] = useState([]);
    const [alunoSelecionado, setAlunoSelecionado] = useState(null)

    const handleBuscaAluno = (e) => {
        const termo = e.target.value;
        setBuscaAluno(termo);
        setAlunoSelecionado(null); // Reseta a seleção se o usuário voltar a digitar

        if (termo.length > 1) { // Só filtra após digitar 2 letras
            const filtrados = listaAlunosDB.filter(aluno =>
                aluno.nome.toLowerCase().includes(termo.toLowerCase()) &&
                !integrantes.some(jaAdicionado => jaAdicionado.id === aluno.id) // Não sugere quem já está no grupo
            );
            setSugestoes(filtrados);
        } else {
            setSugestoes([]);
        }

        // Sincroniza com o useForm para validações
        handleChange(e);
    };

    const selecionarSugestao = (aluno) => {
        setBuscaAluno(aluno.nome);
        setAlunoSelecionado(aluno);
        setSugestoes([]);
    };
    // Tabela
    const colunas = [
        { header: "Nome do aluno", accessor: "nome" },
        {
            header: "Remover",
            // Render customizado para os botões de Aceitar/Recusar
            render: (row) => (
                <Stack direction="horizontal" gap={5} className="justify-content-center">
                    <img
                        src={CancelIcon}
                        alt="Remover"
                        style={{ cursor: 'pointer', width: '3rem' }}
                        onClick={() => removerIntegrante(row.id)}
                    />
                </Stack>
            )
        }
    ]

    const adicionarIntegrante = () => {
        // Só adiciona se houver um aluno selecionado do dropdown ou se você permitir digitação livre
        const alunoParaAdicionar = alunoSelecionado;

        if (alunoParaAdicionar) {
            const novaLista = [...integrantes, alunoParaAdicionar];
            setIntegrantes(novaLista);
            setBuscaAluno("");
            setAlunoSelecionado(null);
            setSugestoes([]);

            // Sincroniza com o hook de validação
            handleChange({ target: { name: 'integrantes', value: novaLista } });
        }
    };

    const removerIntegrante = (id) => {
        const novaLista = integrantes.filter(aluno => aluno.id !== id);
        setIntegrantes(novaLista);

        // Sincroniza com o useForm
        handleChange({
            target: { name: 'integrantes', value: novaLista }
        });
    };

    const enviarDados = (dados) => {
        console.log("Grupo Criado:", { ...dados, integrantes });
    };
    return (
        <>
            <Container className="mt-5"  >
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Formar Grupo</h2>
                <Form
                    noValidate
                    onSubmit={handleSubmit(enviarDados)}
                    className='form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm px-5'>

                    {/* Tema */}
                    <Row className="mb-4 justify-content-center align-items-center">
                        <Col md={2} className="text-end">
                            <FormLabel className='text-secondary fs-4 fw-bold m-0'>Tema:</FormLabel>
                        </Col>
                        <Col md={6}>
                            <FormControl
                                name="tema"
                                value={values.tema}
                                onChange={handleChange}
                                isInvalid={!!errors.tema}
                                type="text"
                                placeholder="Digite o tema do grupo"
                                className='bg-white text-black fw-bold fs-5'
                            />
                            <Form.Control.Feedback type="invalid">{errors.tema}</Form.Control.Feedback>
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
                                value={values.tipoTG}
                                onChange={handleChange}
                                isInvalid={!!errors.tipoTG}
                                className='bg-white text-black fw-bolder fs-5 text-center'
                            >
                                <option value="" disabled>Selecione o tipo de tg do seu trabalho</option>
                                <option value="monografia">Monografia</option>
                                <option value="desenvolvimento">Desenvolvimento</option>
                                <option value="artigo">Artigo</option>
                                <option value="plano-de-negocio">Plano de negocio</option>
                            </FormSelect>
                            <Form.Control.Feedback type="invalid">{errors.tipoTG}</Form.Control.Feedback>
                        </Col>
                        <Col md={1}></Col>
                    </Row>

                    {/* Aluno */}
                    <Row className="mb-5 align-items-center">
                        <Col md={3} className="text-end">
                            <FormLabel className='text-secondary fs-4 fw-bold m-0'>Aluno:</FormLabel>
                        </Col>
                        <Col md={6} style={{ position: 'relative' }}> {/* Importante: relative para o dropdown */}
                            <FormControl
                                name="aluno"
                                value={buscaAluno}
                                onChange={handleBuscaAluno}
                                autoComplete="off"
                                placeholder="Digite ou selecione o nome do integrante"
                                className='bg-white text-black fw-bold fs-5'
                                isInvalid={!!errors.aluno}
                            />

                            {/* Lista de Sugestões */}
                            {sugestoes.length > 0 && (
                                <ul className="list-group position-absolute w-100 shadow-lg" style={{ zIndex: 1000, top: '100%' }}>
                                    {sugestoes.map(aluno => (
                                        <ListGroup.Item
                                            key={aluno.id}
                                            action
                                            className="list-group-item list-group-item-action cursor-pointer py-2"
                                            onClick={() => selecionarSugestao(aluno)}
                                            style={{ cursor: 'pointer' }}
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
                                onClick={adicionarIntegrante ? adicionarIntegrante : null}
                                title="Adicionar integrante"
                            />
                        </Col>
                    </Row>


                    {/* tabela de integrantes */}
                    <FormGroup className="mb-3 d-flex flex-column" controlId="formBasicConfirmPassword">
                        <FormLabel className='text-secondary fs-4 fw-bold text-center'>Integrantes do Grupo</FormLabel>
                        <TableComponent
                            columns={colunas}
                            data={integrantes}
                        />
                        {/* Exibe erro de validação se a lista estiver vazia */}
                        {errors.aluno && (
                            <small className="text-danger text-center fw-bold">{errors.aluno}</small>
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
            </Container >
        </>
    );
}

export default FormarGrupo;