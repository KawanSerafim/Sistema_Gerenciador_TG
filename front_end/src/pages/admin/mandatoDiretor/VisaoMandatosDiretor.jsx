import { useState, useEffect } from 'react';
import UserNavBar from "../../../components/usernavbar/UserNavBar";
import { Container, Form, Button, Row, Col, Spinner, ToastContainer, Toast, ListGroup, FormControl } from 'react-bootstrap';

//Services
import { diretorService } from '../../../services/mandatoDiretor/diretorService';
import { professorService } from '../../../services/professor/professorService';

//RHF e zod
import { diretorZodSchema } from '../../../schemas/diretor/diretorZodSchema';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm, useWatch } from 'react-hook-form';

const VisaoMandatosDiretor = () => {
    const [diretorAtual, setDiretorAtual] = useState(null);
    const [carregando, setCarregando] = useState(true);
    const [enviando, setEnviando] = useState(false);

    const [resultado, setResultado] = useState({ exibir: false, variante: "", mensagem: "" });

    const [professores, setProfessores] = useState([]);
    const [carregandoProfessores, setCarregandoProfessores] = useState(true);
    const [buscaProfessor, setBuscaProfessor] = useState("");
    const [sugestoes, setSugestoes] = useState([]);

    const {
        register,
        handleSubmit,
        setValue,
        control,
        reset,
        formState: { errors }
    } = useForm({
        resolver: zodResolver(diretorZodSchema),
        defaultValues: {
            matriculaProfessor: "",
            dataInicio: "",
            dataFim: "",
            assinaturaBase64: ""
        }
    });

    const professorSelecionado = useWatch({ control, name: "matriculaProfessor" });
    const assinaturaPreview = useWatch({ control, name: "assinaturaBase64" });

    useEffect(() => {
        const carregarDadosIniciais = async () => {
            try {
                setCarregando(true);
                setCarregandoProfessores(true);

                const dadosDiretor = await diretorService.buscarDiretorAtual();
                setDiretorAtual(dadosDiretor);

                const dadosProfessores = await professorService.buscaProfessoresPorCargo("ORIENTADOR");

                // Salva o ID e a Matrícula para garantir que o "find" vai funcionar
                const professoresFormatados = dadosProfessores.map(prof => ({
                    // Previne caso o backend retorne propriedades diferentes
                    id: prof.id || prof.idProfessor,
                    matricula: prof.matricula,
                    nome: prof.nome
                }));
                setProfessores(professoresFormatados);

            } catch (error) {
                console.error(error);
                setResultado({ exibir: true, variante: "danger", mensagem: "Erro ao carregar os dados iniciais do servidor." });
            } finally {
                setCarregando(false);
                setCarregandoProfessores(false);
            }
        };

        carregarDadosIniciais();
    }, []);

    const handleSugestoesFocus = () => {
        if (buscaProfessor.length === 0) {
            setSugestoes(professores.slice(0, 3));
        }
    };

    const handleSugestoesBlur = () => {
        setTimeout(() => setSugestoes([]), 200);
    };

    const handleBuscaProfessor = (e) => {
        const termo = e.target.value;
        setBuscaProfessor(termo);
        setValue("matriculaProfessor", "", { shouldValidate: true });

        if (termo.length > 1) {
            const filtrados = professores.filter(prof =>
                prof.nome.toLowerCase().includes(termo.toLowerCase())
            );
            setSugestoes(filtrados);
        } else {
            setSugestoes(professores.slice(0, 3));
        }
    };

    const selecionarSugestao = (professor) => {
        setBuscaProfessor(professor.nome);
        setValue("matriculaProfessor", String(professor.matricula), { shouldValidate: true });
        setSugestoes([]);
    };

    const handleUploadImagem = (event) => {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setValue("assinaturaBase64", reader.result, { shouldValidate: true });
            };
            reader.readAsDataURL(file);
        }
    };

    const enviarParaBackend = async (dadosValidados) => {
        try {
            setEnviando(true);
            setResultado({ exibir: false, variante: "", mensagem: "" });

            await diretorService.atribuirDiretor({
                matriculaProfessor: dadosValidados.matriculaProfessor,
                dataInicio: dadosValidados.dataInicio,
                dataFim: dadosValidados.dataFim || null,
                assinaturaBase64: dadosValidados.assinaturaBase64
            });

            setResultado({ exibir: true, variante: "success", mensagem: "Mandato atribuído com sucesso!" });

            const dadosDiretor = await diretorService.buscarDiretorAtual();
            setDiretorAtual(dadosDiretor);

            setBuscaProfessor("");
            reset();

        } catch (error) {
            setResultado({ exibir: true, variante: "danger", mensagem: error.message || "Erro ao atribuir o mandato." });
        } finally {
            setEnviando(false);
        }
    };

    const handleEncerrar = async () => {
        if (!window.confirm("Tem certeza que deseja encerrar o mandato do diretor atual?")) return;

        try {
            setCarregando(true);
            await diretorService.retirarDiretor();
            setResultado({ exibir: true, variante: "success", mensagem: "Mandato encerrado com sucesso." });
            setDiretorAtual(null);
        } catch (error) {
            setResultado({ exibir: true, variante: "danger", mensagem: error.message || "Erro ao encerrar mandato." });
        } finally {
            setCarregando(false);
        }
    };

    // HOTFIX: Busca o nome do diretor cruzando o ID/Matrícula da gestão atual com a lista de professores carregada.
    const obterNomeDiretor = () => {
        if (!diretorAtual) return "";
        const profEncontrado = professores.find(
            p => p.id === diretorAtual.professorId || p.matricula === diretorAtual.professorId
        );
        // Se encontrar, retorna o nome. Se não, fallback para exibir o ID como era antes.
        return profEncontrado ? profEncontrado.nome : diretorAtual.professorId;
    };

    return (
        <>
            <UserNavBar userName='Administrador' maxWidth="1200px" />
            <Container className="mt-5 text-center px-3 px-md-0" style={{ maxWidth: "1000px" }}>
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Gestão da Diretoria</h2>

                <div className='form-bg border border-dark border-top-0 p-3 p-md-5 rounded-bottom-4 shadow-sm text-start'>

                    {carregando ? (
                        <div className="text-center py-5">
                            <Spinner animation="border" variant="primary" style={{ width: '4rem', height: '4rem' }} />
                            <h4 className="mt-3 text-secondary">Carregando dados...</h4>
                        </div>
                    ) : diretorAtual ? (
                        <div className="text-center px-lg-5">
                            <h4 className="text-secondary mb-4">Existe um diretor com mandato vigente na instituição.</h4>
                            <div className="bg-white p-4 rounded-3 shadow-sm border mb-4 text-start">
                                <h5 className="fw-bold text-primary mb-3">Dados da Gestão Atual</h5>

                                {/* AQUI ESTÁ A MUDANÇA: Exibindo o Nome formatado */}
                                <p className="mb-2 fs-5"><strong>Diretor(a):</strong> Prof(a). {obterNomeDiretor()}</p>

                                <p className="mb-2 fs-5"><strong>Início do Mandato:</strong> {new Date(diretorAtual.dataInicio).toLocaleDateString('pt-BR')}</p>

                                <div className="mt-4">
                                    <p className="fw-bold mb-2">Assinatura Oficial:</p>
                                    <div className="p-3 border rounded bg-light text-center">
                                        <img
                                            src={diretorAtual.assinaturaBase64}
                                            alt="Assinatura Diretor"
                                            style={{ maxHeight: '120px' }}
                                        />
                                    </div>
                                </div>
                            </div>
                            <Button
                                variant="danger"
                                size="lg"
                                className="fw-bold px-5"
                                onClick={handleEncerrar}
                                disabled={carregando}
                            >
                                Encerrar Gestão Atual
                            </Button>
                        </div>
                    ) : (
                        <Form noValidate onSubmit={handleSubmit(enviarParaBackend)}>
                            <p className="text-secondary fs-5 mb-4 text-center">
                                Não há diretores vigentes. Pesquise um professor e atribua um novo mandato para habilitar a emissão de documentos oficiais.
                            </p>

                            <Row className="mb-4 justify-content-center">
                                <Col xs={12} lg={10} style={{ position: 'relative' }}>
                                    <Form.Label className="fw-bold">Selecionar Professor</Form.Label>
                                    <FormControl
                                        value={buscaProfessor}
                                        onChange={handleBuscaProfessor}
                                        onFocus={handleSugestoesFocus}
                                        onBlur={handleSugestoesBlur}
                                        autoComplete="off"
                                        placeholder={carregandoProfessores ? "Carregando professores..." : "Digite o nome do professor"}
                                        className='bg-white text-black fw-bold fs-5'
                                        isInvalid={!!errors.matriculaProfessor}
                                        disabled={carregandoProfessores || enviando}
                                    />

                                    {sugestoes.length > 0 && (
                                        <ul className="list-group position-absolute shadow-lg mt-1" style={{ zIndex: 1000, top: '100%', width: "95%" }}>
                                            {sugestoes.map(prof => (
                                                <ListGroup.Item
                                                    key={prof.matricula}
                                                    action
                                                    className="list-group-item list-group-item-action cursor-pointer py-2 fs-6 fw-medium"
                                                    onClick={() => selecionarSugestao(prof)}
                                                >
                                                    {prof.nome}
                                                </ListGroup.Item>
                                            ))}
                                        </ul>
                                    )}
                                    <Form.Control.Feedback type="invalid" className="fw-bold fs-6">
                                        {errors.matriculaProfessor?.message}
                                    </Form.Control.Feedback>
                                </Col>
                            </Row>

                            <Row className="mb-4 justify-content-center">
                                <Col xs={12} lg={5}>
                                    <Form.Group>
                                        <Form.Label className="fw-bold">Data de Início</Form.Label>
                                        <Form.Control
                                            type="date"
                                            className="fs-5"
                                            {...register("dataInicio")}
                                            isInvalid={!!errors.dataInicio}
                                            disabled={enviando}
                                        />
                                        <Form.Control.Feedback type="invalid" className="fw-bold">
                                            {errors.dataInicio?.message}
                                        </Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                                <Col xs={12} lg={5} className="mt-3 mt-lg-0">
                                    <Form.Group>
                                        <Form.Label className="fw-bold">Data de Fim <span className="text-muted fw-normal">(Opcional)</span></Form.Label>
                                        <Form.Control
                                            type="date"
                                            className="fs-5"
                                            {...register("dataFim")}
                                            disabled={enviando}
                                        />
                                    </Form.Group>
                                </Col>
                            </Row>

                            <Row className="mb-5 justify-content-center">
                                <Col xs={12} lg={10}>
                                    <Form.Group>
                                        <Form.Label className="fw-bold">Upload da Assinatura</Form.Label>
                                        <Form.Control
                                            type="file"
                                            accept="image/*"
                                            className="fs-5"
                                            onChange={handleUploadImagem}
                                            isInvalid={!!errors.assinaturaBase64}
                                            disabled={enviando}
                                        />
                                        <Form.Text className="text-muted">
                                            Envie uma imagem limpa (PNG ou JPG) com a assinatura que sairá nos certificados.
                                        </Form.Text>
                                        <Form.Control.Feedback type="invalid" className="fw-bold">
                                            {errors.assinaturaBase64?.message}
                                        </Form.Control.Feedback>
                                    </Form.Group>

                                    {assinaturaPreview && (
                                        <div className="mt-4 p-3 bg-white border rounded text-center shadow-sm">
                                            <span className="text-muted d-block mb-2 fw-bold">Pré-visualização da Assinatura:</span>
                                            <img src={assinaturaPreview} alt="Preview" style={{ maxHeight: '80px' }} />
                                        </div>
                                    )}
                                </Col>
                            </Row>

                            <Row className="justify-content-center">
                                <Col xs={12} lg={6}>
                                    <Button
                                        variant="success"
                                        type="submit"
                                        className='p-2 fs-5 fw-bold w-100'
                                        disabled={!professorSelecionado || enviando || carregandoProfessores}
                                    >
                                        {enviando ? (
                                            <>
                                                <Spinner as="span" animation="border" size="sm" role="status" aria-hidden="true" className="me-2" />
                                                Processando...
                                            </>
                                        ) : "Confirmar Nomeação"}
                                    </Button>
                                </Col>
                            </Row>
                        </Form>
                    )}
                </div>

                <ToastContainer position="top-end" className="p-3" style={{ position: "fixed", zIndex: 9999 }}>
                    <Toast
                        show={resultado.exibir}
                        onClose={() => setResultado({ exibir: false, variante: "", mensagem: "" })}
                        bg={resultado.variante}
                        delay={5000}
                        autohide={resultado.variante === "success"}
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
            </Container>
        </>
    );
};

export default VisaoMandatosDiretor;