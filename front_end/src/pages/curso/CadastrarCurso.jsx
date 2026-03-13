import { useState } from "react";
import { Container, Form, FormCheck, FormControl, FormGroup, FormLabel, FormSelect, Button } from "react-bootstrap";


const CadastrarCurso = () => {
    //TODO: VALIDAÇÃO com o hook useForm

    const [validado, setValidado] = useState(false);

    const handleSubmit = (event) => {
        event.preventDefault();
        const form = event.currentTarget;
        if (form.checkValidity() === false) {
            event.stopPropagation();
        }
        setValidado(true);
    }

    const [tiposAtivos, setTiposAtivos] = useState({
        checkboxSoftware: false,
        checkboxMonografia: false,
        checkboxArtigo: false,
        checkboxPlanoNegocios: false
    });

    // Função para alternar o estado
    const handleCheckboxChange = (e) => {
        const { id, checked } = e.target;
        setTiposAtivos(prev => ({
            ...prev,
            [id]: checked
        }));
    };


    return (
        <>
            <Container className="mt-5" style={{ maxWidth: '800px' }}>
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Cadastro de Curso</h2>
                <Form
                    validated={validado}
                    className='form-bg border border-secondary-subtle border-top-0 p-4 rounded-bottom-4 shadow-sm'
                    id="formCurso"
                    onSubmit={handleSubmit}

                >
                    {/* Nome */}
                    <FormGroup className="mb-3 d-flex flex-column" controlId="formBasicName">
                        <FormLabel className='text-secondary fs-5 fw-medium'>Nome do Curso</FormLabel>
                        <FormControl type="text" placeholder="Digite o nome do curso" required={true} className='bg-white text-black fw-normal fs-5' />
                    </FormGroup>
                    {/* Turno e Disciplinas */}
                    <FormGroup className="mb-3" controlId="formBasicTurnoDisciplinas">
                        <div className="row">
                            {/* Turno */}
                            <div className="col-md-6">

                                <FormLabel className='text-secondary fs-5 fw-medium d-block mb-2'>Turno</FormLabel>
                                <div className="mb-2 fw-medium fs-6">
                                    <FormCheck
                                        inline
                                        label="Manhã"
                                        name="Manhã"
                                        type="checkbox"
                                        id="checkbox-manha"
                                        className=""
                                    />
                                    <FormCheck
                                        inline
                                        label="Tarde"
                                        name="Tarde"
                                        type="checkbox"
                                        id="checkbox-tarde"
                                    />
                                    <FormCheck
                                        inline
                                        label="Noite"
                                        name="Noite"
                                        type="checkbox"
                                        id="checkbox-noite"
                                    />
                                </div>
                            </div>
                            {/* Disciplinas */}
                            <div className="col-md-6">
                                <FormLabel className='text-secondary fs-5 fw-medium d-block mb-2'>Disciplinas</FormLabel>
                                <div className="mb-3 fw-medium fs-6">
                                    <FormCheck
                                        inline
                                        label="TG1"
                                        name="TG1"
                                        type="checkbox"
                                        id="checkbox-tg1"
                                    />
                                    <FormCheck
                                        inline
                                        label="TG2"
                                        name="TG2"
                                        type="checkbox"
                                        id="checkbox-tg2"
                                    />
                                </div>
                            </div>
                        </div>
                    </FormGroup>

                    {/* Tipos de trab de graduacao */}
                    <FormGroup className="mb-3 d-flex flex-column" controlId="formBasicTipo">
                        <FormLabel className='text-secondary fs-5 fw-medium'>Tipo de Trabalho de Graduação</FormLabel>

                        <div className="mb-3">
                            {/* Desenvolvimento de Software */}
                            <div className="d-flex align-items-center gap-2 mb-3">
                                <FormCheck
                                    name="Desenvolvimento de Software"
                                    type="checkbox"
                                    id="checkboxSoftware"
                                    checked={tiposAtivos.checkboxSoftware}
                                    onChange={handleCheckboxChange}
                                />
                                <FormLabel htmlFor="software" className='mb-0 fw-bold text-uppercase'>Desenvolvimento de Software </FormLabel>

                                {/* Poderá selecionar o campo number apenas se o checkbox foi selecionado*/}
                                <div className="d-flex align-items-center gap-2 ms-auto">
                                    <FormLabel className={tiposAtivos.checkboxSoftware ? "text-dark fs-6 fw-medium" : "text-muted fs-6 fw-medium"}>Quantidade maxima de integrantes do grupo: </FormLabel>
                                    <FormControl type="number" id="desenvolvimentoQnt" className="fw-medium"
                                        style={{
                                            width: '5rem',
                                            backgroundColor: tiposAtivos.checkboxSoftware ? '#FFFFFF' : '#E9ECEF', // Muda o fundo se desativado
                                            cursor: tiposAtivos.checkboxSoftware ? 'text' : 'not-allowed'

                                        }} placeholder="0"
                                        disabled={!tiposAtivos.checkboxSoftware}
                                    />

                                </div>
                            </div>
                            {/* Monografia */}
                            <div className="d-flex align-items-center gap-2 mb-3 ">
                                <FormCheck
                                    name="Monografia"
                                    type="checkbox"
                                    id="checkboxMonografia"
                                    checked={tiposAtivos.checkboxMonografia}
                                    onChange={handleCheckboxChange}
                                />
                                <FormLabel htmlFor="checkboxMonografia" className='mb-0 fw-bold text-uppercase'>Monografia</FormLabel>

                                <div className="d-flex align-items-center gap-2 ms-auto">
                                    {/* TODO: Apenas exibir se o checkbox foi selecionado*/}
                                    <FormLabel className='text-secondary fs-6 fw-medium'>Quantidade maxima de integrantes do grupo: </FormLabel>
                                    <FormControl type="number" id="monografiaQnt" className="fw-medium" placeholder="0"
                                        style={{
                                            width: '5rem',
                                            backgroundColor: tiposAtivos.checkboxMonografia ? '#FFFFFF' : '#E9ECEF', // Muda o fundo se desativado
                                            cursor: tiposAtivos.checkboxMonografia ? 'text' : 'not-allowed'

                                        }}
                                        disabled={!tiposAtivos.checkboxMonografia}

                                    />

                                </div>
                            </div>

                            {/* Artigo */}
                            <div className="d-flex align-items-center gap-2 mb-3">
                                <FormCheck
                                    name="Artigo"
                                    type="checkbox"
                                    id="checkboxArtigo"
                                    checked={tiposAtivos.checkboxArtigo}
                                    onChange={handleCheckboxChange}
                                />
                                <FormLabel htmlFor="checkboxArtigo" className='mb-0 fw-bold text-uppercase'>Artigo</FormLabel>

                                <div className="d-flex align-items-center gap-2 ms-auto">
                                    {/* TODO: Apenas exibir se o checkbox foi selecionado*/}
                                    <FormLabel className='text-secondary fs-6 fw-medium'>Quantidade maxima de integrantes do grupo: </FormLabel>
                                    <FormControl type="number" id="artigoQnt" className="fw-medium" placeholder="0"
                                        style={{
                                            width: '5rem',
                                            // Muda o fundo se desativado
                                            backgroundColor: tiposAtivos.checkboxArtigo ? '#FFFFFF' : '#E9ECEF',
                                            cursor: tiposAtivos.checkboxArtigo ? 'text' : 'not-allowed'

                                        }}
                                        disabled={!tiposAtivos.checkboxArtigo} />
                                </div>
                            </div>

                            {/* Plano de negócios */}
                            <div className="d-flex align-items-center gap-2 fw-medium">
                                <FormCheck
                                    className=""
                                    name="Plano de negócios"
                                    type="checkbox"
                                    id="checkboxPlanoNegocios"
                                    checked={tiposAtivos.checkboxPlanoNegocios}
                                    onChange={handleCheckboxChange}
                                />
                                <FormLabel htmlFor="checkboxPlanoNegocios" className='mb-0 fw-bold text-uppercase'>Plano de negócios</FormLabel>

                                <div className="d-flex align-items-center gap-2 ms-auto">
                                    {/* TODO: Apenas exibir se o checkbox foi selecionado*/}
                                    <FormLabel className='text-secondary fs-6 fw-medium'>Quantidade maxima de integrantes do grupo: </FormLabel>
                                    <FormControl type="number" id="planoNegociosQnt" className="fw-medium" placeholder="0"
                                        style={{
                                            width: '5rem',
                                            // Muda o fundo se desativado
                                            backgroundColor: tiposAtivos.checkboxPlanoNegocios ? '#FFFFFF' : '#E9ECEF',
                                            cursor: tiposAtivos.checkboxPlanoNegocios ? 'text' : 'not-allowed'

                                        }}
                                        disabled={!tiposAtivos.checkboxPlanoNegocios} />
                                </div>
                            </div>
                        </div>
                    </FormGroup>
                    {/* Selecionar Coordenador */}
                    <FormGroup className="mb-3" controlId="formCoordenador">
                        <FormLabel className='text-secondary fs-5 fw-medium'>Coordenador do Curso</FormLabel>
                        <FormSelect required={true} className='bg-dark-subtle text-black fw-medium fs-5'>
                            <option key='blankChoice' hidden value=''>Digite ou selecione o coordenador do curso</option>
                            <option key='coord1' value='coord1'>Coordenador 1</option>
                        </FormSelect>
                    </FormGroup>

                    {/* Botão de Cadastrar */}
                    <FormGroup className="text-center">
                        <Button
                            variant="primary"
                            type="submit"
                            id='btn-cadastro' className='mb-2 fs-5 fw-medium w-100'
                        >
                            Cadastrar
                        </Button>
                    </FormGroup>
                </Form>
            </Container >
        </>
    )

}
export default CadastrarCurso;