import { useState } from "react";
import { Container, Button, Form, Alert } from "react-bootstrap";
import { bloquearCaracteresInputNome } from "../../../utils/utils";

// 1. Importações do RHF e Zod
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { professorSchema } from "../../../schemas/usuarioSchema";
import { usuarioService } from "../../../service/usuario/usuarioService";

const CadastroProfessor = () => {
  const [resultado, setResultado] = useState({
    show: false,
    variant: "",
    message: "",
  });

  // 3. Configuração do Hook
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(professorSchema),
    defaultValues: {
      nome: "",
      matricula: "",
      email: "",
      senha: "",
      confirmarSenha: "",
      cargo: "",
    },
  });

  // 4. Função de envio (Só roda se o formulário estiver 100% válido)
  const enviarParaBackend = async (dadosValidados) => {
    try {
      //Aguarda o service com o uso do Interceptador
      const resposta = await usuarioService.cadastrarUsuario(
        dadosValidados,
        "professor",
      );
      console.log("Enviando payload para a API Java:", dadosValidados);
      //Se chegou aqui deu tudo certo
      setResultado({
        show: true,
        variant: "success",
        message: "Cadastro realizado! Verifique seu e-mail para ativar a conta",
      });

      // Joga a tela pra baixo suavemente para o usuário ler o sucesso
      window.scrollTo(0, document.body.scrollHeight);

      reset(); // Limpa o formulário após o sucesso
      //Some a mensagem depois de um tempo
      setTimeout(
        () => setResultado({ show: false, variant: "", message: "" }),
        5000,
      );
    } catch (erro) {
      console.error("Falha no cadastro: ", erro);
      setResultado({
        show: true,
        variant: "danger",
        message: erro.message || "Erro ao cadastrar. Tente novamente.",
      });
      // Joga a tela pra baixo suavemente para o usuário ler o sucesso
      window.scrollTo(0, document.body.scrollHeight);
    }
  };

  return (
    <Container className="mt-5 mb-5" style={{ maxWidth: "800px" }}>
      <h2 className="bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0">
        Cadastro de Professor
      </h2>

      <Form
        noValidate
        onSubmit={handleSubmit(enviarParaBackend)}
        className="form-bg border border-dark border-top-0 p-4 rounded-bottom-4 shadow-sm"
      >
        {/* Nome */}
        <Form.Group className="mb-3" controlId="formBasicName">
          <Form.Label className="text-secondary fs-4 fw-medium">
            Nome Completo
          </Form.Label>
          <Form.Control
            type="text"
            placeholder="Digite seu nome completo"
            className="bg-white text-black fw-normal fs-5"
            {...register("nome")}
            onKeyDown={bloquearCaracteresInputNome}
            isInvalid={!!errors.nome}
          />
          <Form.Control.Feedback type="invalid">
            {errors.nome?.message}
          </Form.Control.Feedback>
        </Form.Group>

        {/* Matrícula */}
        <Form.Group className="mb-3" controlId="formBasicMatricula">
          <Form.Label className="text-secondary fs-4 fw-medium">
            Matrícula
          </Form.Label>
          <Form.Control
            type="text"
            placeholder="Digite sua matrícula (11 dígitos)"
            className="bg-white text-black fw-normal fs-5"
            {...register("matricula")}
            isInvalid={!!errors.matricula}
          />
          <Form.Control.Feedback type="invalid">
            {errors.matricula?.message}
          </Form.Control.Feedback>
        </Form.Group>

        {/* Email */}
        <Form.Group className="mb-3" controlId="formBasicEmail">
          <Form.Label className="text-secondary fs-4 fw-medium">
            Email
          </Form.Label>
          <Form.Control
            type="email"
            placeholder="Digite seu email"
            className="bg-white text-black fw-normal fs-5"
            {...register("email")}
            isInvalid={!!errors.email}
          />
          <Form.Control.Feedback type="invalid">
            {errors.email?.message}
          </Form.Control.Feedback>
        </Form.Group>

        {/* Senha */}
        <Form.Group className="mb-3" controlId="formBasicPassword">
          <Form.Label className="text-secondary fs-4 fw-medium">
            Senha
          </Form.Label>
          <Form.Control
            type="password"
            placeholder="Digite sua senha"
            className="bg-white text-black fw-normal fs-5"
            {...register("senha")}
            isInvalid={!!errors.senha}
          />
          <Form.Control.Feedback type="invalid">
            {errors.senha?.message}
          </Form.Control.Feedback>
        </Form.Group>

        {/* Confirmar Senha */}
        <Form.Group className="mb-3" controlId="formBasicConfirmPassword">
          <Form.Label className="text-secondary fs-4 fw-medium">
            Confirmar Senha
          </Form.Label>
          <Form.Control
            type="password"
            placeholder="Confirme sua senha"
            className="bg-white text-black fw-normal fs-5"
            {...register("confirmarSenha")}
            isInvalid={!!errors.confirmarSenha}
          />
          <Form.Control.Feedback type="invalid">
            {errors.confirmarSenha?.message}
          </Form.Control.Feedback>
        </Form.Group>

        {/* Selecionar Cargo */}
        <Form.Group className="mb-4" controlId="formBasicRole">
          <Form.Label className="text-secondary fs-4 fw-medium">
            Cargo
          </Form.Label>
          <Form.Select
            className="bg-white text-black fw-normal fs-5"
            {...register("cargo")}
            isInvalid={!!errors.cargo}
          >
            <option value="">Selecione seu cargo</option>
            <option value="professor">Professor de TG</option>
            <option value="coordenador">Coordenador</option>
            <option value="orientador">Orientador</option>
          </Form.Select>
          <Form.Control.Feedback type="invalid">
            {errors.cargo?.message}
          </Form.Control.Feedback>
        </Form.Group>

        {/* Botão de Cadastrar */}
        <div className="text-center">
          <Button
            variant="primary"
            type="submit"
            className="mb-2 fs-4 fw-medium w-100 py-2"
          >
            Cadastrar
          </Button>
        </div>
      </Form>

      {resultado.show && (
        <Alert
          variant={resultado.variant}
          onClose={() =>
            setResultado({ show: false, variant: "", message: "" })
          }
          dismissible
          className="mt-3 shadow-sm fw-bold text-center"
        >
          {resultado.message}
        </Alert>
      )}
    </Container>
  );
};

export default CadastroProfessor;
