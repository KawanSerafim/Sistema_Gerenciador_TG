import { Container } from 'react-bootstrap';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import { FormControl } from 'react-bootstrap';
const confirmarEmail = () => {
    return (
        <>
            <Container className="mt-5">
                <h1 className="text-black text-center fw-bold fs-1">Confirmar Email</h1>
                <div className="bg-primary text-white border-1 rounded-3 p-3 my-4 text-center">
                    <p className='mt-2 fw-bold'>Vamos enviar um email para confirmar seu email institucional</p>
                </div>
                <Form
                    validated={true}
                    className='p-4 rounded-4 shadow-sm px-5 d-flex flex-column align-items-center flex-nowrap'>

                    {/* Codigo */}

                    <FormControl type="text" placeholder="Digite o código enviado em seu email" required={true} className='text-black fw-bold fs-3 w-75 mb-4 text-center' />

                    <Button variant="primary" type="submit" size="lg" className='fw-bold fs-5 text-white py-3 rounded-3'>
                        Confirmar Email
                    </Button>
                </Form>
            </Container>

        </>
    )

}
export default confirmarEmail;