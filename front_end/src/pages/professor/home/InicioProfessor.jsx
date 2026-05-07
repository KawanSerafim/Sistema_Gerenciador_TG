import { Button, Container } from "react-bootstrap"
import UserNavBar from "../../../components/usernavbar/UserNavBar"
import { useNavigate } from "react-router-dom"

const InicioProfessor = () => {
    const nomeProfessor = "Professor de TG"
    const navigate = useNavigate()

    const handleClick = (evento) => {
        if (evento.target.value.includes("coordenador")) {
            navigate(`/${evento.target.value}`)
        }
        // Redireciona para as rotas específicas do painel do professor
        navigate(`/professor${evento.target.value}`)
    }

    return (
        <>
            <UserNavBar
                userName={nomeProfessor}
                opcoes={["sair"]}
            />

            <Container className="mt-5" style={{ maxWidth: '1200px' }}>
                <div className="border border-dark rounded-4 shadow-sm pb-3">

                    <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center mb-5'>
                        Seja Bem-vindo, {nomeProfessor}
                    </h2>

                    {/* Linha 1: Cadastrar Turma e Visão de Grupos */}
                    <div className="d-flex justify-content-around gap-5 m-3">
                        <Button
                            variant="primary"
                            type="button"
                            onClick={handleClick}
                            value={'/coordenador/cadastrarTurmaTG'}
                            id='btn-cadastrar-turma'
                            className='fs-4 fw-medium w-100'
                        >
                            Cadastrar turma TG
                        </Button>

                        <Button
                            variant="primary"
                            type="button"
                            onClick={handleClick}
                            // Essa é a rota da tela de visão que acabamos de integrar!
                            value={'/visaoGrupos'}
                            id='btn-visao-grupos'
                            className='fs-4 fw-medium w-100'
                        >
                            Visão grupos TG
                        </Button>
                    </div>

                    {/* Linha 2: Enviar arquivo da Turma */}
                    <div className="d-flex justify-content-center gap-5 m-3">
                        <Button
                            variant="primary"
                            type="button"
                            onClick={handleClick}
                            value={'/enviarTurma'}
                            id='btn-enviar-arquivo'
                            // w-50 para ele ficar do tamanho de um botão normal, centralizado
                            className='fs-4 fw-medium w-50'
                        >
                            Enviar arquivo da turma TG
                        </Button>
                    </div>

                </div>
            </Container>
        </>
    )
}

export default InicioProfessor;