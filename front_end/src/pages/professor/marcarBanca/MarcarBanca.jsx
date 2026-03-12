import { Button, Container, Form } from "react-bootstrap"
import TableComponent from "../../../components/table/TableComponent"
import AddIcon from "../../../assets/add.svg"
import { useState } from "react"

const MarcarBanca = () => {

    //TODO: tirar mocks e buscar dados do backend
    // GRUPOS
    const grupos = [
        { grupoId: 1, tema: "Tema1", alunos: [{ nome: "aluno1" }, { nome: "aluno2" }] },
        { grupoId: 2, tema: "Tema2", alunos: [{ nome: "aluno3" }, { nome: "aluno4" }, { nome: "aluno5" }] },
        { grupoId: 3, tema: "Tema3", alunos: [{ nome: "aluno6" }, { nome: "aluno7" }, { nome: "aluno8" }] }
    ]

    // Tabela grupo
    const [selectedGrupo, setSelectedGrupo] = useState(null)

    const handleSelectedGrupo = (e) => {
        const id = parseInt(e.target.value);
        const grupoEncontrado = grupos.find(i => i.grupoId === id)
        if (grupoEncontrado)
            setSelectedGrupo(grupoEncontrado)
    }

    const colunaTabelaGupo = [{ header: "Alunos", accessor: "nome" }]

    //Membros
    const [membros, setMembros] = useState([])
    const colunaTabelaMembro = [{ header: "Membros da Banca", accessor: "nome" }]

    // Professores
    const professores = [
        { id: 1, nome: "professor1" },
        { id: 2, nome: "professor2" },
        { id: 3, nome: "professor3" }
    ]

    const [profSelecionado, setProfSelecionado] = useState("");

    const handleAddProfessor = () => {
        if (profSelecionado != "") {
            const prof = professores.find(i => i.id === parseInt(profSelecionado));
            //Evita duplicatas
            if (prof && !membros.some(membro => membro.id === prof.id && membro.tipo === "professor")) {
                //Copia a lista anterior e adiciona o novo professor
                setMembros([...membros, { id: prof.id, tipo: "professor", nome: prof.nome }])
            }
        }
    }

    //Membros externos
    //TODO: Verificar se a unica informação pedida do membro externo será o email
    const [emailExterno, setEmailExterno] = useState("");

    const handleAddMembroExterno = () => {
        if (emailExterno && emailExterno.trim() !== "") {
            const jaExiste = membros.some(membro => membro.nome === emailExterno);
            if (!jaExiste) {
                setMembros([...membros, { id: emailExterno, tipo: "membroExterno", nome: emailExterno }]);
                //Limpa o input apos adicionar
                setEmailExterno("");
            }
        }
    }

    return (
        <>
            <Container className="mt-5" style={{ minWidth: "800px" }}  >
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Marcar banca</h2>
                <Form
                    validated={true}
                    className='form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm px-5'>

                    {/* Grupo */}
                    <div className="d-flex justify-content-center align-items-center mb-4 gap-2">
                        <Form.Label className="m-0 fw-bold text-secondary">Grupo:</Form.Label>
                        <Form.Select
                            defaultValue=""
                            onChange={handleSelectedGrupo}
                            className="w-50 bg-primary border-primary text-white fw-bold"
                        >
                            <option value="" disabled selected>Selecione o grupo</option>
                            {
                                grupos.map((grupo) => (
                                    <option key={grupo.grupoId} value={grupo.grupoId}>{grupo.grupoId}</option>
                                ))
                            }
                        </Form.Select>
                    </div>

                    {/*Após selecionado exibir o tema e preencher a tabela com os nomes dos alunos*/}
                    {selectedGrupo && (
                        <div className="text-center mb-4">
                            <h5 className="text-primary fw-bold">[{selectedGrupo.tema}]</h5>
                            <div className="mt-3">
                                {/* Tabela de Integrantes do grupo */}
                                <TableComponent
                                    columns={colunaTabelaGupo}
                                    data={selectedGrupo.alunos}
                                />
                            </div>
                        </div>
                    )}

                    <h6 className="fw-bold text-primary mt-4 mb-3">Membros da banca: </h6>

                    {/* Professor */}
                    <div className="d-flex align-items-center mb-3 gap-2">
                        <Form.Label className="m-0 fw-bold text-primary" style={{ width: "130px" }}>Professor: </Form.Label>
                        <Form.Select
                            defaultValue=""
                            onChange={(e) => setProfSelecionado(e.target.value)}
                            className="border-primary text-white bg-primary flex-grow-1"
                        >
                            <option value="" disabled selected>Selecione o professor</option>
                            {
                                professores.map((professor) => (
                                    <option key={professor.id} value={professor.id}>{professor.nome}</option>
                                ))
                            }
                        </Form.Select>
                        <Button
                            variant="link"
                            className="p-0 text-primary"
                            onClick={handleAddProfessor}
                        >
                            <img src={AddIcon} alt="Adicionar professor" width={'55rem'} />
                        </Button>
                    </div>
                    {/* Membro Externo */}
                    <div className="d-flex align-items-center mb-4 gap-2">
                        {/* TODO: Verificar se é necessario pegar o nome do membro externo ou se será aceito apenas quem esta no sistema */}
                        <Form.Label className="m-0 fw-bold text-primary" style={{ width: "130px" }}>Membro Externo: </Form.Label>
                        <Form.Control
                            type="email"
                            placeholder="Digite o email do membro externo"
                            value={emailExterno}
                            onChange={(e) => setEmailExterno(e.target.value)}
                            className="flex-grow-1"
                        />
                        <Button variant="link" className="p-0 text-primary"
                            onClick={handleAddMembroExterno}>
                            <img src={AddIcon} alt="Adicionar membro" width={'55rem'} />
                        </Button>
                    </div>

                    {/* Tabela membros */}
                    {membros.length > 0 && (
                        <div className="mb-4">
                            <TableComponent
                                columns={colunaTabelaMembro}
                                data={membros}
                            />
                        </div>
                    )}

                    {/* Horario e Local */}
                    <div className="d-flex align-items-center justify-content-between mb-4 gap-2 fw-bold text-primary">
                        <div className="d-flex align-items-center gap-2">
                            <Form.Label className="m-0">Data:</Form.Label>
                            <Form.Control type="date" placeholder="DD/MM/AAA" />
                        </div>
                        <div className="d-flex align-items-center gap-2">
                            <Form.Label className="m-0">Hora:</Form.Label>
                            <Form.Control type="time" placeholder="HH:MM" />
                        </div>
                        <div className="d-flex align-items-center gap-2">
                            <Form.Label className="m-0">Sala:</Form.Label>
                            <Form.Select defaultValue="" className="bg-primary text-white border-primary">
                                <option value="" disabled selected>Selecione a sala</option>
                                <option value="1">Sala 1</option>
                                <option value="2">Sala 2</option>
                            </Form.Select>
                        </div>
                    </div>
                    <div className="d-flex justify-content-center mt-5">
                        <Button variant="primary" className="w-50 fw-bold">
                            Marcar banca
                        </Button>
                    </div>
                </Form>
            </Container>
        </>
    )
}
export default MarcarBanca