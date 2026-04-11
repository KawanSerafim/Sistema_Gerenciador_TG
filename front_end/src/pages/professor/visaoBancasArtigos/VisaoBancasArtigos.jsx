import { Alert, Button, Col, Container, Form, Modal, Row, Stack } from "react-bootstrap";
import UserNavBar from "../../../components/usernavbar/UserNavBar";
import TableComponent from "../../../components/table/TableComponent";
import { useMemo, useState } from "react";
import { useModal } from "../../../hooks/useModal/useModal";
import { bloquearCaracteresInputNumber } from "../../../utils/utils";

import { camposSchema } from "../../../schemas/professor/visaoBancasArtigos/visaoBancasArtigosZodSchema"
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm, useFieldArray, useWatch } from "react-hook-form";

const VisaoBancasArtigos = () => {

    const [exibirResultado, setExibirResultado] = useState({
        show: false, message: "", variant: ""
    })
    const [temaSelecionado, setTemaSelecionado] = useState(null)

    const { show, selectedData, handleOpen, handleClose } = useModal(null)

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
            idGrupo: "",
            notas: [{
                nomeMembroBanca: "",
                nota: 0
            }]
        }
    })

    //Mocks temporarios
    const columns = [
        { header: "Tema", accessor: "tema", filtravel: true, tipoFiltro: "text" },
        { header: "Tipo de TG", accessor: "tipoTG", filtravel: true, tipoFiltro: "select" },
        {
            header: "Grupo",
            accessor: "grupo",
            filtravel: true,
            tipoFiltro: "autocomplete",
            // Render customizado para o botão vermelho
            render: (row) => (
                <Button variant='primary'
                    size='lm'
                    className="px-2"
                    //Passa um objeto com o tipo do modal que será renderizado ao abrir
                    onClick={() => handleOpen({ type: "INTEGRANTES", row })}
                >
                    Visualizar Integrantes
                </Button>
            ),

        },
        {
            header: "Data",
            accessor: "data",
            filtravel: true,
            tipoFiltro: "select"
        },
        {
            header: "Membros da Banca",
            accessor: "membros",
            filtravel: true,
            tipoFiltro: "autocomplete",
            // Render customizado para o botão vermelho
            render: (row) => (
                <Button variant='primary'
                    size='lm'
                    className="px-2"
                    disabled={!row.membros || row.membros.length === 0}
                    //Passa um objeto com o tipo do modal que será aberto
                    onClick={() =>
                        handleOpen({ type: "MEMBROS", row })
                    }
                >
                    Visualizar Membros
                </Button>
            ),

        },
        {
            header: "Situação",
            accessor: "situacao",
            filtravel: true,
            tipoFiltro: "select"
        },
        {
            header: "Ações",
            // Render customizado para os botões de Aceitar/Recusar
            render: (row) => {
                const isBancaRealizada = row.situacao === "Pré Banca realizada" || row.situacao === "Banca realizada";
                const isArtigo = row.tipoTg === "Artigo";

                return (
                    <Stack direction="horizontal" gap={5} className="justify-content-center">
                        <Button variant={isBancaRealizada ? "success" : "light"}
                            size="lm"
                            disabled={!isBancaRealizada}
                            className={isBancaRealizada ? "fw-bold text-black" : "text-muted border"}
                            //Passa um objeto com o tipo do modal que será aberto
                            onClick={() => {
                                handleOpen({ type: "NOTA", row })
                                setTemaSelecionado(row.tema)
                                //Injeta id grupo e zera a nota no RHF
                                setValue("idGrupo", String(row.id));
                                const arrayNotasIniciais = row.membros.map((nomeMembro) => ({
                                    nomeMembroBanca: nomeMembro,
                                    //Inicia todas as notas em 0
                                    nota: 0
                                }));

                                setValue("notas", arrayNotasIniciais);
                            }}>
                            Atribuir Nota
                        </Button>
                        <Button
                            variant={isArtigo ? "light" : "danger"}
                            size="lm"
                            disabled={isArtigo}
                            className={isArtigo ? "text-muted border" : "text-black"}
                            onClick={() => {
                                setTemaSelecionado(row.tema)
                                setExibirResultado({ show: true, message: `Avaliação do grupo de tema: ${temaSelecionado} foi cancelada`, variant: "danger" })
                            }}>
                            Cancelar Avaliação
                        </Button>
                    </Stack>
                )
            }
        }
    ]

    const data = useMemo(() => [
        {
            id: 1,
            tema: "Ética no desenvolvimento de IA",
            disciplina: "TG2",
            tipoTG: "Artigo",
            grupo: ["Joe", "Miranda", "Nat"],
            data: "[Data de publicação]",
            membros: ["Professor 1", "Professor 2"],
            situacao: "Artigo a ser publicado"
        },
        {
            id: 2,
            tema: "A importância da Cibersegurança",
            disciplina: "TG1",
            tipoTG: "Monografia",
            grupo: ["Ana Maria", "Ashlhey", "James"],
            data: "25/07/2026 18:30",
            membros: ["Membro 1 - Professor", "Membro 2 - Membro externo", "Membro 3 - Professor"],
            situacao: "Pré Banca realizada"

        },
        {
            id: 3,
            tema: "Os perigos do Vibe Coding",
            disciplina: "TG2",
            tipoTG: "Desenvolvimento de Software",
            grupo: ["Mariana Silva", "Samuel", "Geraldo"],
            data: "26/07/2026 19:00",
            membros: ["Professor Orientador", "Professor Convidado"],
            situacao: "Banca marcada"
        }
    ], [])

    //Lida com as notas dos membros da banca
    const { fields } = useFieldArray({
        control,
        name: "notas"
    })

    //Observa mudanças no array de notas, para o calculo da média
    const notasEmTempoReal = useWatch({
        control,
        name: "notas",
        defaultValue: []
    });

    //Calcula média das notas
    const calcularMedia = () => {
        if (notasEmTempoReal.length === 0) return 0;

        const soma = notasEmTempoReal.reduce(
            (acc, membro) => acc + Number(membro.nota || 0)
            , 0);
        const media = soma / notasEmTempoReal.length;

        //Caso seja decimal returna apenas 1 casa pós virgula
        return media.toFixed(1)
    }

    // Função auxiliar para renderizar o conteúdo interno do Modal correto
    const renderModalContent = () => {
        if (!selectedData || !selectedData.type) return null;

        // Desestruturamos o objeto que enviamos no handleOpen
        const { type, row: data } = selectedData;

        if (type === 'INTEGRANTES' || type === 'MEMBROS') {
            const listData = type === 'INTEGRANTES' ? data.grupo : data.membros;
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
                                {item}
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
                            <span className="fw-bold fs-5">{`${data.tema} - ${data.disciplina.toUpperCase()}`}</span>
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
                                            {/* Nome integrante */}
                                            <li className="list-group-item fw-bold" style={{ backgroundColor: '#ffecd9' }}>
                                                {field.nomeMembroBanca}
                                            </li>
                                            {/* Nota do integrante */}
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
            // Usa formData para enviar para o backend, o grupo e a nota deles
            const formData = new FormData();
            formData.append("idGrupo", dadosValidados.idGrupo);
            formData.append("notas", dadosValidados.notas); // Pega o arquivo real

            console.log("Enviando para o backend...");

            // Simula o delay da rede e a resposta do backend lendo o CSV/XLSX
            setTimeout(() => {
                const respostaBackend = "200";
                if (respostaBackend.includes("200")) {
                    setExibirResultado({ show: true, variant: "success", message: `Nota do grupo ${dadosValidados.idGrupo} enviada com sucesso` });
                } else {
                    setExibirResultado({ show: true, variant: "danger", message: `Erro ao enviar nota: ${dadosValidados.nota} do grupo ${dadosValidados.idGrupo}.` });
                }
                reset(); // Limpa o formulário
            }, 1500);

        } catch (e) {
            console.log(e)
            setExibirResultado({ show: true, variant: "danger", message: "Erro ao enviar nota, tente novamente" });
        } finally {
            handleClose();
            reset();
        }
    };
    return (
        <>
            <UserNavBar
                /*Deve verificar qual o nome do usuario logado para ser passado ao componente*/
                userName='Orientador'
                maxWidth="1500px"
            />

            <Container className="mt-5" style={{ maxWidth: '1500px' }}>
                <h2 className='text-black p-3 fs-1 rounded-top-4 text-center mb-5'>Visão das Bancas</h2>
                <TableComponent
                    colunas={columns}
                    dados={data}
                />
                <Modal
                    show={show}
                    onHide={handleClose}
                    centered
                    contentClassName="custom-modal-content"
                >
                    {renderModalContent()}
                </Modal>
                {/* Renderiza o alerta de sucesso após passar nas validações */}
                {exibirResultado.show && (
                    <Alert variant={exibirResultado.variant} onClose={() => setExibirResultado({ ...exibirResultado, show: false })} dismissible className="mt-3" >
                        {exibirResultado.message}
                    </Alert>
                )}
            </Container>
        </>
    )
}
export default VisaoBancasArtigos