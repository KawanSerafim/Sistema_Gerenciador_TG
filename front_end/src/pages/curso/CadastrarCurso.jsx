import { useEffect, useState } from "react";
import {
  Container,
  Form,
  FormCheck,
  FormControl,
  FormGroup,
  FormLabel,
  FormSelect,
  Button,
  Toast, ToastContainer,
} from "react-bootstrap";
import "./cadastrarCurso.css";
import UserNavBar from "../../components/usernavbar/UserNavBar";

// Zod e RHF para validações
import { useFieldArray, useForm, useWatch } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { cursoSchema } from "../../schemas/curso/cadastrarCursoZodSchema";
import { professorService } from "../../services/professor/professorService";
import { cursoService } from "../../services/curso/cursoService";

const CadastrarCurso = () => {
  // Estado para exibição do resultado de requisições
  const [resultado, setResultado] = useState({
    exibir: false,
    variante: "",
    mensagem: "",
  });
  // =========== ESTADOS =====================
  // Estados para as listas dinâmicas
  const [turnosDisponiveis, setTurnosDisponiveis] = useState([]);
  const [disciplinasDisponiveis, setDisciplinasDisponiveis] = useState([]);
  // Estado para armazenar os coordenadores vindos do backend
  const [coordenadores, setCoordenadores] = useState([]);

  const {
    register,
    handleSubmit,
    control,
    reset,
    getValues,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(cursoSchema),
    defaultValues: {
      nome: "",
      turno: [],
      disciplina: [],
      coordenador: "",
      tiposTG: [],
    },
  });

  //Control o array de tipos TG
  const { fields, replace } = useFieldArray({
    control,
    //Nome do array no Schema
    name: "tiposTG",
  });

  //Observador de mudanças no array tipoTG, para saber quais inputs habilitar
  const tiposTGWatched = useWatch({
    control,
    name: "tiposTG",
  });

  // =============== CARREGAMENTO DE DADOS ===============
  useEffect(() => {
    const inicializarDados = async () => {
      try {
        //Busca tudo em paralelo
        const [listaCoords, listaTurnos, listaDiscip, listaTipos] =
          await Promise.all([
            professorService.buscaProfessoresPorCargo("COORDENADOR_CURSO"),
            cursoService.buscaTurnos(),
            cursoService.buscaDisciplinas(),
            cursoService.buscaTiposTg()
          ]);

        setCoordenadores(listaCoords);
        setTurnosDisponiveis(listaTurnos);
        setDisciplinasDisponiveis(listaDiscip);

        //Popula o fieldArray do zod com os tipos vindos do backend
        const tgsIniciais = listaTipos.map(tipo => ({
          id: tipo, // O ID técnico (ex: "DESENVOLVIMENTO_SOFTWARE")
          label: tipo,
          ativo: false,
          qntMax: 0
        }));
        replace(tgsIniciais);

      } catch (erro) {
        console.error("Erro na carga inicial:", erro);
        setResultado({
          exibir: true,
          variante: "danger",
          mensagem: "Erro ao carregar dados do servidor. Verifique sua conexão.",
        });
      };
    };
    inicializarDados();
  }, [replace]);

  const enviarParaBackend = async (dadosValidados) => {
    try {
      await cursoService.cadastarCurso(dadosValidados);
      console.log("Enviando para a API:", dadosValidados);
      setResultado({
        exibir: true,
        variante: "success",
        mensagem: "Curso cadastrado com sucesso!",
      });

      const tgsAtuais = getValues("tiposTG");

      reset({
        nome: "",
        turno: [],
        disciplina: [],
        coordenador: "",
        // Mapeia a lista atual, forçando tudo a ficar desmarcado e zerado
        tiposTG: tgsAtuais.map((tg) => ({
          ...tg,
          ativo: false,
          qntMax: 0
        }))
      })

    } catch (erro) {
      console.error("Falha ao cadastrar curso: ", erro);
      setResultado({
        exibir: true,
        variante: "danger",
        mensagem: erro.message || "Erro ao cadastrar curso. Tente novamente.",
      });
    }
  };

  return (
    <>
      <UserNavBar userName="Administrador" maxWidth="800px" />
      <Container className="mt-5" style={{ maxWidth: "800px" }}>
        <h2 className="bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0">
          Cadastro de Curso
        </h2>

        <Form
          noValidate
          className="form-bg border border-secondary-subtle border-top-0 p-4 rounded-bottom-4 shadow-sm"
          id="formCurso"
          onSubmit={handleSubmit(enviarParaBackend)}
        >
          {/* Nome */}
          <FormGroup
            className="mb-3 d-flex flex-column"
          >
            <FormLabel className="text-secondary fs-4 fw-medium">
              Nome do Curso
            </FormLabel>
            <FormControl
              type="text"
              placeholder="Digite o nome do curso"
              {...register("nome")}
              isInvalid={!!errors.nome}
              className="bg-white text-black fw-normal fs-5"
            />
            <Form.Control.Feedback type="invalid">
              {errors.nome?.message}
            </Form.Control.Feedback>
          </FormGroup>
          {/* Turno e Disciplinas */}
          <FormGroup className="mb-3">
            <div className="row">
              {/* Turno */}
              <div className="col-md-6">
                <FormLabel className="text-secondary fs-4 fw-medium d-block mb-2">
                  Turno
                </FormLabel>
                {turnosDisponiveis.map(t => (
                  <FormCheck
                    key={t} inline label={t}
                    value={t} type="checkbox"
                    {...register("turno")} isInvalid={!!errors.turno}
                    id={t} className="fw-bold"
                  />
                ))}
                {errors.turno && (
                  <div className="text-danger small fw-bold">
                    {errors.turno.message}
                  </div>
                )}
              </div>
              {/* Disciplinas */}
              <div className="col-md-6">
                <FormLabel className="text-secondary fs-4 fw-medium d-block mb-2">
                  Disciplinas
                </FormLabel>
                {disciplinasDisponiveis.map(d => (
                  <FormCheck
                    key={d} inline
                    label={d} value={d}
                    type="checkbox" {...register("disciplina")}
                    isInvalid={!!errors.disciplina}
                    id={d} className="fw-bold" />
                ))}
                {errors.disciplina && (
                  <div className="text-danger small fw-bold">
                    {errors.disciplina.message}
                  </div>
                )}
              </div>
            </div>
          </FormGroup>

          {/* Tipos de trab de graduacao */}
          <FormGroup
            className="mb-3 d-flex flex-column"
          >
            <FormLabel className="text-secondary fs-4 fw-medium">
              Tipo de Trabalho de Graduação
            </FormLabel>
            {/* Checkboxes de tipo tg e inputs de numero de integrantes */}
            <div className="">
              {fields.map((opcao, index) => {
                // Verifica se este checkbox específico está marcado para habilitar o input
                const isAtivo = tiposTGWatched[index]?.ativo;
                const idCheckbox = `checkbox-tipo-tg-${index}`;
                const idInputQtd = `input-qtd-tg-${index}`;

                return (
                  <div
                    key={opcao.id}
                    className="d-flex align-items-center gap-3 mb-3 border-bottom pb-2"
                  >
                    <Form.Check
                      type="checkbox"
                      // usa o replace para transformar em algo legível para o label
                      title={opcao.label.replace(/_/g, ' ')}
                      id={idCheckbox}
                      label={opcao.label.replace(/_/g, ' ').toUpperCase()}
                      className="fw-bold"
                      {...register(`tiposTG.${index}.ativo`)}
                    />
                    <div className="d-flex align-items-center gap-2 ms-auto">
                      <FormLabel
                        className="text-secondary fs-6 fw-bold"
                        title={"Quantidade maxima de " + opcao.label.replace(/_/g, ' ')}
                        htmlFor={idInputQtd}
                      >
                        Quantidade maxima de integrantes do grupo:{" "}
                      </FormLabel>
                      <Form.Control
                        type="number"
                        id={idInputQtd}
                        title={"Quantidade maxima de " + opcao.label.replace(/_/g, ' ')}
                        disabled={!isAtivo}
                        className={isAtivo ? "bg-white fw-medium" : "bg-light"}
                        style={{ maxWidth: "10rem" }}
                        placeholder="0"
                        {...register(`tiposTG.${index}.qntMax`, { valueAsNumber: true })}
                        isInvalid={isAtivo && !!errors.tiposTG?.[index]?.qntMax}
                        min="1"
                      />
                      <Form.Control.Feedback type="invalid">
                        {errors.tiposTG?.[index]?.qntMax?.message}
                      </Form.Control.Feedback>
                    </div>
                  </div>
                );
              })}
            </div>
            {errors.tiposTG?.root && (
              <div className="text-danger fw-bold">
                {errors.tiposTG.root?.message}
              </div>
            )}
          </FormGroup>

          {/* Selecionar Coordenador */}
          <FormGroup className="mb-3" controlId="formCoordenador">
            <FormLabel className="text-secondary fs-4 fw-medium">
              Coordenador do Curso
            </FormLabel>
            <FormSelect
              {...register("coordenador")}
              isInvalid={!!errors.coordenador}
              className="bg-white text-black fw-normal fs-5"
              // Desabilita se a lista estiver vazia (carregando ou erro)
              disabled={coordenadores.length === 0}
            >
              <option value="">Selecione um coordenador</option>

              {/* Lista vinda do backend */}
              {coordenadores.map((coord) => (
                <option key={coord.id} value={coord.matricula}>
                  {coord.nome}
                </option>
              ))
              }
            </FormSelect>
            <Form.Control.Feedback type="invalid">
              {errors.coordenador?.message}
            </Form.Control.Feedback>
          </FormGroup>

          {/* Botão de Cadastrar */}
          <FormGroup className="text-center">
            <Button
              variant="primary"
              type="submit"
              id="btn-cadastro"
              className="mb-2 fs-5 fw-medium w-100"
            >
              Cadastrar
            </Button>
          </FormGroup>

          {resultado.exibir && (
            // Toast flutuante no topo direito
            <ToastContainer
              position="top-end"
              className="p-3"
              style={{ position: "fixed", zIndex: 9999 }}
            >
              <Toast
                show={resultado.exibir}
                onClose={() => setResultado({ exibir: false, variante: "", mensagem: "" })}
                bg={resultado.variante} // Cor vem do variante
              //autohide retirado para garantir que usuario venha retirar
              >
                <Toast.Header>
                  <strong className="me-auto text-dark">
                    {resultado.variante === "danger" ? "Atenção" : "Sucesso"}
                  </strong>
                </Toast.Header>
                <Toast.Body className="text-white fw-bold fs-6">
                  {resultado.mensagem}
                </Toast.Body>
              </Toast>
            </ToastContainer>
          )}
        </Form>
      </Container>
    </>
  );
};
export default CadastrarCurso;
