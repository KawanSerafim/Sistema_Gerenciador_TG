import { Container, Form, FormGroup, FormSelect } from "react-bootstrap"
import UserNavBar from "../../../components/usernavbar/UserNavBar"
import TableComponent from "../../../components/table/TableComponent"

const VisaoAlunosEnviados = () => {
    //todo: Add hook de validação
    //TODO: Criar logica para preencher a tabela com os alunos quando a turma for selecionada

    const columns = [
        { header: "Nome do aluno", accessor: "nome", filtravel: true, tipoFiltro: "autocomplete" },
        { header: "RA", accessor: "ra", filtravel: true, tipoFiltro: "text" },
        { header: "Situação de Cadastro", accessor: "situacaoCadastro", filtravel: true, tipoFiltro: "select" }
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
                userName="Professor de TG"
                maxWidth="1500px"
            ></UserNavBar>
            <Container className="mt-5" style={{ maxWidth: "1500px" }}>
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Alunos enviados: </h2>

                <Form
                    noValidate
                    className='form-bg border border-dark border-top-0 p-2 p-md-4 rounded-bottom-4 shadow-sm'>

                    {/* Seleção de turma */}
                    <FormGroup className="my-3 d-flex justify-content-center gap-3" controlId="formSelectTurma">
                        <FormSelect
                            required={true}
                            title="Selecione a turma que deseja exibir"
                            className='bg-white text-black fw-medium fs-4 w-50 text-center'
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
                        colunas={columns}
                        dados={data}
                    />
                </div>
            </Container>
        </>
    )
}
export default VisaoAlunosEnviados