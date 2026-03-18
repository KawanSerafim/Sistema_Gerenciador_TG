
import { Container, FormControl, ListGroup, Col, Row, Alert } from 'react-bootstrap';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import UserNavBar from '../../../components/usernavbar/UserNavBar';
import { useState } from 'react';
import { useForm } from '../../../hooks/useForm';


const validarCampos = (valores) => {
    let erros = {}
    if (!valores.orientador) {
        erros.orientador = "Orientador é um campo obrigatório";
    }
    return erros
}

const SolicitarOrientacao = () => {

    const campos = {
        orientador: ""
    }
    const { errors, handleChange, handleSubmit } = useForm(campos, validarCampos);

    const [exibirSucesso, setExibirSucesso] = useState(false)

    // Estado do que esta escrito no input de orientador
    const [buscaOrientador, setBuscaOrientador] = useState("")
    //Lista filtrada de opções com base no input
    const [sugestoes, setSugestoes] = useState([]);
    const [orientadorSelecionado, setOrientadorSelecionado] = useState(null)

    //Mock de orientador, TODO buscar do backend
    const [listaOrientadorDB] = useState([
        { id: 1, nome: "Cristina", },
        { id: 2, nome: "Luciano", },
        { id: 3, nome: "Satoshi", },
        { id: 4, nome: "Colevati", },
    ])

    const handleSugestoesFocus = () => {
        if (buscaOrientador.length === 0) {
            const opcoesDisponiveis = listaOrientadorDB
                //Pega os 3 primeiros
                .slice(0, 3)
            setSugestoes(opcoesDisponiveis);
        }
    }

    const handleSugestoesBlur = () => {
        setTimeout(() => {
            setSugestoes([])
        }, 200)
    }

    const handleBuscaOrientador = (e) => {
        const termo = e.target.value;
        setBuscaOrientador(termo);
        // Reseta a seleção se o usuário voltar a digitar
        setOrientadorSelecionado(null);

        const opcoesDisponiveis = listaOrientadorDB
            //Pega os 3 primeiros
            .slice(0, 3)

        if (termo.length > 1) { // Só filtra após digitar 2 letras
            const filtrados = listaOrientadorDB.filter(orientador =>
                orientador.nome.toLowerCase().includes(termo.toLowerCase())
            );
            setSugestoes(filtrados);
        } else {
            setSugestoes(opcoesDisponiveis);
        }

        // Sincroniza com o useForm para validações
        handleChange(e);
    };

    const selecionarSugestao = (orientador) => {
        setBuscaOrientador(orientador.nome);
        setOrientadorSelecionado(orientador);
        setSugestoes([]);
    };

    const criarSolicitacao = () => {
        // Só adiciona se houver um orientador selecionado do dropdown
        const orientador = orientadorSelecionado;

        if (orientador) {
            setOrientadorSelecionado("");
            setOrientadorSelecionado(null);
            setSugestoes([]);

            // Sincroniza com o hook de validação
            handleChange({ target: { name: 'orientador', value: orientador } });
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
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Solicitar Orientação</h2>
                <Form noValidate
                    className='form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm px-5'
                    onSubmit={handleSubmit(enviarParaBackend)}
                >
                    <Row className="mb-4 d-flex flex-column align-items-center">
                        <Col md={6} style={{ position: 'relative' }}> {/* Importante: relative para o dropdown */}
                            <FormControl
                                name="orientador"
                                value={buscaOrientador}
                                onChange={handleBuscaOrientador}
                                onFocus={handleSugestoesFocus}
                                onBlur={handleSugestoesBlur}
                                autoComplete="off"
                                placeholder="Digite ou selecione o nome do orientador"
                                className='bg-white text-black fw-bold fs-5'
                                isInvalid={!!errors.orientador}
                            />

                            {/* Lista de Sugestões */}
                            {sugestoes.length > 0 && (
                                <ul className="list-group position-absolute shadow-lg" style={{ zIndex: 1000, top: '100%', width: "95%" }}>
                                    {sugestoes.map(orientador => (
                                        <ListGroup.Item
                                            key={orientador.id}
                                            action
                                            className="list-group-item list-group-item-action cursor-pointer py-2 fs-6"
                                            onClick={() => selecionarSugestao(orientador)}
                                        >
                                            {orientador.nome}
                                        </ListGroup.Item>
                                    ))}
                                </ul>
                            )}
                        </Col>
                        {/* Exibe erro de validação se a lista estiver vazia */}
                        {errors.orientador && (
                            <Alert variant="danger" onClose={() => setExibirSucesso(false)} dismissible className="mt-3" >
                                {errors.orientador}
                            </Alert>
                        )}
                    </Row>
                    <Button
                        variant="primary"
                        type="submit" id='btn-select'
                        className='mb-2 p-2 fs-4 fw-medium'
                        style={{
                            cursor: orientadorSelecionado ? 'pointer' : 'not-allowed',
                            opacity: orientadorSelecionado ? 1 : 0.4
                        }}
                        onClick={criarSolicitacao}
                        title="Enviar Solicitação"
                    >
                        Enviar solicitação
                    </Button>
                </Form>
                {/* Renderiza o alerta de sucesso após passar nas validações */}
                {exibirSucesso && (
                    <Alert variant="success" onClose={() => setExibirSucesso(false)} dismissible className="mt-3" >
                        {`Solicitação de orientação enviada com sucesso`}
                    </Alert>
                )}
            </Container>
        </>
    )
}

export default SolicitarOrientacao