package br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.CriptografoSenhas;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Senha;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.CargoProfessor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;

public class CadastrarProfessorCaso {
    private final ProfessorRepositorio professorRepositorio;
    private final ContaUsuarioRepositorio contaUsuarioRepositorio;
    private final CriptografoSenhas criptografo;

    public CadastrarProfessorCaso(ProfessorRepositorio professorRepositorio,
                                  ContaUsuarioRepositorio contaUsuarioRepositorio,
                                  CriptografoSenhas criptografo) {
        this.professorRepositorio = professorRepositorio;
        this.contaUsuarioRepositorio = contaUsuarioRepositorio;
        this.criptografo = criptografo;
    }

    public record Comando(
            String nome,
            String matricula,
            String email,
            String senhaLimpa,
            CargoProfessor cargo
    ) {}

    public record Resposta(
            String id,
            String nome,
            String matricula,
            CargoProfessor cargo
    ) {}

    public Resposta executar(Comando comando) {
        Email emailAlvo = new Email(comando.email());
        Matricula matriculaAlvo = new Matricula(comando.matricula());
        Nome nome = new Nome(comando.nome());

        validarEmail(emailAlvo);
        validarMatricula(matriculaAlvo);

        ContaUsuario novaConta = gerarNovaConta(emailAlvo,
                comando.senhaLimpa());
        Professor novoProfessor = gerarNovoProfessor(nome, matriculaAlvo,
                novaConta, comando.cargo());

        contaUsuarioRepositorio.salvar(novaConta);
        professorRepositorio.salvar(novoProfessor);

        return new Resposta(novoProfessor.idTexto(), nome.valor(),
                matriculaAlvo.valor(), comando.cargo());
    }

    private void validarEmail(Email email) {
        contaUsuarioRepositorio.buscarPorEmail(email).ifPresent(conta -> {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_002_REGISTRO_DUPLICADO, "email");
        });
    }

    private void validarMatricula(Matricula matricula) {
        professorRepositorio.buscarPorMatricula(matricula)
                .ifPresent(professor -> {
                    throw new RegraNegocioExcecao(
                            CodigoErro.RN_002_REGISTRO_DUPLICADO, "matricula"
                    );
                });
    }

    private ContaUsuario gerarNovaConta(Email email, String senhaLimpa) {
        Senha senhaCriptografada = criptografo.criptografar(senhaLimpa);
        return ContaUsuario.novo(email, senhaCriptografada);
    }

    private Professor gerarNovoProfessor(Nome nome, Matricula matricula,
                                         ContaUsuario conta,
                                         CargoProfessor cargo) {
        return Professor.novo(nome, matricula, conta, cargo);
    }
}