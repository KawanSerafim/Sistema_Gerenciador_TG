import { Button, Container, Form } from "react-bootstrap"
import TableComponent from "../../../components/table/TableComponent"
import AddIcon from "../../../assets/add.svg"
import { useState } from "react"

const MarcarBanca = () => {

    //TODO: tirar mocks e buscar dados do backend
    // GRUPOS
    const grupos = [{ grupoId: 1, tema: "Tema1", alunos: ["aluno1", "aluno2"] }, { grupoId: 2, tema: "Tema2", alunos: ["aluno3", "aluno4", "aluno5"] }, { grupoId: 3, tema: "Tema3", alunos: ["aluno6", "aluno7", "aluno8"] }]

    // Tabela grupo
    const [selectedGrupo, setSelectedGrupo] = useState({})

    const handleSelectedGrupo = (e) => {
        const grupo = e.target.value
        if (grupo !== "")
            setSelectedGrupo(grupo)
    }
    const colunaTabelaGupo = [{ header: "Alunos", acessor: "alunos" }]

    //Membros
    const [membros, setMembros] = useState([{}])
    const colunaTabelaMembro = [{ header: "Membros da Banca", acessor: "membros" }]

    // Professores
    const professores = [
        { id: 1, nome: "professor1" },
        { id: 2, nome: "professor2" },
        { id: 3, nome: "professor3" }
    ]

    const handleProfessorSelected = (e) => {
        const professor = e.target.value
        if (professor != "") {
            console.log(professor)
            setMembros(membros.push({ id: professor.id, tipo: "professor", membro: professor }))
        }
    }

    //Membros externos
    // TODO: função para buscar membros
    const handleMembroSelected = (e) => {
        const membro = e.target.value
        if (membro != "") {
            console.log(membro)
            setMembros(membros.push({ tipo: "membroExterno", membro: membro }))
        }
    }

    return (
        <>
            <Container className="mt-5"  >
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Marcar banca</h2>
                <Form
                    validated={true}
                    className='border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm px-5'>
                    <div className="d-flex justify-content-center">
                        <Form.Label>Grupo:</Form.Label>
                        <Form.Select
                            onChange={(e) => handleSelectedGrupo(e)}
                        >
                            <option value="" disabled selected>Selecione o grupo</option>
                            {grupos ? (
                                grupos.map((grupo) => (
                                    <option value={grupo.grupoId}>{grupo.tema}</option>
                                ))
                            ) : (
                                <option value="" disabled selected>Nenhum grupo foi encontrado</option>
                            )}
                        </Form.Select>
                    </div>

                    {/* TODO: Após selecionado exibir o tema e preencher a tabela com os nomes dos alunos*/}
                    {selectedGrupo && (
                        <h3>{selectedGrupo.tema}</h3>
                    )}
                    {/* Tabela de Integrantes do grupo */}
                    {selectedGrupo && (
                        <TableComponent
                            columns={colunaTabelaGupo}
                            data={selectedGrupo}
                        />
                    )}

                    <Form.Label>Membros da banca: </Form.Label>
                    {/* Professor */}
                    <div className="d-flex justify-content-between">
                        <Form.Label>Professor: </Form.Label>
                        <Form.Select
                            onChange={(e) => handleProfessorSelected(e)}
                        >
                            <option value="" disabled selected>Selecione o professor</option>
                            {professores ? (
                                professores.map((professor) => (
                                    <option value={professor}>{professor}</option>
                                ))
                            ) : (
                                <option value="" disabled selected>Nenhum professor encontrado</option>
                            )}
                        </Form.Select>
                        {/* <Button
                            onClick={(e) => handleProfessorSelected(e)}
                        >
                            <img src={AddIcon} alt="Adicionar professor" width={'55rem'} />
                        </Button> */}
                    </div>
                    {/* Membro Externo */}
                    <div className="d-flex justify-content-between">
                        {/* TODO: Verificar se é necessario pegar o nome do membro externo ou se será aceito apenas quem esta no sistema */}
                        <Form.Label>Membro Externo: </Form.Label>
                        <Form.Select
                            onChange={handleMembroSelected}
                        >
                            <option value="" disabled selected>Digite o email do membro externo</option>

                        </Form.Select>
                        {/* <Button>
                            <img src={AddIcon} alt="Adicionar membro" width={'55rem'} />
                        </Button> */}
                    </div>

                    {/* Tabela membros */}
                    <TableComponent
                        columns={colunaTabelaMembro}
                        data={membros}
                    />
                    {/* Horario e Local */}
                    <div className="d-flex justify-content-between">
                        <Form.Label>Data:</Form.Label>
                        <Form.Control placeholder="DD/MM/AAA" id="inputData"
                        />
                        <Form.Label>Hora:</Form.Label>
                        <Form.Control placeholder="HH:MM" id="inputHora"
                        />
                        <Form.Label>Sala:</Form.Label>
                        <Form.Select>
                            <option value="" disabled selected>Selecione a sala</option>
                        </Form.Select>
                    </div>
                    <Form.Group>
                        <Button variant="primary">
                            Marcar banca
                        </Button>
                    </Form.Group>
                </Form>
            </Container>
        </>
    )
}
export default MarcarBanca