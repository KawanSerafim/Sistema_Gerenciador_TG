
import { Container, FormControl, ListGroup, Col, Row, Alert, Spinner } from 'react-bootstrap';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import UserNavBar from '../../../components/usernavbar/UserNavBar';
import { useState, useEffect } from 'react';

// Zod e RHF
import { solicitarOrientacaoZodSchema } from '../../../schemas/aluno/solicitarOrientacao/solicitarOrientacaoZodSchema';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm, useWatch } from 'react-hook-form';

//Services
import { professorService } from "../../../services/professor/professorService"
import { alunoService } from '../../../services/aluno/alunoService';

const SolicitarOrientacao = () => {

    const {
        control,
        setValue,
        formState: { errors },
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

    /* === Estados da API e UI === */
    const [professores, setProfessores] = useState([]);
    const [carregandoProfessores, setCarregandoProfessores] = useState(true);
    const [enviando, setEnviando] = useState(false);

    // Novo estado unificado para feedbacks (Substitui o erroAPI e o antigo exibirSucesso)
    const [exibirSucesso, setExibirSucesso] = useState({ exibir: false, variante: "", mensagem: "" });

    /* === Estados do Input Auto-complete === */
    const [buscaOrientador, setBuscaOrientador] = useState("");
    const [sugestoes, setSugestoes] = useState([]);

    useEffect(() => {
        const carregarOrientadores = async () => {
            try {
                setCarregandoProfessores(true);
                const dados = await professorService.buscaProfessoresPorCargo("ORIENTADOR");

                const professoresFormatados = dados.map(prof => ({
                    id: prof.idProfessor || prof.id,
                    nome: prof.nome
                }));

                setProfessores(professoresFormatados);
            } catch (error) {
                console.error("Erro ao buscar orientadores:", error);
                // Usando o novo estado para mostrar erro de carregamento
                setExibirSucesso({
                    exibir: true,
                    variante: "danger",
                    mensagem: "Não foi possível carregar a lista de orientadores disponíveis."
                });
            } finally {
                setCarregandoProfessores(false);
            }
        };

        carregarOrientadores();
    }, []);

    const handleSugestoesFocus = () => {
        if (buscaOrientador.length === 0) {
            setSugestoes(professores.slice(0, 3));
        }
    }

    const handleSugestoesBlur = () => {
        setTimeout(() => {
            setSugestoes([]);
        }, 200);
    }

    const handleBuscaOrientador = (e) => {
        const termo = e.target.value;
        setBuscaOrientador(termo);
        setValue("orientadorId", "", { shouldValidate: true });

        if (termo.length > 1) {
            const filtrados = professores.filter(orientador =>
                orientador.nome.toLowerCase().includes(termo.toLowerCase())
            );
            setSugestoes(filtrados);
        } else {
            setSugestoes(professores.slice(0, 3));
        }
    };

    const selecionarSugestao = (orientador) => {
        setBuscaOrientador(orientador.nome);
        setValue("orientadorId", String(orientador.id), { shouldValidate: true });
        setSugestoes([]);
    };

    const enviarParaBackend = async (dadosValidados) => {
        try {
            setEnviando(true);
            // Reseta o alerta antes de tentar de novo
            setExibirSucesso({ exibir: false, variante: "", mensagem: "" });

            const payload = {
                idProfessor: dadosValidados.orientadorId
            };

            await alunoService.solicitarOrientacao(payload);

            // Fluxo de Sucesso usando o novo objeto
            setExibirSucesso({
                exibir: true,
                variante: "success",
                mensagem: "Solicitação de orientação enviada com sucesso!"
            });

            setBuscaOrientador("");
            reset();

            // Esconde automaticamente após 5 segundos
            setTimeout(() => setExibirSucesso({ exibir: false, variante: "", mensagem: "" }), 5000);

        } catch (error) {
            console.error("Erro ao solicitar orientação:", error);
            // Fluxo de Erro usando o mesmo objeto, mudando apenas a variante
            setExibirSucesso({
                exibir: true,
                variante: "danger",
                mensagem: error.message || "Ocorreu um erro ao enviar a solicitação. Verifique se você já possui um orientador."
            });
        } finally {
            setEnviando(false);
        }
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
                    <p className="text-secondary fs-5 mb-4">
                        Pesquise e selecione o professor que você deseja como orientador do seu grupo.
                    </p>

                    <Row className="mb-4 justify-content-center">
                        {/* xs=12 (100% no celular), md=8 (maior no tablet), lg=6 (metade na tela grande) */}
                        <Col xs={12} md={8} lg={6} style={{ position: 'relative' }}> {/* Importante: relative para o dropdown */}
                            <FormControl
                                value={buscaOrientador}
                                onChange={handleBuscaOrientador}
                                onFocus={handleSugestoesFocus}
                                onBlur={handleSugestoesBlur}
                                autoComplete="off"
                                placeholder={carregandoProfessores ? "Carregando professores..." : "Digite ou selecione o nome do orientador"}
                                className='bg-white text-black fw-bold fs-5'
                                isInvalid={!!errors.orientadorId}
                                disabled={carregandoProfessores || enviando}
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
                                disabled={!orientadorSelecionado || enviando || carregandoProfessores}
                                style={{
                                    cursor: orientadorSelecionado && !enviando ? 'pointer' : 'not-allowed',
                                    opacity: orientadorSelecionado && !enviando ? 1 : 0.6
                                }}
                                title="Enviar Solicitação"
                            >
                                {enviando ? (
                                    <>
                                        <Spinner as="span" animation="border" size="sm" role="status" aria-hidden="true" />
                                        Enviando...
                                    </>
                                ) : "Enviar solicitação"}
                            </Button>
                        </Col>
                    </Row>
                </Form>

                {/* Renderiza o alerta dinamico de erro ou sucesso após passar nas validações */}
                {exibirSucesso.exibir && (
                    <Row className="justify-content-center mt-3">
                        <Col xs={12} md={8} lg={6}>
                            <Alert
                                variant={exibirSucesso.variante}
                                onClose={() => setExibirSucesso({ exibir: false, variante: "", mensagem: "" })}
                                dismissible
                                className="fw-bold"
                            >
                                {exibirSucesso.mensagem}
                            </Alert>
                        </Col>
                    </Row>
                )}
            </Container>
        </>
    )
}

export default SolicitarOrientacao