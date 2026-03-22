package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.AlunoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.StatusAluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;

import java.util.*;

public class Aluno {
    private final AlunoId id;
    private final Nome nome;
    private final Matricula matricula;
    private ContaUsuarioId contaUsuarioId;
    private StatusAluno status;
    private List<Turma> turmas;

    private Aluno(AlunoId id, Nome nome, Matricula matricula,
                  ContaUsuarioId contaUsuarioId, StatusAluno status,
                  List<Turma> turmas) {
        this.id = assegurarPresenca(id, "ID");
        this.nome = assegurarPresenca(nome, "nome");
        this.matricula = assegurarPresenca(matricula, "matrícula");
        this.contaUsuarioId = contaUsuarioId;
        this.status = assegurarPresenca(status, "status");
        this.turmas = new ArrayList<>(assegurarPresenca(turmas, "turmas"));
    }

    // Métodos Factory ---------------------------------------------------------

    public static Aluno novo(AlunoId id, Nome nome, Matricula matricula,
                             Turma turmaInicial) {
        if(turmaInicial == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "turma inicial");
        }

        return new Aluno(id, nome, matricula, null, StatusAluno.PRE_CADASTRO,
                List.of(turmaInicial));
    }

    public static Aluno carregar(AlunoId id, Nome nome, Matricula matricula,
                                 ContaUsuarioId contaUsuarioId,
                                 StatusAluno status, List<Turma> turmas) {
        return new Aluno(id, nome, matricula, contaUsuarioId, status, turmas);
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

    public void concluirCadastro() {
        if(status != StatusAluno.AGUARDANDO_CONFIRMACAO) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "status do aluno", "AGUARDANDO_CONFIRMACAO");
        }
        this.status = StatusAluno.CADASTRADO;
    }

    // Métodos de Atualização --------------------------------------------------

    public void vincularConta(ContaUsuarioId novaContaUsuarioId) {
        this.contaUsuarioId = assegurarPresenca(novaContaUsuarioId,
                "ID da conta de usuário");

        if(status == StatusAluno.PRE_CADASTRO) {
            this.status = StatusAluno.AGUARDANDO_CONFIRMACAO;
        }
    }

    public void atualizarTurmas(List<Turma> novasTurmas) {
        this.turmas = new ArrayList<>(assegurarPresencaTurmas(novasTurmas));
    }

    // Métodos Getters ---------------------------------------------------------

    public AlunoId id() { return id; }
    public String idTexto() { return id.valor().toString(); }
    public Nome nome() { return nome; }
    public String nomeTexto() { return nome.valor(); }
    public Matricula matricula() { return matricula; }
    public String matriculaTexto() { return matricula.valor(); }
    public ContaUsuarioId contaUsuarioId() { return contaUsuarioId; }
    public StatusAluno status() { return status; }
    public List<Turma> turmas() { return Collections.unmodifiableList(turmas); }
}