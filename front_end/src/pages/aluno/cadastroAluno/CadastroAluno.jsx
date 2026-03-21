import { Container, InputGroup } from 'react-bootstrap';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import { FormGroup } from "react-bootstrap";

import { bloquearCaracteresInputNome, validarNome } from '../../../utils/utils';

// Zod e RHF (react hook form)
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm, useFieldArray } from "react-hook-form";

//Schema de validação zod
const alunoSchema = z.object({
    nome: z.string()
        .min(1, "Nome é um campo obrigatório")
        .refine(validarNome, "Nome com caracteres invalidos"),
    matricula: z.string()
        .length(11, "A matricula tem que ter 11 dígitos"),
    email: z.email("Formato de email inválido")
        .min(1, "Email é um campo obrigatório"),
    telefone: z.string()
        .length(11, "Telefone inválido"),
    senha: z.string()
        //TODO: Verificar no backend a regra de minimo
        .min(6, "A senha deve ter no mínimo 6 caracteres"),
    confirmarSenha: z.string()
        .min(1, "Confirme sua senha"),
    // Array de Redes
    redes: z.array(z.object({
        rede: z.string(),
        url: z.url("Formato de URL inválido. Insira o link completo (http://...)")
    }))
}).refine((data) => data.senha === data.confirmarSenha, {
    message: "Senha e Confirmar Senha devem ser iguais",
    // Apota o erro para o campo de confirmação
    path: ["confirmarSenha"]
})


const CadastroAluno = () => {

    // Inicializa react hook form
    const {
        register,
        control,
        handleSubmit,
        formState: { errors }
    } = useForm({
        resolver: zodResolver(alunoSchema),
        defaultValues: {
            nome: "", matricula: "", email: "",
            telefone: "", senha: "", confirmarSenha: "",
        }
    });

    //Gerenciamento de arrays dinâmicos(substitui useStates manuais)
    const { fields, append, remove } = useFieldArray({
        control,
        //Nome do array no Schema
        name: "redes"
    });


    // Redes Sociais
    const handleRedeSelecionada = (e) => {
        const redeEscolhida = e.target.value;

        // Evita adicionar se não escolheu nada ou se já adicionou aquela rede (opcional)
        if (redeEscolhida) {
            append({ rede: redeEscolhida, url: "" });
            //Reseta o select
            e.target.value = ""
        }
    };


    // A função que realmente envia os dados caso passe na validação do frontend
    const enviarParaBackend = (dadosValidados) => {
        // Aqui vai o seu fetch/axios enviando o JSON para a API em Java
        console.log("Enviando payload para a API:", dadosValidados);
    };
    return (
        <>
            <Container className="mt-5" style={{ maxWidth: '1000px' }}>
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Cadastro de Aluno</h2>
                <Form
                    onSubmit={handleSubmit(enviarParaBackend)}
                    noValidate
                    className='form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm no-success-icon'>

                    {/* Nome */}
                    <Form.Group className="mb-3" controlId="formBasicName">
                        <Form.Label className='text-secondary fs-4 fw-medium'>Nome Completo</Form.Label>
                        <Form.Control
                            type="text"
                            name="nome"
                            placeholder="Digite seu nome completo" required={true}
                            //Conecta input ao RHF
                            {...register("nome")}
                            className='bg-white text-black fw-normal fs-5'
                            onKeyDown={bloquearCaracteresInputNome}
                            isInvalid={!!errors.nome}
                        />

                        <Form.Control.Feedback type="invalid">
                            {errors.nome?.message}
                        </Form.Control.Feedback>
                    </Form.Group>

                    {/* Matrícula */}
                    <FormGroup className="mb-3" controlId="formBasicMatricula">
                        <Form.Label className='text-secondary fs-4 fw-medium'>Matrícula</Form.Label>
                        <Form.Control
                            type="text" placeholder="Digite sua matrícula"
                            className='bg-white text-black fw-normal fs-5'
                            name="matricula"
                            //Conecta input ao RHF
                            {...register("matricula")}
                            isInvalid={!!errors.matricula} />

                        <Form.Control.Feedback type="invalid">
                            {errors.matricula?.message}
                        </Form.Control.Feedback>
                    </FormGroup>

                    {/* Email */}
                    <Form.Group className="mb-3" controlId="formBasicEmail">
                        <Form.Label className='text-secondary fs-4 fw-medium'>Email</Form.Label>
                        <Form.Control
                            type="email" placeholder="Digite seu email"
                            className='bg-white text-black fw-normal fs-5'
                            name="email"
                            {...register("email")}
                            isInvalid={!!errors.email} />

                        <Form.Control.Feedback type="invalid">
                            {errors.email?.message}
                        </Form.Control.Feedback>
                    </Form.Group>

                    {/* Contato */}
                    <Form.Group className="mb-3">
                        <Form.Label className='text-secondary fs-4 fw-medium'>Telefone</Form.Label>
                        <Form.Control
                            type="tel" placeholder="11912345678"
                            className='bg-white text-black fw-normal fs-5'
                            pattern="[0-9]{2}-[9]{1}-[0-9]{8}"
                            name="telefone"
                            {...register("telefone")}
                            isInvalid={!!errors.telefone} />

                        <Form.Control.Feedback type="invalid">
                            {errors.telefone?.message}
                        </Form.Control.Feedback>
                    </Form.Group>

                    {/* Redes Sociais */}

                    <Form.Group className="mb-4" controlId="formRedes">
                        <Form.Label className='text-secondary fs-4 fw-medium'>Redes Sociais</Form.Label>

                        <Form.Select
                            required={false}
                            className='bg-white fw-medium fs-5 w-100 text-center mb-3'
                            onChange={handleRedeSelecionada}
                            defaultValue=""
                        >

                            <option value="" disabled>Selecione as redes sociais que deseja adicionar</option>
                            <option value="linkedin">Linkedin</option>
                            <option value="instagram">Instagram</option>
                            <option value="facebook">Facebook</option>
                        </Form.Select>

                        {/*Array dinamico gerenciado pelo RHF  */}
                        {fields.map((rede, index) => (
                            <div key={rede.id} className="mb-2">
                                <InputGroup className="w75" key={index}>
                                    {/* Exibe o nome da rede com a primeira letra maiúscula */}
                                    <InputGroup.Text className="text-capitalize fw-bold fs-5" >
                                        {rede.rede}
                                    </InputGroup.Text>

                                    <Form.Control
                                        type="url"
                                        className='fs-5 text-black'
                                        placeholder={`Ex: https://${rede.rede}.com/in/seu-perfil`}
                                        {...register(`redes.${index}.url`)}
                                        isInvalid={!!errors.redes?.[index]?.url}
                                    />
                                    <Button variant='outline-primary' className="fs-5" title='Clique aqui para remover essa rede social' onClick={() => remove(index)}>
                                        Remover
                                    </Button>
                                </InputGroup>
                                {/* Erro da URL da rede específica */}
                                {errors.redes?.[index]?.url && (
                                    <div className="text-danger mt-1 small fw-bold">
                                        {errors.redes[index].url.message}
                                    </div>
                                )}
                            </div>
                        ))}
                    </Form.Group>

                    {/* Senha */}
                    <Form.Group className="mb-4" controlId="formBasicPassword">
                        <Form.Label className='text-secondary fs-4 fw-medium'>Senha</Form.Label>
                        <Form.Control
                            type="password" placeholder="Digite sua senha"
                            className='bg-white text-black fw-normal fs-5'
                            name="senha"
                            {...register("senha")}
                            isInvalid={!!errors.senha}
                        />

                        <Form.Control.Feedback type="invalid">
                            {errors.senha?.message}
                        </Form.Control.Feedback>
                    </Form.Group>

                    {/* Confirmar Senha */}
                    <FormGroup className="mb-4" controlId="formBasicConfirmPassword">
                        <Form.Label className='text-secondary fs-4 fw-medium'>Confirmar Senha</Form.Label>
                        <Form.Control
                            type="password" placeholder="Confirme sua senha"
                            required={true}
                            className='bg-white text-black fw-normal fs-5'
                            name="confirmarSenha"
                            {...register("confirmarSenha")}
                            isInvalid={!!errors.confirmarSenha}
                        />
                        <Form.Control.Feedback type="invalid">
                            {errors.confirmarSenha?.message}
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
            </Container >
        </>
    )
}

export default CadastroAluno;