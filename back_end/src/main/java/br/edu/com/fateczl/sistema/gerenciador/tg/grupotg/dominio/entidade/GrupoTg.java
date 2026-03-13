package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.identificadores.Coorientador;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.objetosvalor.TipoTcc;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.TemaTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.entidade.Professor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GrupoTg {
    private final GrupoTgId id;
    private Professor orientador;
    private Coorientador coorientador;
    private final Curso curso;
    private Disciplina disciplina;
    private TemaTg temaTg;
    private TipoTcc tipoTcc;
    private List<Aluno> alunos;

    private GrupoTg(GrupoTgId id, Professor orientador,
                    Coorientador coorientador, Curso curso,
                    Disciplina disciplina, TemaTg temaTg, TipoTcc tipoTcc,
                    List<Aluno> alunos) {
        this.id = id;
        this.orientador = orientador;
        this.coorientador = coorientador;
        this.curso = assegurarPresenca(curso, "curso");
        this.disciplina = assegurarPresenca(disciplina, "disciplina");
        this.temaTg = assegurarPresenca(temaTg, "tema de TCC");
        this.tipoTcc = assegurarPresenca(tipoTcc, "tipo de TCC");
        this.alunos = new ArrayList<>(validarAlunos(alunos));
    }

    // Métodos Factory ---------------------------------------------------------

    public static GrupoTg novo(Curso curso, Disciplina disciplina,
                               TemaTg temaTg, TipoTcc tipoTcc,
                               List<Aluno> alunos) {
        return new GrupoTg(null, null, null, curso, disciplina, temaTg,
                tipoTcc, alunos);
    }

    public static GrupoTg carregar(GrupoTgId id, Professor orientador,
                                   Coorientador coorientador, Curso curso,
                                   Disciplina disciplina, TemaTg temaTg,
                                   TipoTcc tipoTcc, List<Aluno> alunos) {
        if(id == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "ID do grupo");
        }
        return new GrupoTg(id, orientador, coorientador, curso, disciplina,
                temaTg, tipoTcc, alunos);
    }

    // Métodos especiais -------------------------------------------------------

    private <T> T assegurarPresenca(T objeto, String campo) {
        if(objeto == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    campo);
        }
        return objeto;
    }

    private List<Aluno> validarAlunos(List<Aluno> alunos) {
        if(alunos == null || alunos.isEmpty()) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "alunos");
        }

        // TODO: Caso seja necessário limitar o aluno a estar numa turma de
        //  mesma disciplina do grupo, fazer a devida validação.

        if(!curso.validarQtdAlunosGrupo(tipoTcc, alunos.size())) {
            throw new RegraNegocioExcecao(CodigoErro
                    .RN_004_LIMITE_ALUNOS_EXCEDIDO, alunos.size(),
                    tipoTcc.name(),
                    curso.nomeTexto());
        }
        return alunos;
    }

    public void vincularOrientador(Professor professor) {
        assegurarPresenca(professor, "orientador");

        if(!professor.podeSerOrientador()) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_003_CONDICAO_ACAO_NAO_ATENDIDA, "professor",
                    "para ser um orientador");
        }
        this.orientador = professor;
    }

    public void vincularCoorientador(Coorientador coorientador) {
        if(coorientador instanceof Professor professor
                && !professor.podeSerOrientador()) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_003_CONDICAO_ACAO_NAO_ATENDIDA,
                    "coorientador",
                    "para ser um orientador");
        }
        this.coorientador = coorientador;
    }

    // Métodos de Atualização --------------------------------------------------

    public void atualizarDisciplina(Disciplina novaDisciplina) {
        this.disciplina = assegurarPresenca(novaDisciplina, "disciplina");
    }

    public void atualizarTemaTcc(TemaTg novoTemaTg) {
        this.temaTg = assegurarPresenca(novoTemaTg, "tema de TCC");
    }

    public void atualizarTipoTcc(TipoTcc novoTipoTcc) {
        this.tipoTcc = assegurarPresenca(novoTipoTcc, "tipo de TCC");
    }

    public void atualizarAlunos(List<Aluno> novosAlunos) {
        this.alunos = new ArrayList<>(validarAlunos(novosAlunos));
    }

    // Métodos Getters ---------------------------------------------------------

    public GrupoTgId id() { return id; }
    public Professor orientador() { return orientador; }
    public Coorientador coorientador() { return coorientador; }
    public Curso curso() { return curso; }
    public Disciplina disciplina() { return disciplina; }
    public TemaTg temaTcc() { return temaTg; }
    public TipoTcc tipoTcc() { return tipoTcc; }
    public List<Aluno> alunos() { return Collections.unmodifiableList(alunos); }
}