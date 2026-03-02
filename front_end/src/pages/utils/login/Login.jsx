import { Button, Container, Form, FormGroup } from "react-bootstrap"

const Login = () => {

    return (
        <>
            <Container className="mt-5" style={{ maxWidth: '800px' }}>
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Login</h2>
                <Form
                    validated={true}
                    className='border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm'>

                    {/* Email */}
                    <Form.Group className="mb-3" controlId="formBasicEmail">
                        <Form.Label className='text-secondary fs-4 fw-bold'>Email</Form.Label>
                        <Form.Control type="email" placeholder="Digite seu email" required={true} className='bg-white text-black fw-normal fs-5' />
                    </Form.Group>

                    {/* Senha */}
                    <Form.Group className="mb-3" controlId="formBasicPassword">
                        <Form.Label className='text-secondary fs-4 fw-bold'>Senha</Form.Label>
                        <Form.Control type="password" placeholder="Digite sua senha" required={true} className='bg-white text-black fw-normal fs-5' />
                    </Form.Group>


                    {/* Botão de Cadastrar */}
                    <FormGroup className="text-center">
                        <Button
                            variant="primary"
                            type="submit"
                            id='btn-cadastro' className='mb-2 fs-5 fw-medium w-100'
                        >
                            Entrar
                        </Button>
                    </FormGroup>

                </Form>
            </Container>
        </>
    )
}
export default Login