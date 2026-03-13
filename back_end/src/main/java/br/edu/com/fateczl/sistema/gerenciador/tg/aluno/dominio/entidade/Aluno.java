package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.AlunoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.StatusAluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.StatusContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Aluno {
    private final AlunoId id;
    private final Nome nome;
    private final Matricula matricula;
    private ContaUsuario contaUsuario;
    private StatusAluno status;
    private List<Turma> turmas;

    private Aluno(AlunoId id, Nome nome, Matricula matricula,
                  ContaUsuario contaUsuario, StatusAluno status,
                  List<Turma> turmas) {
        this.id = id;
        this.nome = assegurarPresenca(nome, "nome");
        this.matricula = assegurarPresenca(matricula, "matrícula");
        this.contaUsuario = contaUsuario;
        this.status = assegurarPresenca(status, "status");
        this.turmas = new ArrayList<>(assegurarPresenca(turmas, "turmas"));
    }

    // Métodos Factory ---------------------------------------------------------

    public static Aluno novo(Nome nome, Matricula matricula,
                             Turma turmaInicial) {
        if(turmaInicial == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "turma inicial");
        }

        return new Aluno(null, nome, matricula, null,
                StatusAluno.PRE_CADASTRO, List.of(turmaInicial));
    }

    public static Aluno carregar(AlunoId id, Nome nome, Matricula matricula,
                                 ContaUsuario contaUsuario,
                                 StatusAluno status, List<Turma> turmas) {
        if(id == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "ID do aluno");
        }

        return new Aluno(id, nome, matricula, contaUsuario, status, turmas);
    }

    // Métodos especiais -------------------------------------------------------

    private <T> T assegurarPresenca(T objeto, String campo) {
        if(objeto == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    campo);
        }
        return objeto;
    }

    private List<Turma> assegurarPresencaTurmas(List<Turma> turmas) {
        if(turmas == null || turmas.isEmpty()) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "turmas");
        }
        return turmas;
    }

    public void matricularEmTurma(Turma novaTurma) {
        assegurarPresenca(novaTurma, "turma");

        boolean jaMatriculado = this.turmas.stream()
                .anyMatch(turmaExistente -> Objects
                        .equals(turmaExistente.id(), novaTurma.id()));

        if(jaMatriculado) {
            throw new RegraNegocioExcecao(CodigoErro.RN_002_REGISTRO_DUPLICADO,
                    "turma");
        }
        this.turmas.add(novaTurma);
    }

    public void finalizarCadastro(ContaUsuario contaUsuario) {
        if(contaUsuario.status() != StatusContaUsuario.EMAIL_CONFIRMADO) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "status da conta de usuário", "email confirmado");
        }
        atualizarContaUsuario(contaUsuario);
        contaUsuario.atualizarStatus(StatusContaUsuario.ATIVO);
        atualizarStatus(StatusAluno.CADASTRADO);
    }

    // Métodos de Atualização --------------------------------------------------

    public void atualizarStatus(StatusAluno novoStatus) {
        this.status = assegurarPresenca(novoStatus, "status");
    }

    public void atualizarContaUsuario(ContaUsuario novaContaUsuario) {
        this.contaUsuario = assegurarPresenca(novaContaUsuario,
                "conta de usuário");
    }

    public void atualizarTurmas(List<Turma> novasTurmas) {
        this.turmas = new ArrayList<>(assegurarPresencaTurmas(novasTurmas));
    }

    // Métodos Getters de Delegação --------------------------------------------

    public Email emailContaUsuario() {
        return (contaUsuario != null) ? contaUsuario.email() : null;
    }

    public StatusContaUsuario statusContaUsuario() {
        return (contaUsuario != null) ? contaUsuario.status() : null;
    }

    // Métodos Getters ---------------------------------------------------------

    public AlunoId id() { return id; }
    public Nome nome() { return nome; }
    public Matricula matricula() { return matricula; }
    public ContaUsuario contaUsuario() { return contaUsuario; }
    public StatusAluno status() { return status; }
    public List<Turma> turmas() { return Collections.unmodifiableList(turmas); }
}