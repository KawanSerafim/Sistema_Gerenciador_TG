
import { Container } from 'react-bootstrap';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';

const SolicitarOrientacao = () => {
    return (
        <>
            <Container className="mt-5 text-center ">
                <h2 className='bg-primary text-white p-2 fs-1 rounded-top-4'>Solicitar Orientação</h2>
                <Form validated={true} className='form-bg rounded-bottom-4 border border-2 border-black p-2'>
                    <Form.Group className="mb-3" controlId="formBasicEmail">
                        <Form.Label className='text-primary'>Digite ou selecione o orientador</Form.Label>
                        <Form.Select id='orientadorSelect' required={true} className='bg-primary text-white fw-medium fs-5' >
                            <option defaultValue={''} selected={true} disabled>Opções serão buscadas no backend</option>
                            <option>Orientador 1</option>
                            <option>Orientador 2</option>
                        </Form.Select>
                    </Form.Group>
                    <Button variant="primary" type="submit" id='btn-select' className='mb-2 p-2 fs-5 fw-medium'>
                        Enviar solicitação
                    </Button>
                </Form>
            </Container>
        </>
    )
}

export default SolicitarOrientacao