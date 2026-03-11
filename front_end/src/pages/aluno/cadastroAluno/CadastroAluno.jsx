import { Container } from 'react-bootstrap';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import { FormGroup } from "react-bootstrap";
import { useForm } from "../../../hooks/useForm"

//Função pura que recebe os valores digitados e retorna um objeto com ois erros, caso não tenha erros retorna um objeto vazio
const validarCadastro = (valores) => {
    let erros = {};
    for (const [key, value] of Object.entries(valores)) {
        //Validações gênericas do campo vazio
        if (!value || typeof value === "string" && value.trim() === '') {
            erros[key] = `${key.charAt(0).toUpperCase()}${key.slice(1).toLowerCase()} é um campo obrigatório`
        }
        //Validações especificas
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

    }


    return erros;
};


const CadastroAluno = () => {
    //Nomes dos campos a serem validados
    const campos = {
        nome: "",
        matricula: "",
        email: "",
        senha: "",
        confirmarSenha: "",
    }
    const { values, errors, handleChange, handleSubmit } = useForm(
        campos,
        validarCadastro
    );

    // A função que realmente envia os dados caso passe na validação do frontend
    const enviarParaBackend = (dadosValidados) => {
        // Aqui vai o seu fetch/axios enviando o JSON para a API em Java
        console.log("Enviando payload para a API:", dadosValidados);
    };
    return (
        <>
            <Container className="mt-5" style={{ maxWidth: '800px' }}>
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Cadastro de Aluno</h2>
                <Form
                    onSubmit={handleSubmit(enviarParaBackend)} noValidate
                    className='border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm no-success-icon'>
                    {/* Nome */}
                    <Form.Group className="mb-3" controlId="formBasicName">
                        <Form.Label className='text-secondary fs-5 fw-medium'>Nome Completo</Form.Label>
                        <Form.Control
                            type="text"
                            name="nome"
                            placeholder="Digite seu nome completo" required={true}
                            value={values.nome}
                            className='bg-white text-black fw-normal fs-5'
                            onChange={handleChange}
                            isInvalid={!!errors.nome}
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
                            required={true}
                            className='bg-white text-black fw-normal fs-5'
                            name="matricula"
                            value={values.matricula}
                            onChange={handleChange}
                            isInvalid={!!errors.matricula} />

                        <Form.Control.Feedback type="invalid">
                            {errors.matricula}
                        </Form.Control.Feedback>
                    </FormGroup>

                    {/* Email */}
                    <Form.Group className="mb-3" controlId="formBasicEmail">
                        <Form.Label className='text-secondary fs-5 fw-medium'>Email</Form.Label>
                        <Form.Control
                            type="email" placeholder="Digite seu email"
                            required={true}
                            className='bg-white text-black fw-normal fs-5'
                            name="email"
                            value={values.email}
                            onChange={handleChange}
                            isInvalid={!!errors.email} />

                        <Form.Control.Feedback type="invalid">
                            {errors.email}
                        </Form.Control.Feedback>
                    </Form.Group>

                    {/* Senha */}
                    <Form.Group className="mb-3" controlId="formBasicPassword">
                        <Form.Label className='text-secondary fs-5 fw-medium'>Senha</Form.Label>
                        <Form.Control
                            type="password" placeholder="Digite sua senha"
                            required={true}
                            className='bg-white text-black fw-normal fs-5'
                            name="senha"
                            value={values.senha}
                            onChange={handleChange}
                            isInvalid={!!errors.senha}
                        />

                        <Form.Control.Feedback type="invalid">
                            {errors.senha}
                        </Form.Control.Feedback>
                    </Form.Group>

                    {/* Confirmar Senha */}
                    <FormGroup className="mb-3" controlId="formBasicConfirmPassword">
                        <Form.Label className='text-secondary fs-5 fw-medium'>Confirmar Senha</Form.Label>
                        <Form.Control
                            type="password" placeholder="Confirme sua senha"
                            required={true}
                            className='bg-white text-black fw-normal fs-5'
                            name="confirmarSenha"
                            value={values.confirmarSenha}
                            onChange={handleChange}
                            isInvalid={!!errors.confirmarSenha}
                        />
                        <Form.Control.Feedback type="invalid">
                            {errors.confirmarSenha}
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
            </Container>
        </>
    )
}

export default CadastroAluno;