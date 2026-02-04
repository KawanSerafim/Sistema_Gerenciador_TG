import { Container } from 'react-bootstrap';
import Nav from 'react-bootstrap/Nav';
import './Styles.css';

const UserNavBar = ({ userName = '' }) => {
    const handleSelect = (eventKey) => alert(`selected ${eventKey}`);
    return (
        <>
            <Container className="mt-2">
                <Nav variant="pills" activeKey="1" onSelect={handleSelect} className='bg-primary rounded d-flex justify-content-between'>
                    <Nav.Item>
                        <Nav.Link eventKey='1' href="#/home" className='text-white nav__link fs-3 fw-bold'>
                            Inicio
                        </Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                        <Nav.Link disabled="disabled" className='text-white fs-3 fw-bold'>
                            Olá, { userName }
                        </Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                        <Nav.Link eventKey='3' href="#/home" className='text-white nav__link fs-3 fw-bold'>
                            Sair
                        </Nav.Link>
                    </Nav.Item>
                </Nav>
            </Container>
        </>
    )
}
export default UserNavBar;