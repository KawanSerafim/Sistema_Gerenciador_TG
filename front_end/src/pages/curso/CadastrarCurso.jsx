import { useEffect, useState } from "react";
import { Container, Form, FormCheck, FormControl, FormGroup, FormLabel, FormSelect, Button, Alert } from "react-bootstrap";
import "./cadastrarCurso.css"
import UserNavBar from "../../components/usernavbar/UserNavBar";

// Zod e RHF para validações
import { useFieldArray, useForm, useWatch } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { cursoSchema } from "../../schemas/curso/cadastrarCursoZodSchema";
import { professorService } from "../../services/professor/professorService"
import { cursoService } from "../../services/curso/cursoService";


const CadastrarCurso = () => {
    // Estado para exibição do resultado de requisições
    const [resultado, setResultado] = useState({ exibir: false, variante: "", mensagem: "" });

    // Estado para armazenar os coordenadores vindos do backend
    const [coordenadores, setCoordenadores] = useState([])

    const {
        register,
        handleSubmit,
        control,
        reset,
        formState: { errors }
    } = useForm({
        resolver: zodResolver(cursoSchema),
        defaultValues: {
            nome: "",
            turno: [],
            disciplina: [],
            coordenador: "",
            tiposTG: [
                { id: 'software', label: 'Desenvolvimento de Software', ativo: false, qntMax: 0 },
                { id: 'monografia', label: 'Monografia', ativo: false, qntMax: 0 },
                { id: 'artigo', label: 'Artigo', ativo: false, qntMax: 0 },
                { id: 'plano-negocios', label: 'Plano de Negócios', ativo: false, qntMax: 0 }
            ]
        }
    });


    //Control o array de tipos TG
    const { fields } = useFieldArray({
        control,
        //Nome do array no Schema
        name: "tiposTG"
    });

    //Observador de mudanças no array tipoTG, para saber quais inputs habilitar 
    const tiposTGWatched = useWatch({
        control,
        name: "tiposTG"
    });

    //useEffect para buscar os coodenadores assim que a tela abir
    useEffect(() => {
        const carregarCoordenadores = async () => {
            try {
                //Chama service passando cargo
                const lista = await professorService.buscaProfessoresPorCargo("coordenador");
                setCoordenadores(lista);
            } catch (erro) {
                console.error("Erro ao carregar a lista de coordenadores: ", erro)
                setResultado({
                    exibir: true,
                    variante: "danger",
                    mensagem: "Aviso: Não foi possivel carregar a lista de coordenadores"
                })
            }
        }

        carregarCoordenadores();
    }, [])

    const enviarParaBackend = async (dadosValidados) => {
        try {
            await cursoService.cadastarCurso(dadosValidados);
            console.log("Enviando para a API:", dadosValidados);
            setResultado({ exibir: true, variante: "success", mensagem: "Curso cadastrado com sucesso!" });
            reset(); // Limpa o formulário

            // Joga a tela pro topo para o usuário ver o sucesso
            window.scrollTo({ top: 0, behavior: "smooth" });

            setTimeout(() => setResultado({ exibir: false, variante: "", mensagem: "" }), 5000);
        } catch (erro) {
            console.error("Falha ao cadastrar curso: ", erro);
            setResultado({
                exibir: true,
                variante: "danger",
                mensagem: erro.message || "Erro ao cadastrar curso. Tente novamente."
            });
            window.scrollTo({ top: 0, behavior: "smooth" })
        }
    }

    return (
        <>
            <UserNavBar
                userName="Administrador"
                maxWidth="800px"
            />
            <Container className="mt-5" style={{ maxWidth: '800px' }}>
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Cadastro de Curso</h2>

                {/* Renderização do Alerta no Topo */}
                {resultado.exibir && (
                    <Alert
                        variant={resultado.variante}
                        onClose={() => setResultado({ exibir: false, variante: "", mensagem: "" })}
                        dismissible
                        className="mb-0 rounded-0 shadow-sm fw-bold text-center border-start border-end border-dark"
                    >
                        {resultado.mensagem}
                    </Alert>
                )}


                <Form
                    noValidate
                    className='form-bg border border-secondary-subtle border-top-0 p-4 rounded-bottom-4 shadow-sm'
                    id="formCurso"
                    onSubmit={handleSubmit(enviarParaBackend)}

                >
                    {/* Nome */}
                    <FormGroup className="mb-3 d-flex flex-column" controlId="formBasicName">
                        <FormLabel className='text-secondary fs-4 fw-medium'>Nome do Curso</FormLabel>
                        <FormControl
                            type="text"
                            placeholder="Digite o nome do curso"
                            {...register("nome")}
                            isInvalid={!!errors.nome}
                            className='bg-white text-black fw-normal fs-5' />
                        <Form.Control.Feedback type="invalid">{errors.nome?.message}</Form.Control.Feedback>
                    </FormGroup>
                    {/* Turno e Disciplinas */}
                    <FormGroup className="mb-3" controlId="formBasicTurnoDisciplinas">
                        <div className="row">
                            {/* Turno */}
                            <div className="col-md-6">

                                <FormLabel className='text-secondary fs-4 fw-medium d-block mb-2'>Turno</FormLabel>
                                <div className="mb-2 fw-medium fs-5">
                                    <FormCheck
                                        inline
                                        title="Manhã"
                                        label="Manhã"
                                        name="Manhã"
                                        type="checkbox"
                                        id="checkbox-manha"
                                        className=""
                                        {...register("turno")}
                                        isInvalid={!!errors.turno}
                                    />
                                    <FormCheck
                                        inline
                                        title="Tarde"
                                        label="Tarde"
                                        name="Tarde"
                                        type="checkbox"
                                        id="checkbox-tarde"
                                        {...register("turno")}
                                        isInvalid={!!errors.turno}
                                    />
                                    <FormCheck
                                        inline
                                        title="Noite"
                                        label="Noite"
                                        name="Noite"
                                        type="checkbox"
                                        id="checkbox-noite"
                                        {...register("turno")}
                                        isInvalid={!!errors.turno}
                                    />
                                </div>
                                {errors.turno && <div className="text-danger small fw-bold">{errors.turno.message}</div>}
                            </div>
                            {/* Disciplinas */}
                            <div className="col-md-6">
                                <FormLabel className='text-secondary fs-4 fw-medium d-block mb-2'>Disciplinas</FormLabel>
                                <div className="mb-3 fw-medium fs-5">
                                    <FormCheck
                                        inline
                                        title="TG1"
                                        label="TG1"
                                        name="TG1"
                                        type="checkbox"
                                        id="checkbox-tg1"
                                        {...register("disciplina")}
                                        isInvalid={!!errors.disciplina}
                                    />
                                    <FormCheck
                                        inline
                                        title="TG2"
                                        label="TG2"
                                        name="TG2"
                                        type="checkbox"
                                        id="checkbox-tg2"
                                        {...register("disciplina")}
                                        isInvalid={!!errors.disciplina}
                                    />
                                </div>
                                {errors.disciplina && <div className="text-danger small fw-bold">{errors.disciplina.message}</div>}
                            </div>
                        </div>
                    </FormGroup>

                    {/* Tipos de trab de graduacao */}
                    <FormGroup className="mb-3 d-flex flex-column" controlId="formBasicTipo">
                        <FormLabel className='text-secondary fs-4 fw-medium'>Tipo de Trabalho de Graduação</FormLabel>
                        {/* Checkboxes de tipo tg e inputs de numero de integrantes */}
                        <div className="">
                            {fields.map((opcao, index) => {
                                // Verifica se este checkbox específico está marcado para habilitar o input
                                const isAtivo = tiposTGWatched[index]?.ativo;
                                return (
                                    <div key={opcao.id} className="d-flex align-items-center gap-3 mb-3 border-bottom pb-2">
                                        <Form.Check
                                            type="checkbox"
                                            title={opcao.label}
                                            id={opcao.id}
                                            label={opcao.label.toUpperCase()}
                                            className="fw-bold"
                                            {...register(`tiposTG.${index}.ativo`)}
                                        />
                                        <div className="d-flex align-items-center gap-2 ms-auto">
                                            <FormLabel
                                                className='text-secondary fs-6 fw-bold'
                                                title={'Quantidade maxima de ' + opcao.label}
                                            >Quantidade maxima de integrantes do grupo: </FormLabel>
                                            <Form.Control
                                                type="number"
                                                title={'Quantidade maxima de ' + opcao.label}
                                                disabled={!isAtivo}
                                                className={isAtivo ? "bg-white fw-medium" : "bg-light"}
                                                style={{ maxWidth: '10rem' }}
                                                placeholder="0"
                                                {...register(`tiposTG.${index}.qntMax`)}
                                                isInvalid={isAtivo && errors.tiposTG}
                                                min="0"
                                            />
                                        </div>
                                    </div>
                                )
                            })}
                        </div>
                        {errors.tiposTG &&
                            <div className="text-danger fw-bold">
                                {errors.tiposTG.message || errors.tiposTG.root?.message}
                            </div>
                        }
                    </FormGroup>

                    {/* Selecionar Coordenador */}
                    <FormGroup className="mb-3" controlId="formCoordenador">
                        <FormLabel className='text-secondary fs-4 fw-medium'>Coordenador do Curso</FormLabel>
                        <FormSelect
                            {...register("coordenador")}
                            isInvalid={!!errors.coordenador}
                            className='bg-white text-black fw-normal fs-5'
                            // Desabilita se a lista estiver vazia (carregando ou erro)
                            disabled={coordenadores.length === 0}
                        >
                            <option value='' hidden>
                                {coordenadores.length === 0 ? "Carregando coordenadores..." : "Selecione o coordenador do curso"}
                            </option>
                            {/* Mocks
                            <option value='coord1'>Coordenador 1</option>
                            <option value='coord2'>Coordenador 2</option> */}

                            {/* Lista vinda do backend */}
                            {coordenadores.map((coord) => {
                                <option key={coord.id} value={coord.id}>
                                    {coord.nome}
                                </option>
                            })}

                        </FormSelect>
                        <Form.Control.Feedback type="invalid">{errors.coordenador?.message}</Form.Control.Feedback>
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
            </Container>
        </>
    )

}
export default CadastrarCurso;