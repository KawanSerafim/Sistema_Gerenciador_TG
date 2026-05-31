import { Toast, ToastContainer, Alert, Button, Col, Container, Form, Modal, Row, Stack, Spinner } from "react-bootstrap";
import UserNavBar from "../../../components/usernavbar/UserNavBar";
import TableComponent from "../../../components/table/TableComponent";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useModal } from "../../../hooks/useModal/useModal";
import { bloquearCaracteresInputNumber } from "../../../utils/utils";

import { camposSchema } from "../../../schemas/professor/visaoBancasArtigos/visaoBancasArtigosZodSchema"
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm, useFieldArray, useWatch } from "react-hook-form";

// Importa a service
import { bancaService } from "../../../services/banca/bancaService";
import { grupoService } from "../../../services/grupotg/grupoService";

const VisaoBancas = () => {
    // Estados da API
    const [dados, setDados] = useState([]);
    const [carregando, setCarregando] = useState(true);
    const [baixandoAtaId, setBaixandoAtaId] = useState(null);

    const [resultado, setResultado] = useState({
        exibir: false, mensagem: "", variante: ""
    });
    const [_temaSelecionado, setTemaSelecionado] = useState(null);
    const { show, selectedData, handleOpen, handleClose } = useModal(null);

    const {
        register,
        handleSubmit,
        setValue,
        reset,
        formState: { errors },
        control
    } = useForm({
        resolver: zodResolver(camposSchema),
        defaultValues: {
            idBanca: "",
            notas: []
        }
    });

    // Busca os dados da API ao carregar
    // Função memorizada para não causar re-renderizações em cadeia
    const carregarBancas = useCallback(async () => {
        try {
            setCarregando(true);
            // setExibirResultado({ exibir: false, mensagem: "", variante: "" });

            const response = await bancaService.listarBancas();
            setDados(response);
        } catch (error) {
            console.error(error);
            setResultado({
                exibir: true,
                variante: "danger",
                mensagem: "Não foi possível carregar as bancas. Tente novamente."
            });
        } finally {
            setCarregando(false);
        }
    }, []);

    useEffect(() => {
        carregarBancas();
    }, [carregarBancas]);

    // Formata a data ISO do backend para string amigável no BR
    const formatarData = (dataIso) => {
        if (!dataIso) return "Não definida";
        const dataObj = new Date(dataIso);
        return dataObj.toLocaleString('pt-BR', {
            day: '2-digit', month: '2-digit', year: 'numeric',
            hour: '2-digit', minute: '2-digit'
        });
    };

    // Função exclusiva para lidar com o cancelamento da banca
    const handleCancelarAvaliacao = useCallback(async (idBanca, tema) => {
        const confirmar = window.confirm(`Tem certeza que deseja cancelar a avaliação do tema: ${tema}?`);
        if (!confirmar) return;

        try {
            // Chama a service que bate no endpoint PATCH /bancas/{idBanca}/cancelar
            await bancaService.cancelarBanca(idBanca);

            setResultado({
                exibir: true,
                mensagem: `Avaliação do grupo "${tema}" foi cancelada com sucesso.`,
                variante: "success"
            });

            // Atualiza a tabela para refletir o novo status "Cancelada"
            carregarBancas();
        } catch (error) {
            console.error(error);
            setResultado({
                exibir: true,
                mensagem: "Erro ao tentar cancelar a avaliação. Tente novamente.",
                variante: "danger"
            });
        }
    }, [carregarBancas]);


    const handleBaixarTrabalho = async (idBanca) => {
        try {
            setResultado({ exibir: true, variante: "info", mensagem: "Iniciando download..." });

            const { blob, filename } = await grupoService.baixarTrabalhoBanca(idBanca);

            // Cria uma URL temporária na memória do navegador para o arquivo
            const url = window.URL.createObjectURL(blob);

            // Cria uma tag <a> invisível e clica nela automaticamente
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', filename);
            document.body.appendChild(link);
            link.click();

            // Limpeza da memória
            link.parentNode.removeChild(link);
            window.URL.revokeObjectURL(url);

            setResultado({ exibir: false, variante: "", mensagem: "" });

        } catch (error) {
            console.error(error);
            setResultado({
                exibir: true, variante: "danger", mensagem: "Não foi possível baixar o trabalho. Verifique se o aluno já realizou o envio."
            });
        }
    };


    const handleBaixarAta = async (idBanca, temaBanca) => {
        try {
            // Ativa o loading no botão clicado
            setBaixandoAtaId(idBanca);
            console.log(idBanca)
            const blob = await bancaService.baixarAtaBanca(idBanca);

            // Cria um link invisível na memória do navegador para forçar o download
            const url = window.URL.createObjectURL(new Blob([blob], { type: 'application/pdf' }));
            const link = document.createElement('a');
            link.href = url;

            // Define o nome do arquivo que vai ser salvo no PC do professor
            const nomeFormatado = temaBanca.replace(/\s+/g, '_');
            link.setAttribute('download', `Ata_Banca_${nomeFormatado}.pdf`);

            // Simula o clique e depois limpa a memória
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);

        } catch (error) {
            console.error("Erro ao baixar a ata da banca:", error);
            alert("Não foi possível gerar a Ata da Banca. Verifique com o suporte.");
        } finally {
            setBaixandoAtaId(null);
        }
    };

    const columns = useMemo(() => [
        { header: "Tema", accessor: "tema", filtravel: true, tipoFiltro: "text" },
        { header: "Tipo de TG", accessor: "tipoTg", filtravel: true, tipoFiltro: "select" },
        {
            header: "Grupo",
            accessor: "alunos",
            filtravel: true,
            tipoFiltro: "autocomplete",
            render: (row) => (
                <Button variant='primary'
                    size='lm'
                    className="px-2"
                    onClick={() => handleOpen({ type: "INTEGRANTES", row })}
                >
                    Visualizar Integrantes
                </Button>
            ),
        },
        {
            header: "Data",
            accessor: "dataHora",
            filtravel: true,
            tipoFiltro: "select",
            render: (row) => <span>{formatarData(row.dataHora)}</span>
        },
        {
            header: "Membros da Banca",
            accessor: "membros",
            filtravel: false,
            render: (row) => (
                <Button variant='primary'
                    size='lm'
                    className="px-2"
                    // Adapte caso o DTO traga os membros de fato. Se não trouxer, remova este botão
                    disabled={!row.membros || row.membros.length === 0}
                    onClick={() => handleOpen({ type: "MEMBROS", row })}
                >
                    Visualizar Membros
                </Button>
            ),

        },
        {
            header: "Situação",
            accessor: "situacao",
            filtravel: true,
            tipoFiltro: "select",
            render: (row) => {
                console.log(`situacao: ${row.situacao}`)
                // Trata o estado quando a data passou mas ainda não possui nota
                if (row.situacao === "Marcada") {
                    const prefixoBanca = row.disciplina?.includes("TG1") ? "Pré-banca" : "Banca";
                    return <span className="fw-medium text-primary">{prefixoBanca} marcada</span>;
                }

                if (row.situacao === "Realizada") {
                    const prefixoBanca = row.disciplina?.includes("TG1") ? "Pré-banca" : "Banca";
                    return <span className="fw-medium text-primary">{prefixoBanca} realizada</span>;
                }
                // Trata o estado de banca avaliada com sucesso
                if (row.situacao === "Avaliada") {
                    return <span className="fw-bold text-success">Avaliada</span>;
                }
                // Trata o estado de banca cancelada
                if (row.situacao === "Cancelada") {
                    return <span className="fw-bold text-danger">Cancelada</span>;
                }
                // Retorna o estado padrão vindo do banco de dados (Marcada)
                return <span>{row.situacao}</span>;
            }
        },
        {
            header: "Ações",
            render: (row) => {
                // A flag do back-end já nos diz se podemos habilitar o botão, se foi marcada e se não foi avaliada
                const podeSerAvaliada = row.podeAtribuirNota;

                const podeSerCancelada = row.situacao == "Marcada" ? true : false;

                return (
                    <Stack direction="horizontal" gap={3} className="justify-content-center flex-wrap">
                        <Button variant={podeSerAvaliada ? "success" : "light"}
                            size="lg"
                            disabled={!podeSerAvaliada}
                            className={podeSerAvaliada ? "fw-bold text-black" : "text-muted border"}
                            onClick={() => {
                                handleOpen({ type: "NOTA", row })
                                setTemaSelecionado(row.tema)

                                setValue("idBanca", row.idBanca);

                                const arrayNotasIniciais = (row.membros || []).map((membro) => ({
                                    idMembro: membro.id, // ID real para montar o map depois
                                    nomeMembroBanca: membro.nome,
                                    nota: 0
                                }));

                                setValue("notas", arrayNotasIniciais);
                            }}>
                            Atribuir Nota
                        </Button>
                        <Button
                            variant={podeSerCancelada ? "danger" : "light"}
                            size="lg"
                            disabled={!podeSerCancelada}
                            className={podeSerCancelada ? "text-black" : "text-muted border"}
                            onClick={() => handleCancelarAvaliacao(row.idBanca, row.tema)}>
                            Cancelar Avaliação
                        </Button>

                        <Button variant="outline-primary"
                            size="lg"
                            onClick={() => handleBaixarTrabalho(row.idBanca)}>
                            Baixar Trabalho
                        </Button>
                        <Button
                            variant="outline-danger"
                            size="lg"
                            onClick={() => handleBaixarAta(row.idBanca, row.tema)}
                            disabled={baixandoAtaId === row.idBanca}
                        >
                            {baixandoAtaId === row.idBanca ? (
                                <Spinner as="span" animation="border" size="sm" role="status" aria-hidden="true" />
                            ) : (
                                "Baixar Ata da Banca"
                            )}
                        </Button>
                    </Stack >
                )
            }
        }
    ], [handleOpen, handleCancelarAvaliacao, setValue, baixandoAtaId]);

    const { fields } = useFieldArray({
        control,
        name: "notas"
    })

    const notasEmTempoReal = useWatch({
        control,
        name: "notas",
        defaultValue: []
    });

    const calcularMedia = () => {
        if (notasEmTempoReal.length === 0) return 0;

        const soma = notasEmTempoReal.reduce(
            (acc, membro) => acc + Number(membro.nota || 0)
            , 0);
        const media = soma / notasEmTempoReal.length;
        return media.toFixed(1)
    }

    const renderModalContent = () => {
        if (!selectedData || !selectedData.type) return null;

        const { type, row: data } = selectedData;

        if (type === 'INTEGRANTES' || type === 'MEMBROS') {
            const listData = type === 'INTEGRANTES' ? (data.alunos || []) : (data.membros || []);
            const title = type === 'INTEGRANTES' ? 'Integrantes do Grupo' : 'Membros da banca';

            return (
                <>
                    <Modal.Header className="d-flex justify-content-center" closeButton>
                        <div className="custom-modal-title fs-5">
                            <h5>{title}</h5>
                        </div>
                    </Modal.Header>
                    <ul className="list-group list-group-flush text-center">
                        {listData.map((item, index) => (
                            <li key={index} className="list-group-item fs-6 fw-bold" style={{ backgroundColor: '#ffecd9' }}>
                                {/* Adaptado caso seja um objeto ou string */}
                                {item.nome || item}
                            </li>
                        ))}
                    </ul>
                </>
            );
        }
        if (type === 'NOTA') {
            return (
                <Form
                    noValidate
                    onSubmit={handleSubmit(enviarParaBackend)}>
                    <Modal.Header className="d-flex justify-content-center" closeButton>
                        <div className="custom-modal-title">
                            <span className="fw-bold fs-5">{`${data.tema} - ${data.tipoTg.toUpperCase().replace("_", " ")} - ${data.disciplina.toUpperCase()}`}</span>
                        </div>
                    </Modal.Header>

                    <Row className="m-0 text-center">
                        <Col xs={6} className="p-0 border-end border-dark">
                            <div className="fw-bold p-2 border-dark fs-5" style={{ backgroundColor: '#ffe5cc' }}>
                                Integrantes da Banca
                            </div>
                            <ul className="list-group list-group-flush fs-6">
                                {fields.
                                    map((field, idx) => (
                                        <div key={idx} className="p-4 d-flex flex-column align-items-center gap-2">
                                            <li className="list-group-item fw-bold" style={{ backgroundColor: '#ffecd9' }}>
                                                {field.nomeMembroBanca}
                                            </li>
                                            <Form.Control
                                                type="number"
                                                defaultValue={0}
                                                min={0}
                                                max={10}
                                                {...register(`notas.${idx}.nota`)}
                                                isInvalid={!!errors.notas?.[idx]?.nota}
                                                title="Digite a nota do integrante da banca"
                                                onKeyDown={bloquearCaracteresInputNumber}
                                                className="text-center w-50"
                                            />
                                            <Form.Control.Feedback type="invalid" className="fw-bold fs-6">
                                                {errors.notas?.[idx]?.message}
                                            </Form.Control.Feedback>
                                        </div>
                                    ))}
                            </ul>
                        </Col>
                        <Col xs={6} className="p-0 d-flex flex-column align-items-center justify-content-center" style={{ backgroundColor: '#ffecd9' }}>
                            <div className="fw-bold p-2 border-bottom border-dark w-100" style={{ backgroundColor: '#ffe5cc' }}>
                                Média final do grupo: {calcularMedia()}
                            </div>
                            <div className="p-4 d-flex flex-column align-items-center gap-3 w-100 h-100">
                                <Button variant="success"
                                    type="submit" className="fw-bold text-black px-4"
                                >
                                    Confirmar Nota
                                </Button>
                            </div>
                        </Col>
                    </Row>
                </Form>

            );
        }
    };

    const enviarParaBackend = async (dadosValidados) => {
        try {
            // Utilizamos o índice (idx) para buscar o idMembro diretamente da variável 'fields'
            // que mantém os dados originais injetados quando o modal foi aberto, driblando o Zod.
            const notasMap = dadosValidados.notas.reduce((acc, current, idx) => {
                const idCorreto = fields[idx].idMembro;
                acc[idCorreto] = Number(current.nota);
                return acc;
            }, {});

            const payload = {
                notasMembros: notasMap
            };

            await bancaService.atribuirNotasBanca(dadosValidados.idBanca, payload);

            setResultado({
                exibir: true,
                variante: "success",
                mensagem: `Notas da banca atribuídas com sucesso.`
            });

            // Atualiza a tabela buscando os dados novamente do banco
            carregarBancas();

        } catch (e) {
            console.error(e);
            setResultado({
                exibir: true,
                variante: "danger",
                mensagem: "Erro ao enviar notas. Verifique os dados e tente novamente."
            });
        } finally {
            handleClose();
            reset();
        }
    };

    return (
        <>
            <UserNavBar
                userName='Orientador'
                maxWidth="1500px"
            />

            <Container className="mt-5" style={{ maxWidth: '1500px' }}>
                <h2 className='text-black p-3 fs-1 rounded-top-4 text-center mb-5'>Visão das Bancas</h2>

                {carregando ? (
                    <div className="d-flex flex-column align-items-center justify-content-center py-5">
                        <Spinner animation="border" variant="primary" style={{ width: '4rem', height: '4rem' }} />
                        <h4 className="mt-3 text-secondary">Carregando bancas...</h4>
                    </div>
                ) : (
                    <TableComponent
                        colunas={columns}
                        dados={dados}
                    />
                )}

                <Modal
                    show={show}
                    onHide={handleClose}
                    centered
                    contentClassName="custom-modal-content"
                >
                    {renderModalContent()}
                </Modal>

                {resultado.exibir && (
                    <ToastContainer
                        position="top-end"
                        className="p-3"
                        style={{ position: "fixed", zIndex: 9999 }}
                    >
                        <Toast
                            show={resultado.exibir}
                            onClose={() => setResultado({ exibir: false, variante: "", mensagem: "" })}
                            bg={resultado.variante}
                        //desaparece somente quando o usuario clicar em fechar
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
                )}
            </Container>
        </>
    )
}
export default VisaoBancas;