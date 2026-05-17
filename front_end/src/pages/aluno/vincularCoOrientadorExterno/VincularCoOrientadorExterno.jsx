import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Alert, Button, Container, Form, FormGroup } from "react-bootstrap";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";


import { grupoService } from "../../../services/grupotg/grupoService";
import { vincularCoorientadorSchema } from "../../../schemas/grupoTg/grupoTgZodSchema";
grupoService

const VincularCoorientadorExterno = () => {
    const navigate = useNavigate();

    const [erro, setErro] = useState("");
    const [sucesso, setSucesso] = useState(false);
    const [loading, setLoading] = useState(false);

    const { register, handleSubmit, formState: { errors }, reset } = useForm({
        resolver: zodResolver(vincularCoorientadorSchema),
        defaultValues: { nome: "", origem: "" }
    });

    const onSubmit = async (dadosValidados) => {
        setLoading(true);
        setErro("");
        setSucesso(false);

        try {
            await grupoService.vincularCoorientadorExterno({
                nome: dadosValidados.nome,
                origem: dadosValidados.origem
            });

            setSucesso(true);
            reset(); // Limpa o formulário após o sucesso

        } catch (err) {
            setErro(err.message || "Erro ao vincular o coorientador. Verifique se o grupo já possui um orientador principal.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="mt-5" style={{ maxWidth: '800px' }}>
            <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>
                Vincular Coorientador Externo
            </h2>

            <Form
                noValidate
                onSubmit={handleSubmit(onSubmit)}
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
                        disabled={loading || sucesso}
                        className='mb-2 fs-5 fw-medium w-100'
                    >
                        {loading ? "Vinculando..." : "Vincular Coorientador"}
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
            {sucesso && (
                <Alert variant="success" className="mt-3 shadow-sm fw-bold text-center fs-5">
                    Coorientador vinculado com sucesso!
                </Alert>
            )}

            {erro && (
                <Alert variant="danger" onClose={() => setErro('')} dismissible className="mt-3 shadow-sm fw-bold text-center fs-5">
                    {erro}
                </Alert>
            )}
        </Container>
    );
};

export default VincularCoorientadorExterno;