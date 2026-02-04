import addIcon from '../../../assets/add.svg'
import CancelIcon from '../../../assets/Cancel.svg'
import { Container, Table } from 'react-bootstrap';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import { FormGroup } from "react-bootstrap";
const FormarGrupo = () => {
    return (
        <>
            <Container className="mt-5"  >
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Formar Grupo</h2>
                <Form
                    validated={true}
                    className='border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm px-5'>

                    {/* Tema */}
                    <FormGroup className="mb-3 d-flex justify-content-center gap-3" controlId="formBasicTema">
                        <FormLabel className='text-secondary fs-4 fw-bold'>Tema:</FormLabel>
                        <FormControl type="text" placeholder="Digite o tema do grupo" required={true} className='bg-white text-black fw-bold fs-5 w-75' />
                    </FormGroup>

                    {/* Tipo de TG */}
                    <FormGroup className="mb-3 d-flex justify-content-center gap-3" controlId="formBasicRole">
                        <FormLabel className='text-secondary fs-4 fw-bold'>Tipo de TG: </FormLabel>
                        <FormSelect required={true} className='bg-primary text-white fw-bold fs-5 w-50 text-center'>
                            <option value="" disabled selected>Selecione o tipo de tg do seu trabalho</option>
                            <option value="monografia">Monografia</option>
                            <option value="desenvolvimento">Desenvolvimento</option>
                            <option value="artigo">Artigo</option>
                            <option value="plano-de-negocio">Plano de negocio</option>
                        </FormSelect>
                    </FormGroup>

                    {/* Aluno */}
                    <FormGroup className="mb-3 d-flex justify-content-center gap-3" controlId="formBasicEmail">
                        <FormLabel className='text-secondary fs-4 fw-bold'>Aluno:</FormLabel>
                        <FormControl type="text" placeholder="Digite o nome do integrante" required={true} className='bg-white text-black fw-bold fs-5 w-75' />
                        <img src={addIcon} alt="Adicionar integrante" width={'55rem'} />
                    </FormGroup>

                    {/* tabela de integrantes */}
                    <FormGroup className="mb-3 d-flex flex-column" controlId="formBasicConfirmPassword">
                        <FormLabel className='text-secondary fs-4 fw-bold text-center'>Integrantes do Grupo</FormLabel>
                        <Table striped bordered hover className='bg-white text-black fw-normal fs-4'>
                            <thead>
                                <tr>
                                    <th>Nome</th>
                                    <th>Remover</th>
                                </tr>
                            </thead>
                            <tbody>
                                {/* Alunos serão buscados no backend e após selecionados aqui ficaram */}
                                <tr >
                                    <td>Aluno1</td>
                                    <td className='w-25 '><img src={CancelIcon} alt="Remover integrante" width={'55rem'} /></td>
                                </tr>
                                <tr>
                                    <td>Aluno2</td>
                                    <td className='w-25 '><img src={CancelIcon} alt="Remover integrante" width={'55rem'} /></td>
                                </tr>
                                <tr>

                                </tr>
                            </tbody>
                        </Table>
                    </FormGroup>

                    {/* Botão de Criar Grupo */}
                    <FormGroup className="text-center">
                        <Button
                            variant="primary"
                            type="submit"
                            id='btn-cadastro' className='mb-2 fs-5 fw-medium w-100'
                        >
                            Criar Grupo
                        </Button>
                    </FormGroup>

                </Form>
            </Container>
        </>
    );
}

export default FormarGrupo;