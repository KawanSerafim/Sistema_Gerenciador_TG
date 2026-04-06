import { Container } from 'react-bootstrap';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import { FormGroup } from "react-bootstrap";

import { useState } from 'react';
import { bloquearCaracteresInputNome } from '../../utils/utils';

import UserNavBar from '../../components/usernavbar/UserNavBar';

// 1. Importações do RHF e Zod
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { professorSchema } from "../../schemas/utils/usuarios/usuariosZodSchema"

const CadastrarProfessor = () => {
    const [exibirSucesso, setExibirSucesso] = useState(false);

    // 3. Configuração do Hook
    const {
        register,
        handleSubmit,
        reset,
        formState: { errors }
    } = useForm({
        resolver: zodResolver(professorSchema),
        defaultValues: {
            nome: "", matricula: "", email: "", senha: "", confirmarSenha: "", cargo: ""
        }
    });

    // 4. Função de envio (Só roda se o formulário estiver 100% válido)
    const enviarParaBackend = (dadosValidados) => {
        console.log("Enviando payload para a API Java:", dadosValidados);

        setExibirSucesso(true);
        reset(); // Limpa o formulário após o sucesso

        // Esconde o alerta após 5 segundos
        setTimeout(() => setExibirSucesso(false), 5000);
    };

    return (
        <>
            <UserNavBar
                userName="Administrador"
                maxWidth="800px"
            ></UserNavBar>
            <Container className="mt-5" style={{ maxWidth: '800px' }}>

                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Cadastrar Professor</h2>
                <Form
                    noValidate
                    className='form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm'
                    onSubmit={handleSubmit(enviarParaBackend)}
                >
                    {/* Nome */}
                    <Form.Group className="mb-3" controlId="formBasicName">
                        <Form.Label className='text-secondary fs-4 fw-medium'>Nome Completo</Form.Label>
                        <Form.Control
                            type="text"
                            placeholder="Digite o nome completo do professor"
                            name="nome"
                            {...register("nome")}
                            onKeyDown={bloquearCaracteresInputNome}
                            isInvalid={!!errors.nome}
                            className='bg-white text-black fw-normal fs-5'

                        />
                        <Form.Control.Feedback type="invalid">
                            {errors.nome?.message}
                        </Form.Control.Feedback>
                    </Form.Group>

                    {/* Matrícula */}
                    <FormGroup className="mb-3" controlId="formBasicMatricula">
                        <Form.Label className='text-secondary fs-4 fw-medium'>Matrícula</Form.Label>
                        <Form.Control
                            type="text" placeholder="Digite a matrícula do professor"
                            name="matricula"
                            {...register("matricula")}
                            isInvalid={!!errors.matricula}
                            className='bg-white text-black fw-normal fs-5' />
                        <Form.Control.Feedback type="invalid">
                            {errors.matricula?.message}
                        </Form.Control.Feedback>
                    </FormGroup>

                    {/* Email */}
                    <Form.Group className="mb-3" controlId="formBasicEmail">
                        <Form.Label className='text-secondary fs-4 fw-medium'>Email</Form.Label>
                        <Form.Control
                            type="email"
                            placeholder="Digite o email do professor"
                            name="email"
                            {...register("email")}
                            isInvalid={!!errors.email}
                            className='bg-white text-black fw-normal fs-5' />

                        <Form.Control.Feedback type="invalid">
                            {errors.email?.message}
                        </Form.Control.Feedback>
                    </Form.Group>

                    {/* Senha */}
                    <Form.Group className="mb-3" controlId="formBasicPassword">
                        <Form.Label className='text-secondary fs-4 fw-medium'>Senha</Form.Label>
                        <Form.Control type="password" placeholder="Digite a senha do professor"
                            name="senha"
                            {...register("senha")}
                            isInvalid={!!errors.senha} className='bg-white text-black fw-normal fs-5' />

                        <Form.Control.Feedback type="invalid">
                            {errors.senha?.message}
                        </Form.Control.Feedback>
                    </Form.Group>

                    {/* Confirmar Senha */}
                    <FormGroup className="mb-3" controlId="formBasicConfirmPassword">
                        <Form.Label className='text-secondary fs-4 fw-medium'>Confirmar Senha</Form.Label>
                        <Form.Control type="password" placeholder="Confirme a senha digitada"
                            name="confirmarSenha"
                            {...register("confirmarSenha")}
                            isInvalid={!!errors.confirmarSenha}
                            className='bg-white text-black fw-normal fs-5' />
                        <Form.Control.Feedback type="invalid">
                            {errors.confirmarSenha?.message}
                        </Form.Control.Feedback>
                    </FormGroup>

                    {/* Selecionar Cargo */}
                    <FormGroup className="mb-3" controlId="formBasicRole">
                        <Form.Label className='text-secondary fs-4 fw-medium'>Cargo</Form.Label>
                        <Form.Select
                            name="cargo"
                            {...register("cargo")}
                            isInvalid={!!errors.cargo}
                            className='bg-white text-black fw-normal fs-5'>
                            <option value="" disabled selected>Selecione qual será o cargo do professor</option>
                            <option value="professor">Professor de TG</option>
                            <option value="coordenador">Coordenador</option>
                            <option value="orientador">Orientador</option>
                        </Form.Select>
                        <Form.Control.Feedback type="invalid">
                            {errors.cargo?.message}
                        </Form.Control.Feedback>
                    </FormGroup>

                    {/* Botão de Cadastrar */}
                    <FormGroup className="text-center">
                        <Button
                            variant="primary"
                            type="submit"
                            id='btn-cadastro' className='mb-2 fs-4 fw-medium w-100'
                        >
                            Cadastrar
                        </Button>
                    </FormGroup>

                </Form>
                {/* Renderiza o alerta de sucesso após passar nas validações */}
                {exibirSucesso && (
                    <Alert variant="success" onClose={() => setExibirSucesso(false)} dismissible className="mt-3" >
                        Professor cadastrado com sucesso!
                    </Alert>
                )}
            </Container>
        </>
    )
}

export default CadastrarProfessor;