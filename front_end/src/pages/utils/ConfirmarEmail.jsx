import { Alert, Container } from 'react-bootstrap';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import { FormControl } from 'react-bootstrap';
import UserNavBar from '../../components/usernavbar/UserNavBar';

import { useState } from "react";
import { useLocation, useNavigate } from 'react-router-dom';

//Zod e RHF para validação
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { camposSchema } from '../../schemas/utils/confirmarEmail/confirmarEmailZodSchema';

import { autenticacaoService } from '../../services/usuario/autenticacaoService';

const ConfirmarEmail = () => {

    const navigate = useNavigate();
    const location = useLocation();

    const emailUsuario = location.state?.emailCapturado || "";


    // ======== Estados ========

    const [resultado, setResultado] = useState({ exibir: false, variante: "", mensagem: "" });


    const [desabilitarReenvio, setDesabilitarReenvio] = useState(false);
    const [mensagemReenvio, setMensagemReenvio] = useState("");


    const {
        register,
        handleSubmit,
        formState: { errors }
    } = useForm({
        resolver: zodResolver(camposSchema),
        defaultValues: {
            codigo: ""
        }
    });

    //Função que aciona o POST do service
    const enviarParaBackend = async (dadosValidados) => {
        //Valida email
        if (!emailUsuario) {
            setResultado({
                exibir: true,
                variante: "warning",
                mensagem: "E-mail não identificado. Por favor, volte e faça login novamente."
            });
            return;
        }

        try {
            // Chama a rota de validação de codigo
            await autenticacaoService.validarCodigo(
                emailUsuario,
                dadosValidados.codigo
            );

            setResultado({
                exibir: true,
                variante: "success",
                mensagem: "E-mail confirmado com sucesso! Redirecionando para o login..."
            });

            // Aguarda 3 segundos para o usuário ler a mensagem e manda para o login
            setTimeout(() => navigate("/"), 3000);

        } catch (erro) {
            setResultado({
                exibir: true,
                variante: "danger",
                mensagem: erro.message || "Código inválido ou expirado. Tente novamente."
            });
        }
    };

    // Função para o botão de Reenviar
    const lidarComReenvio = async () => {
        if (!emailUsuario) return;

        try {
            setDesabilitarReenvio(true);
            setMensagemReenvio("Enviando um novo código...");

            // Chama a rota /api/autenticacao/reenviar-codigo
            await autenticacaoService.reenviarCodigo(emailUsuario);

            setMensagemReenvio("Novo código enviado! Verifique sua caixa de entrada.");

            // Libera o botão após 60 segundos
            setTimeout(() => {
                setDesabilitarReenvio(false);
                setMensagemReenvio("");
            }, 60000);

        } catch (erro) {
            setMensagemReenvio("Erro ao reenviar: " + erro.message);
            setDesabilitarReenvio(false);
        }
    };


    return (
        <>
            <UserNavBar
                opcoes={["sair"]}
                maxWidth="60rem"
            />
            <Container className="mt-5" style={{ maxWidth: "60rem" }}>
                <h1 className="text-black text-center fw-bold fs-1 mb-5">Confirmar Email</h1>
                <div className="bg-primary text-white border-1 rounded-3 p-3 my-5 text-center">
                    <p className='mt-2 fs-4 fw-bold'>
                        Enviamos um código para <br />
                        <span className="text-warning">{emailUsuario || "seu e-mail"}</span>
                    </p>
                </div>
                <Form
                    noValidate
                    className='form-bg p-4 rounded-4 shadow-sm px-5 d-flex flex-column align-items-center flex-nowrap'
                    onSubmit={handleSubmit(enviarParaBackend)}
                >

                    {/* Codigo */}

                    <FormControl
                        type="text"
                        placeholder="Digite o código enviado em seu email"
                        {...register("codigo")}
                        isInvalid={!!errors.codigo}
                        className='text-black fw-bold fs-4 w-75 mb-4 text-center'
                        maxLength={6}
                    />
                    {/* Feedback de erro */}
                    <Form.Control.Feedback type="invalid" className="text-danger fw-bold text-center mb-4">
                        {errors.codigo?.message}
                    </Form.Control.Feedback>

                    <Button variant="primary" type="submit" size="lg" className='fw-bold fs-4 text-white py-3 rounded-3'>
                        Confirmar Email
                    </Button>
                    {/* Secção de Reenvio de Código */}
                    <div className="mt-4 text-center w-100 border-top pt-3">
                        <Button
                            variant="link"
                            className="text-decoration-none fw-bold fs-5"
                            onClick={lidarComReenvio}
                            disabled={desabilitarReenvio || !emailUsuario}
                        >
                            {desabilitarReenvio ? "Aguarde 1 minuto para reenviar" : "Não recebi o código (Reenviar)"}
                        </Button>
                        {mensagemReenvio && (
                            <p className={`mt-2 fw-medium ${mensagemReenvio.includes("Erro") ? "text-danger" : "text-primary"}`}>
                                {mensagemReenvio}
                            </p>
                        )}
                    </div>
                </Form>

                {/* Renderiza o alerta de sucesso após passar nas validações */}
                {resultado.exibir && (
                    <Alert
                        variant={resultado.variante}
                        onClose={() => setResultado({ exibir: false, variante: "", mensagem: "" })}
                        dismissible
                        className="mt-4 shadow-sm fw-bold text-center fs-5" >
                        {resultado.mensagem}
                    </Alert>
                )}
            </Container>

        </>
    )

}
export default ConfirmarEmail;