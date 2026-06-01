import { Container, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import UserNavBar from "../usernavbar/UserNavBar";

const AcessoNegado = () => {
    const navigate = useNavigate();

    return (
        <>
            {/* NavBar genérica só com botão de voltar */}
            <UserNavBar userName="" opcoes={["sair"]} />

            <Container className="mt-5 text-center" style={{ maxWidth: '800px' }}>
                <div className="border border-danger rounded-4 shadow-sm p-5 bg-white">
                    <h1 className="text-danger display-1 fw-bold mb-3">Ops!</h1>
                    <h2 className="text-dark mb-4">Página não encontrada ou Acesso Negado</h2>
                    <p className="text-secondary fs-5 mb-5">
                        A página que você está tentando acessar não existe ou você não tem
                        as permissões necessárias para visualizá-la.
                    </p>
                    <Button
                        variant="primary"
                        size="lg"
                        className="px-5 fs-4"
                        onClick={() => navigate(-1)} // Volta para a página anterior no histórico
                    >
                        Voltar com Segurança
                    </Button>
                </div>
            </Container>
        </>
    );
};

export default AcessoNegado;