import { Container, Navbar } from 'react-bootstrap';
import Nav from 'react-bootstrap/Nav';
import './Styles.css';

const UserNavBar = ({ userName = '', opcoes = ["inicio", "nome", "sair"], maxWidth = "900px" }) => {
    const handleSelect = () => alert(`Redirecionando .... `);
    let largura = "";
    if (opcoes.length == 1) {
        largura = "15%"
    }
    else if (opcoes.length == 2) {
        largura = "75%"
    }
    else if (opcoes.length == 3) {
        largura = "100%"
    }
    return (
        <>
            <Container className="mt-2" style={{ maxWidth: maxWidth }}>
                <Nav
                    variant="pills"
                    activeKey="1"
                    onSelect={handleSelect}
                    className='bg-primary rounded d-flex align-items-center px-3'
                    style={{ minHeight: '60px', width: largura }}>

                    <div className="d-flex flex-1">

                        {opcoes.includes("inicio") &&
                            (
                                <Nav.Item>
                                    <Nav.Link eventKey="1" href="#/home" className='text-white nav__link fs-3 fs-md-4 fw-bold'>
                                        Inicio
                                    </Nav.Link>
                                </Nav.Item>
                            )

                        }
                    </div>
                    <div className="d-flex flex-grow-1 justify-content-center">
                        {opcoes.includes("nome") && (
                            <Nav.Item className="mx-auto">
                                <Navbar.Text disabled="disabled" className='text-white fs-3 fs-md-4 fw-bold'>
                                    Olá, {userName}
                                </Navbar.Text>
                            </Nav.Item>

                        )}
                    </div>
                    <div className="d-flex flex-1 justify-content-end">
                        {opcoes.includes("sair") && (
                            <Nav.Item className={opcoes.includes("nome") ? "" : "ms-auto"}>
                                <Nav.Link eventKey='3' href="#/home" className='text-white nav__link fs-3 fs-md-4 fw-bold'>
                                    {userName != "" ? "Sair" : "Voltar"}
                                </Nav.Link>
                            </Nav.Item>
                        )}
                    </div>
                </Nav>
            </Container>
        </>
    )
}
export default UserNavBar;