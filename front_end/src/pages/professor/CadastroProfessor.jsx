import { Container } from 'react-bootstrap';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import { FormGroup } from "react-bootstrap";

const CadastroProfessor = () => {
    return (
        <>
            <Container className="mt-5" style={{ maxWidth: '800px' }}>
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Cadastro de Professor</h2>
                <Form
                    validated={true}
                    className='border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm'>
                    {/* Nome */}
                    <Form.Group className="mb-3" controlId="formBasicName">
                        <Form.Label className='text-secondary fs-5 fw-medium'>Nome Completo</Form.Label>
                        <Form.Control type="text" placeholder="Digite seu nome completo" required={true} className='bg-white text-black fw-normal fs-5' />
                    </Form.Group>

                    {/* Matrícula */}
                    <FormGroup className="mb-3" controlId="formBasicMatricula">
                        <Form.Label className='text-secondary fs-5 fw-medium'>Matrícula</Form.Label>
                        <Form.Control type="text" placeholder="Digite sua matrícula" required={true} className='bg-white text-black fw-normal fs-5' />
                    </FormGroup>

                    {/* Email */}
                    <Form.Group className="mb-3" controlId="formBasicEmail">
                        <Form.Label className='text-secondary fs-5 fw-medium'>Email</Form.Label>
                        <Form.Control type="email" placeholder="Digite seu email" required={true} className='bg-white text-black fw-normal fs-5' />
                    </Form.Group>

                    {/* Senha */}
                    <Form.Group className="mb-3" controlId="formBasicPassword">
                        <Form.Label className='text-secondary fs-5 fw-medium'>Senha</Form.Label>
                        <Form.Control type="password" placeholder="Digite sua senha" required={true} className='bg-white text-black fw-normal fs-5' />
                    </Form.Group>

                    {/* Confirmar Senha */}
                    <FormGroup className="mb-3" controlId="formBasicConfirmPassword">
                        <Form.Label className='text-secondary fs-5 fw-medium'>Confirmar Senha</Form.Label>
                        <Form.Control type="password" placeholder="Confirme sua senha" required={true} className='bg-white text-black fw-normal fs-5' />
                    </FormGroup>

                    {/* Selecionar Cargo */}
                    <FormGroup className="mb-3" controlId="formBasicRole">
                        <Form.Label className='text-secondary fs-5 fw-medium'>Cargo</Form.Label>
                        <Form.Select required={true} className='bg-white text-black fw-normal fs-5'>
                            <option value="" disabled selected>Selecione seu cargo</option>
                            <option value="professor">Professor de TG</option>
                            <option value="coordenador">Coordenador</option>
                            <option value="orientador">Orientador</option>
                        </Form.Select>
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

export default CadastroProfessor;