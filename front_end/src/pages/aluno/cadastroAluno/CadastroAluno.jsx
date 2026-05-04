import { Container, InputGroup, Alert } from "react-bootstrap";
import Button from "react-bootstrap/Button";
import Form from "react-bootstrap/Form";
import { FormGroup } from "react-bootstrap";

import { bloquearCaracteresInputNome } from "../../../utils/utils";

// Zod e RHF (react hook form)

import { zodResolver } from "@hookform/resolvers/zod";
import { useForm, useFieldArray } from "react-hook-form";
import { alunoSchema } from "../../../schemas/utils/usuarios/usuariosZodSchema";
import { useState } from "react";
import { usuarioService } from "../../../services/usuario/usuarioService";
import { useNavigate } from "react-router-dom";

const CadastroAluno = () => {
  // Inicializa o navegador do React
  const navigate = useNavigate();
  const [resultado, setResultado] = useState({
    exibir: false,
    variante: "",
    mensagem: "",
  });

  // Inicializa react hook form
  const {
    register,
    control,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm({
    resolver: zodResolver(alunoSchema),
    defaultValues: {
      nome: "",
      matricula: "",
      email: "",
      telefone: "",
      senha: "",
      confirmarSenha: "",
    },
  });

  //Gerenciamento de arrays dinâmicos(substitui useStates manuais)
  const { fields, append, remove } = useFieldArray({
    control,
    //Nome do array no Schema
    name: "redes",
  });

  // Redes Sociais
  const handleRedeSelecionada = (e) => {
    const redeEscolhida = e.target.value;

    // Evita adicionar se não escolheu nada ou se já adicionou aquela rede (opcional)
    if (redeEscolhida) {
      append({ rede: redeEscolhida, url: "" });
      //Reseta o select
      e.target.value = "";
    }
  };

  // A função que realmente envia os dados caso passe na validação do frontend
  const enviarParaBackend = async (dadosValidados) => {
    try {
      // Cria um objeto vazio para ser o nosso Map do Java
      const redesMap = {};

      // Transforma o Array do RHF no formato de Map do Java
      if (dadosValidados.redes && dadosValidados.redes.length > 0) {
        dadosValidados.redes.forEach(item => {
          // Transforma "linkedin" em "LINKEDIN" e atribui a URL
          redesMap[item.rede.toUpperCase()] = item.url;
        });
      }

      // Monta o payload (DTO) final exatamente como o Java espera
      const payloadJava = {
        nome: dadosValidados.nome,
        matricula: dadosValidados.matricula,
        email: dadosValidados.email,
        telefone: dadosValidados.telefone,
        senha: dadosValidados.senha,
        // Envia o mapa formatado em vez do array cru do RHF
        redesSociais: redesMap
      };

      navigate("/confirmarEmail", {
        state: { emailCadastro: dadosValidados.email }
      });
      console.debug("Enviando payload para a API Java:", payloadJava);

      // Aguarda o service com o payload formatado
      await usuarioService.cadastrarUsuario(
        payloadJava,
        "aluno",
      );

      //Se chegou aqui deu tudo certo
      setResultado({
        exibir: true,
        variante: "success",
        mensagem: "Cadastro realizado! Verifique seu e-mail para ativar a conta",
      });
      reset(); // Limpa o formulário após o sucesso
    } catch (erro) {
      console.error("Falha no cadastro: ", erro);
      setResultado({
        exibir: true,
        variante: "danger",
        mensagem: erro.message || "Erro ao cadastrar. Tente novamente.",
      });
    }
  };

  return (
    <>
      <Container className="mt-5" style={{ maxWidth: "1000px" }}>
        <h2 className="bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0">
          Cadastro de Aluno
        </h2>
        <Form
          onSubmit={handleSubmit(enviarParaBackend)}
          noValidate
          className="form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm no-success-icon"
        >
          {/* Nome */}
          <Form.Group className="mb-3" controlId="formBasicName">
            <Form.Label className="text-secondary fs-4 fw-medium">
              Nome Completo
            </Form.Label>
            <Form.Control
              type="text"
              name="nome"
              placeholder="Digite seu nome completo"
              required={true}
              //Conecta input ao RHF
              {...register("nome")}
              className="bg-white text-black fw-normal fs-5"
              onKeyDown={bloquearCaracteresInputNome}
              isInvalid={!!errors.nome}
            />

            <Form.Control.Feedback type="invalid">
              {errors.nome?.message}
            </Form.Control.Feedback>
          </Form.Group>

          {/* Matrícula */}
          <FormGroup className="mb-3" controlId="formBasicMatricula">
            <Form.Label className="text-secondary fs-4 fw-medium">
              Matrícula
            </Form.Label>
            <Form.Control
              type="text"
              placeholder="Digite sua matrícula"
              className="bg-white text-black fw-normal fs-5"
              name="matricula"
              //Conecta input ao RHF
              {...register("matricula")}
              isInvalid={!!errors.matricula}
            />

            <Form.Control.Feedback type="invalid">
              {errors.matricula?.message}
            </Form.Control.Feedback>
          </FormGroup>

          {/* Email */}
          <Form.Group className="mb-3" controlId="formBasicEmail">
            <Form.Label className="text-secondary fs-4 fw-medium">
              Email
            </Form.Label>
            <Form.Control
              type="email"
              placeholder="Digite seu email"
              className="bg-white text-black fw-normal fs-5"
              name="email"
              {...register("email")}
              isInvalid={!!errors.email}
            />

            <Form.Control.Feedback type="invalid">
              {errors.email?.message}
            </Form.Control.Feedback>
          </Form.Group>

          {/* Contato */}
          <Form.Group className="mb-3">
            <Form.Label className="text-secondary fs-4 fw-medium">
              Telefone
            </Form.Label>
            <Form.Control
              type="tel"
              placeholder="11912345678"
              className="bg-white text-black fw-normal fs-5"
              pattern="[0-9]{2}-[9]{1}-[0-9]{8}"
              name="telefone"
              {...register("telefone")}
              isInvalid={!!errors.telefone}
            />

            <Form.Control.Feedback type="invalid">
              {errors.telefone?.message}
            </Form.Control.Feedback>
          </Form.Group>

          {/* Redes Sociais */}

          <Form.Group className="mb-4" controlId="formRedes">
            <Form.Label className="text-secondary fs-4 fw-medium">
              Redes Sociais
            </Form.Label>

            <Form.Select
              required={false}
              className="bg-white fw-medium fs-5 w-100 text-center mb-3"
              onChange={handleRedeSelecionada}
              defaultValue=""
            >
              <option value="" disabled>
                Selecione as redes sociais que deseja adicionar
              </option>
              <option value="linkedin">Linkedin</option>
              <option value="instagram">Instagram</option>
              <option value="facebook">Facebook</option>
            </Form.Select>

            {/*Array dinamico gerenciado pelo RHF  */}
            {fields.map((rede, index) => (
              <div key={rede.id} className="mb-2">
                <InputGroup className="w75" key={index}>
                  {/* Exibe o nome da rede com a primeira letra maiúscula */}
                  <InputGroup.Text className="text-capitalize fw-bold fs-5">
                    {rede.rede}
                  </InputGroup.Text>

                  <Form.Control
                    type="url"
                    className="fs-5 text-black"
                    placeholder={`Ex: https://${rede.rede}.com/in/seu-perfil`}
                    {...register(`redes.${index}.url`)}
                    isInvalid={!!errors.redes?.[index]?.url}
                  />
                  <Button
                    variant="outline-primary"
                    className="fs-5"
                    title="Clique aqui para remover essa rede social"
                    onClick={() => remove(index)}
                  >
                    Remover
                  </Button>
                </InputGroup>
                {/* Erro da URL da rede específica */}
                {errors.redes?.[index]?.url && (
                  <div className="text-danger mt-1 small fw-bold">
                    {errors.redes[index].url.message}
                  </div>
                )}
              </div>
            ))}
          </Form.Group>

          {/* Senha */}
          <Form.Group className="mb-4" controlId="formBasicPassword">
            <Form.Label className="text-secondary fs-4 fw-medium">
              Senha
            </Form.Label>
            <Form.Control
              type="password"
              placeholder="Digite sua senha"
              className="bg-white text-black fw-normal fs-5"
              name="senha"
              {...register("senha")}
              isInvalid={!!errors.senha}
            />

            <Form.Control.Feedback type="invalid">
              {errors.senha?.message}
            </Form.Control.Feedback>
          </Form.Group>

          {/* Confirmar Senha */}
          <FormGroup className="mb-4" controlId="formBasicConfirmPassword">
            <Form.Label className="text-secondary fs-4 fw-medium">
              Confirmar Senha
            </Form.Label>
            <Form.Control
              type="password"
              placeholder="Confirme sua senha"
              required={true}
              className="bg-white text-black fw-normal fs-5"
              name="confirmarSenha"
              {...register("confirmarSenha")}
              isInvalid={!!errors.confirmarSenha}
            />
            <Form.Control.Feedback type="invalid">
              {errors.confirmarSenha?.message}
            </Form.Control.Feedback>
          </FormGroup>

          {/* Botão de Cadastrar */}
          <FormGroup className="text-center">
            <Button
              variant="primary"
              type="submit"
              id="btn-cadastro"
              className="mb-2 fs-4 fw-medium w-100"
            >
              Cadastrar
            </Button>
          </FormGroup>
        </Form>
        {resultado.exibir && (
          <Alert
            variant={resultado.variante}
            onClose={() =>
              setResultado({ exibir: false, variante: "", mensagem: "" })
            }
            dismissible
            className="mt-3 shadow-sm fw-bold text-center"
          >
            {resultado.mensagem}
          </Alert>
        )}
      </Container>
    </>
  );
};

export default CadastroAluno;
