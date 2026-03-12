import { Container, Form, FormGroup, FormSelect } from "react-bootstrap"
import UserNavBar from "../../../components/usernavbar/UserNavBar"
import TableComponent from "../../../components/table/TableComponent"

const VisaoAlunosEnviados = () => {

    //TODO: Criar logica para preencher a tabela com os alunos quando a turma for selecionada

    const columns = [
        { header: "Nome do aluno", accessor: "nome" },
        { header: "RA", accessor: "ra" },
        { header: "Situação de Cadastro", accessor: "situacaoCadastro" }
    ]
    //TODO: Filtrar pelo idTurma
    const data = [
        {
            idTurma: "TG1N",
            nome: "Johana",
            ra: "11125685",
            situacaoCadastro: "Email confirmado"
        },
        {
            idTurma: "TG1N",
            nome: "Jill",
            ra: "11125686",
            situacaoCadastro: "Email ainda não confirmado"
        },
        {
            idTurma: "TG1N",
            nome: "Leon Kennedy",
            ra: "11125687",
            situacaoCadastro: "Email confirmado"
        },
        {
            idTurma: "TG1N",
            nome: "Nome do aluno",
            ra: "11125688",
            situacaoCadastro: "Email ainda não confirmado"
        }
    ]

    return (
        <>
            <UserNavBar
                userName="Cristina"
            ></UserNavBar>
            <Container className="mt-5">
                <h2 className='text-black p-3 fs-1 rounded-top-4 text-center mb-2'>Alunos enviados: </h2>
                <Form
                    validated={true}
                    className='form-bg p-4'>

                    {/* Seleção de turma */}
                    <FormGroup className="mb-3 d-flex justify-content-center gap-3" controlId="formSelectTurma">
                        <FormSelect
                            required={true}
                            className='bg-primary text-white fw-bold fs-5 w-50 text-center'
                        >
                            <option value="" disabled selected>Selecione a turma que deseja exibir</option>
                            <option value="TG1N">TG1 - Noite</option>
                            <option value="TG2N">TG2 - Noite</option>
                            <option value="TG1T">TG1 - Tarde</option>
                            <option value="TG2T">TG2 - Tarde</option>
                        </FormSelect>
                    </FormGroup>
                </Form>
                <div className="mt-5">
                    <TableComponent
                        columns={columns}
                        data={data}
                    />
                </div>
            </Container>
        </>
    )
}
export default VisaoAlunosEnviados