import { useState } from "react";
import { Container, Form, FormCheck, FormControl, FormGroup, FormLabel, FormSelect, Button } from "react-bootstrap";

const CadastrarCurso = () => {
    const [validado, setValidado] = useState(false);

    const handleSubmit = (event) => {
        event.preventDefault();
        const form = event.currentTarget;
        if (form.checkValidity() === false) {
            event.stopPropagation();
        }
        setValidado(true);
    }

    //TODO: VALIDAÇÃO DOS CHECKBOXES E INPUTS DE NÚMERO



    return (
        <>
            <Container className="mt-5" style={{ maxWidth: '800px' }}>
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Cadastro de Curso</h2>
                <Form
                    validated={validado}
                    className='border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm'
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
                            <div className="d-flex align-items-center gap-3 mb-3">
                                <FormCheck
                                    className="fw-medium"
                                    label={"Desenvolvimento de Software".toUpperCase()}
                                    name="Desenvolvimento de Software"
                                    type="checkbox"
                                    id="checkbox-desenvolvimento"
                                />
                                {/* TODO: Apenas exibir se o checkbox foi selecionado*/}
                                <FormLabel className='text-secondary fs-6 fw-medium '>Quantidade maxima do grupo: </FormLabel>
                                <FormControl type="number" id="desenvolvimentoQnt" className="fw-medium" style={{ width: '5rem' }} placeholder="0" required={true} />
                            </div>
                            {/* Monografia */}
                            <div className="d-flex align-items-center gap-3 mb-3 ">
                                <FormCheck
                                    className="fw-medium"
                                    label={"Monografia".toUpperCase()}
                                    name="Monografia"
                                    type="checkbox"
                                    id="checkbox-monografia"
                                />
                                {/* TODO: Apenas exibir se o checkbox foi selecionado*/}
                                <FormLabel className='text-secondary fs-6 fw-medium'>Quantidade maxima do grupo: </FormLabel>
                                <FormControl type="number" id="monografiaQnt" className="fw-medium" style={{ width: '5rem' }} placeholder="0" required={true} />
                            </div>

                            {/* Artigo */}
                            <div className="d-flex align-items-center gap-3 mb-3">
                                <FormCheck
                                    className="fw-medium"
                                    label={"Artigo".toUpperCase()}
                                    name="Artigo"
                                    type="checkbox"
                                    id="checkbox-artigo"
                                />
                                {/* TODO: Apenas exibir se o checkbox foi selecionado*/}
                                <FormLabel className='text-secondary fs-6 fw-medium'>Quantidade maxima do grupo: </FormLabel>
                                <FormControl type="number" id="artigoQnt" className="fw-medium" style={{ width: '5rem' }} placeholder="0" required={true} />
                            </div>

                            {/* Plano de negócios */}
                            <div className="d-flex align-items-center gap-3">
                                <FormCheck
                                    className="fw-medium"
                                    label={"Plano de negócios".toUpperCase()}
                                    name="Plano de negócios"
                                    type="checkbox"
                                    id="checkbox-plano-negocios"
                                />
                                {/* TODO: Apenas exibir se o checkbox foi selecionado*/}
                                <FormLabel className='text-secondary fs-6 fw-medium'>Quantidade maxima do grupo: </FormLabel>
                                <FormControl type="number" id="planoNegociosQnt" className="fw-medium" style={{ width: '5rem' }} placeholder="0" required={true} />
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