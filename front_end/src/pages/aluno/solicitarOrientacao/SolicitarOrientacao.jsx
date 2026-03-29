
import { Container, FormControl, ListGroup, Col, Row, Alert } from 'react-bootstrap';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import UserNavBar from '../../../components/usernavbar/UserNavBar';
import { useState } from 'react';

// Zod e RHF
import { solicitarOrientacaoZodSchema } from './schema/solicitarOrientacaoZodSchema';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm, useWatch } from 'react-hook-form';


const SolicitarOrientacao = () => {

    const {
        control, 
        setValue,
        formState: {errors},
        handleSubmit,
        reset 
    } = useForm({
        resolver: zodResolver(solicitarOrientacaoZodSchema),
        defaultValues: {
            orientadorId: ""
        }
    });

    //Observa o RHF para saber se um orientador valido foi selecionado
    const orientadorSelecionado = useWatch({
        control,
        name: "orientadorId"
    });

    /* === Estados visuais da interaçao com usuario ===*/
    const [exibirSucesso, setExibirSucesso] = useState(false)
    // Estado do que esta escrito no input de orientador
    const [buscaOrientador, setBuscaOrientador] = useState("")
    //Lista filtrada de opções com base no input
    const [sugestoes, setSugestoes] = useState([]);

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
        setValue("orientadorId", "", {shouldValidate: true})

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
    };

    const selecionarSugestao = (orientador) => {
        //Exibe o nome
        setBuscaOrientador(orientador.nome);
        // Injeta o id no RHF
        setValue("orientadorId",String(orientador.id), {shouldValidate: true});
        //Reseta suguestões
        setSugestoes([]);
    };

    // A função mock que realmente envia os dados caso passe na validação do frontend
    const enviarParaBackend = (dadosValidados) => {
        // Aqui vai o seu fetch/axios enviando o JSON para a API em Java
        console.log("Enviando payload para a API:", dadosValidados);
        //Ativa alerta de sucesso
        setExibirSucesso(true);
        //Limpa o input visual
        setBuscaOrientador("");
        //Limpa RHF
        reset();
        //Esconde depois de alguns segundos
        setTimeout(() => setExibirSucesso(false), 5000);
    };

    return (
        <>
            <UserNavBar
                userName='Aluno'
                maxWidth='1200px'
            />
            <Container className="mt-5 text-center px-3 px-md-0" style={{ maxWidth: "1200px" }}>
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Solicitar Orientação</h2>

                <Form noValidate
                    className='form-bg border border-dark border-top-0 p-3 p-md-5 rounded-bottom-4 shadow-sm'
                    onSubmit={handleSubmit(enviarParaBackend)}
                >
                    <Row className="mb-4 justify-content-center">
                        {/* xs=12 (100% no celular), md=8 (maior no tablet), lg=6 (metade na tela grande) */}
                        <Col xs={12} md={8} lg={6} style={{ position: 'relative' }}> {/* Importante: relative para o dropdown */}
                            <FormControl
                                value={buscaOrientador}
                                onChange={handleBuscaOrientador}
                                onFocus={handleSugestoesFocus}
                                onBlur={handleSugestoesBlur}
                                autoComplete="off"
                                placeholder="Digite ou selecione o nome do orientador"
                                className='bg-white text-black fw-bold fs-5'
                                isInvalid={!!errors.orientadorId}
                            />

                            {/* Lista de Sugestões */}
                            {sugestoes.length > 0 && (
                                <ul className="list-group position-absolute shadow-lg mt-1" style={{ zIndex: 1000, top: '100%', width: "95%" }}>
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
                    </Row>
                        {/* Exibe erro de validação se a lista estiver vazia */}
                    {errors.orientadorId && (
                        <Row className="justify-content-center mb-4">
                            <Col xs={12} md={8} lg={6}>
                                 <div className="text-danger fw-bold text-center">
                                    {errors.orientadorId?.message}
                                </div>
                            </Col>
                        </Row>
                    )}
                    <Row className="justify-content-center mt-2">
                        <Col xs={12} md={6} lg={4}>
                            <Button
                                variant="primary"
                                type="submit" id='btn-select'
                                className='mb-2 p-2 fs-5 fs-md-4 fw-medium w-100'
                                //Usa variavel do RHF para controlar comportamento do botão
                                disabled={!orientadorSelecionado}
                                style={{
                                    cursor: orientadorSelecionado ? 'pointer' : 'not-allowed',
                                    opacity: orientadorSelecionado ? 1 : 0.4
                                }}
                                title="Enviar Solicitação"
                            >
                                Enviar solicitação
                            </Button>
                        </Col>
                    </Row>
                </Form>
                {/* Renderiza o alerta de sucesso após passar nas validações */}
                {exibirSucesso && (
                    <Row className="justify-content-center mt-3">
                        <Col xs={12} md={8} lg={6}>
                            <Alert variant="success" onClose={() => setExibirSucesso(false)} dismissible className="mt-3" >
                                Solicitação de orientação enviada com sucesso!
                            </Alert>
                        </Col>
                    </Row>
                )}
            </Container>
        </>
    )
}

export default SolicitarOrientacao