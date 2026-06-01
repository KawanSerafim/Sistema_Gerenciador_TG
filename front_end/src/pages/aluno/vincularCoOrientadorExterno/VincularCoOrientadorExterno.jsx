import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button, Container, Form, FormGroup, Toast, ToastContainer } from "react-bootstrap";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";


import { grupoService } from "../../../services/grupotg/grupoService";
import { vincularCoorientadorSchema } from "../../../schemas/grupoTg/grupoTgZodSchema";
import UserNavBar from "../../../components/usernavbar/UserNavBar";
grupoService

const VincularCoorientadorExterno = () => {
    //Navegação com react-router-dom
    const navigate = useNavigate();
    //Estados
    const [resultado, setResultado] = useState({ exibir: false, variante: "", mensagem: "" });
    const [carregando, setCarregando] = useState(false);
    //RHF e Zod para validação do formulario
    const { register, handleSubmit, formState: { errors }, reset } = useForm({
        resolver: zodResolver(vincularCoorientadorSchema),
        defaultValues: { nome: "", origem: "" }
    });

    const enviarParaBackend = async (dadosValidados) => {
        setCarregando(true);
        setResultado({ exibir: false, variante: "", mensagem: "" });

        try {
            await grupoService.vincularCoorientadorExterno({
                nome: dadosValidados.nome,
                origem: dadosValidados.origem
            });

            setResultado({ exibir: true, variante: "success", mensagem: "Coorientador vinculado com sucesso!" });
            reset(); // Limpa o formulário após o sucesso

        } catch (err) {
            setResultado({
                exibir: true,
                variante: "danger",
                mensagem: err.message || "Erro ao vincular o coorientador. Verifique se o grupo já possui um orientador principal."
            });
        } finally {
            setCarregando(false);
        }
    };

    return (
        <>
            <UserNavBar
                userName="Aluno"
                maxWidth="800px"
            ></UserNavBar>
            <Container className="mt-5" style={{ maxWidth: '800px' }}>

                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>
                    Vincular Coorientador Externo
                </h2>

                <Form
                    noValidate
                    onSubmit={handleSubmit(enviarParaBackend)}
                    className='form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm'
                >
                    <p className="text-primary fs-5 text-center mb-4">
                        OBS: Apenas para aluno com grupo e orientador vinculado!
                    </p>
                    <p className="text-secondary fs-5 mb-4 text-center">
                        Preencha os dados do profissional externo que irá auxiliar no desenvolvimento do seu trabalho.
                    </p>

                    {/* Nome do Coorientador */}
                    <Form.Group className="mb-4" controlId="formNome">
                        <Form.Label className='text-secondary fs-4 fw-bold'>Nome Completo</Form.Label>
                        <Form.Control
                            type="text"
                            placeholder="Ex: Alan Turing"
                            {...register("nome")}
                            isInvalid={!!errors.nome}
                            className='bg-white text-black fw-normal fs-5'
                        />
                        <Form.Control.Feedback type="invalid">
                            {errors.nome?.message}
                        </Form.Control.Feedback>
                    </Form.Group>

                    {/* Origem (Empresa/Instituição) */}
                    <Form.Group className="mb-4" controlId="formOrigem">
                        <Form.Label className='text-secondary fs-4 fw-bold'>Origem (Empresa ou Instituição)</Form.Label>
                        <Form.Control
                            type="text"
                            placeholder="Ex: Microsoft, USP, Banco Itaú"
                            {...register("origem")}
                            isInvalid={!!errors.origem}
                            className='bg-white text-black fw-normal fs-5'
                        />
                        <Form.Control.Feedback type="invalid">
                            {errors.origem?.message}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <FormGroup className="text-center mt-5">
                        <Button
                            variant="primary"
                            type="submit"
                            disabled={carregando || resultado.exibir}
                            className='mb-2 fs-5 fw-medium w-100'
                        >
                            {carregando ? "Vinculando..." : "Vincular Coorientador"}
                        </Button>

                        <Button
                            variant="outline-secondary"
                            onClick={() => navigate('/inicio')}
                            className='mt-2 fs-5 fw-medium w-100'
                        >
                            Voltar
                        </Button>
                    </FormGroup>
                </Form>

                {/* Feedbacks */}
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
    );
};

export default VincularCoorientadorExterno;