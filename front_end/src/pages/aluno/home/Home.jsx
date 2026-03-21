import { Button, Container } from "react-bootstrap"
import UserNavBar from "../../../components/usernavbar/UserNavBar"

const Home = () => {
    const nomeAluno = "Aluno"
    return (
        <>
            <UserNavBar
                userName={nomeAluno}
                opcoes={["sair"]}

            />
            <Container className="mt-5 " style={{ maxWidth: '1200px' }}>
                <div className="border border-dark rounded-4 shadow-sm">

                    <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center mb-5'>Seja Bem vindo, {nomeAluno} </h2>

                    {/*Criar Grupo e Solicitar orientação */}
                    <div className="d-flex justify-content-around gap-5 m-3">

                        <Button variant="primary"
                            type="submit"
                            id='btn-criar-grupo' className='fs-4 fw-medium w-100'
                        >
                            Criar Grupo
                        </Button>

                        <Button variant="primary"
                            type="submit"
                            id='btn-criar-grupo' className='fs-4 fw-medium w-100'
                        >
                            Solicitar orientação
                        </Button>

                    </div>

                    {/*Sair do Grupo e Cadastrar Co-orientador */}
                    <div className="d-flex justify-content-around gap-5 m-3">

                        <Button variant="primary"
                            type="submit"
                            id='btn-sair-grupo' className='fs-4 fw-medium w-100'
                        >
                            Sair do Grupo
                        </Button>

                        <Button variant="primary"
                            type="submit"
                            id='btn-cadastrar-co-orientador' className='fs-4 fw-medium w-100'
                        >
                            Cadastrar Co-orientador
                        </Button>

                    </div>
                </div>
            </Container>
        </>
    )
}
export default Home