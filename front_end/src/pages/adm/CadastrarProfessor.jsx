import { Container } from 'react-bootstrap';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import { FormGroup } from "react-bootstrap";

import { useState } from 'react';
import { bloquearCaracteresInputNome, validarNome } from '../../utils/utils';
import { useForm } from '../../hooks/useForm';
import UserNavBar from '../../components/usernavbar/UserNavBar';

const validarCampos = (valores) => {

    let erros = {};
    for (const [key, value] of Object.entries(valores)) {
        //Validações gênericas do campo vazio
        if (!value || typeof value === "string" && value.trim() === '') {
            erros[key] = `${key.charAt(0).toUpperCase()}${key.slice(1).toLowerCase()} é um campo obrigatório`
        }
        //Validações especificas
        //Nome
        else if (key == "nome" && validarNome(value.trim()) == false) {
            erros.nome = "Nome com caracteres invalidos."
        }
        // Matricula
        else if (key == "matricula" && value.trim().length != 11) {
            erros.matricula = 'A matrícula tem que ter 11 dígitos.';
        }
        //Email
        else if (key == "email" && !/\S+@\S+\.\S+/.test(value)) {
            erros.email = 'Formato de email inválido.';
        }
        //Senha e ConfirmarSenha
        else if (key == "confirmarSenha" && value !== valores.senha) {
            erros.confirmarSenha = "Senha e Confirmar Senha devem ser iguais"
        }
        //Cargo
        else if (key == "cargo" && (value === "" || !value)) {
            erros.cargo = "Cargo é um campo obrigatório"
        }
    }

    return erros;
}

const CadastrarProfessor = () => {
    //usar o hook de validação

    //Usar hook de validação

    const campos = {
        nome: "",
        matricula: "",
        email: "",
        senha: "",
        confirmarSenha: "",
        cargo: ""
    }

    const { values, errors, handleChange, handleSubmit } = useForm(campos, validarCampos)

    const [cargoSelecionado, setCargoSelecionado] = useState("")

    const handleCargoSelecionado = (e) => {
        //Troca no estado
        setCargoSelecionado(e.target.value)
        //Envia para validação
        handleChange(e)
    }


    //Estado para o sucesso
    const [exibirSucesso, setExibirSucesso] = useState(false)


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
                userName="Administrador"
                maxWidth="800px"
            ></UserNavBar>
            <Container className="mt-5" style={{ maxWidth: '800px' }}>

                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Cadastro de Professor</h2>
                <Form
                    noValidate
                    className='form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm'
                    onSubmit={handleSubmit(enviarParaBackend)}
                >
                    {/* Nome */}
                    <Form.Group className="mb-3" controlId="formBasicName">
                        <Form.Label className='text-secondary fs-5 fw-medium'>Nome Completo</Form.Label>
                        <Form.Control
                            type="text"
                            placeholder="Digite seu nome completo"
                            name="nome"
                            value={values.nome}
                            onChange={handleChange}
                            onKeyDown={bloquearCaracteresInputNome}
                            isInvalid={!!errors.nome}
                            className='bg-white text-black fw-normal fs-5'

                        />
                        <Form.Control.Feedback type="invalid">
                            {errors.nome}
                        </Form.Control.Feedback>
                    </Form.Group>

                    {/* Matrícula */}
                    <FormGroup className="mb-3" controlId="formBasicMatricula">
                        <Form.Label className='text-secondary fs-5 fw-medium'>Matrícula</Form.Label>
                        <Form.Control
                            type="text" placeholder="Digite sua matrícula"
                            name="matricula"
                            value={values.matricula}
                            onChange={handleChange}
                            isInvalid={!!errors.matricula}
                            className='bg-white text-black fw-normal fs-5' />
                        <Form.Control.Feedback type="invalid">
                            {errors.matricula}
                        </Form.Control.Feedback>
                    </FormGroup>

                    {/* Email */}
                    <Form.Group className="mb-3" controlId="formBasicEmail">
                        <Form.Label className='text-secondary fs-5 fw-medium'>Email</Form.Label>
                        <Form.Control
                            type="email"
                            placeholder="Digite seu email"
                            name="email"
                            value={values.email}
                            onChange={handleChange}
                            isInvalid={!!errors.email}
                            className='bg-white text-black fw-normal fs-5' />

                        <Form.Control.Feedback type="invalid">
                            {errors.email}
                        </Form.Control.Feedback>
                    </Form.Group>

                    {/* Senha */}
                    <Form.Group className="mb-3" controlId="formBasicPassword">
                        <Form.Label className='text-secondary fs-5 fw-medium'>Senha</Form.Label>
                        <Form.Control type="password" placeholder="Digite sua senha"
                            name="senha"
                            value={values.senha}
                            onChange={handleChange}
                            isInvalid={!!errors.senha} className='bg-white text-black fw-normal fs-5' />

                        <Form.Control.Feedback type="invalid">
                            {errors.senha}
                        </Form.Control.Feedback>
                    </Form.Group>

                    {/* Confirmar Senha */}
                    <FormGroup className="mb-3" controlId="formBasicConfirmPassword">
                        <Form.Label className='text-secondary fs-5 fw-medium'>Confirmar Senha</Form.Label>
                        <Form.Control type="password" placeholder="Confirme sua senha"
                            name="confirmarSenha"
                            value={values.confirmarSenha}
                            onChange={handleChange}
                            isInvalid={!!errors.confirmarSenha}
                            className='bg-white text-black fw-normal fs-5' />
                        <Form.Control.Feedback type="invalid">
                            {errors.confirmarSenha}
                        </Form.Control.Feedback>
                    </FormGroup>

                    {/* Selecionar Cargo */}
                    <FormGroup className="mb-3" controlId="formBasicRole">
                        <Form.Label className='text-secondary fs-5 fw-medium'>Cargo</Form.Label>
                        <Form.Select
                            name="cargo"
                            value={cargoSelecionado}
                            onChange={handleCargoSelecionado}
                            isInvalid={!!errors.cargo}
                            className='bg-white text-black fw-normal fs-5'>
                            <option value="" disabled selected>Selecione seu cargo</option>
                            <option value="professor">Professor de TG</option>
                            <option value="coordenador">Coordenador</option>
                            <option value="orientador">Orientador</option>
                        </Form.Select>
                        <Form.Control.Feedback type="invalid">
                            {errors.cargo}
                        </Form.Control.Feedback>
                    </FormGroup>

                    {/* Botão de Cadastrar */}
                    <FormGroup className="text-center">
                        <Button
                            variant="primary"
                            type="submit"
                            id='btn-cadastro' className='mb-2 fs-5 fw-medium w-100'
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