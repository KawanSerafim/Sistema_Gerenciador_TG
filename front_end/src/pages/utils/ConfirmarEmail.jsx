import { Alert, Container } from 'react-bootstrap';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import { FormControl } from 'react-bootstrap';
import UserNavBar from '../../components/usernavbar/UserNavBar';

import { useState } from "react";
import { useForm } from '../../hooks/useForm';

//Função de validacao

const validarCampos = (valores) => {
    let erros = {};
    if (!valores.codigo) erros.codigo = "Codigo é um campo obrigatório"
    return erros;
}

const ConfirmarEmail = () => {

    const campos = {
        codigo: ""
    }
    const { values, errors, handleChange, handleSubmit } = useForm(campos, validarCampos)

    //Estado para o sucesso
    const [exibirSucesso, setExibirSucesso] = useState(false);

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
                opcoes={["sair"]}
                maxWidth="60rem"
            />
            <Container className="mt-5" style={{ maxWidth: "60rem" }}>
                <h1 className="text-black text-center fw-bold fs-1 mb-5">Confirmar Email</h1>
                <div className="bg-primary text-white border-1 rounded-3 p-3 my-5 text-center">
                    <p className='mt-2 fs-4 fw-bold'>Vamos enviar um email para confirmar seu email institucional</p>
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
                        name="codigo"
                        value={values.codigo}
                        onChange={handleChange}
                        isInvalid={!!errors.codigo}
                        className='text-black fw-bold fs-3 w-75 mb-4 text-center'
                    />
                    {/* Feedback de erro */}
                    <Form.Control.Feedback type="invalid">
                        {errors.codigo}
                    </Form.Control.Feedback>

                    <Button variant="primary" type="submit" size="lg" className='fw-bold fs-5 text-white py-3 rounded-3'>
                        Confirmar Email
                    </Button>
                </Form>
                {/* Renderiza o alerta de sucesso após passar nas validações */}
                {exibirSucesso && (
                    <Alert variant="success" onClose={() => setExibirSucesso(false)} dismissible className="mt-3" >
                        Email confirmado, você será redirecionado para a tela de login!
                    </Alert>
                )}
            </Container>

        </>
    )

}
export default ConfirmarEmail;