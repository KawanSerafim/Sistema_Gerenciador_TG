package br.edu.com.fateczl.sistema.gerenciador.tcc.grupotcc.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tcc.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.identificadores.Coorientador;
import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tcc.curso.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tcc.curso.objetosvalor.TipoTcc;
import br.edu.com.fateczl.sistema.gerenciador.tcc.grupotcc.objetosvalor.GrupoTccId;
import br.edu.com.fateczl.sistema.gerenciador.tcc.grupotcc.objetosvalor.TemaTcc;
import br.edu.com.fateczl.sistema.gerenciador.tcc.professor.entidade.Professor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GrupoTcc {
    private final GrupoTccId id;
    private Professor orientador;
    private Coorientador coorientador;
    private final Curso curso;
    private Disciplina disciplina;
    private TemaTcc temaTcc;
    private TipoTcc tipoTcc;
    private List<Aluno> alunos;

    private GrupoTcc(GrupoTccId id, Professor orientador,
                     Coorientador coorientador, Curso curso,
                     Disciplina disciplina, TemaTcc temaTcc, TipoTcc tipoTcc,
                     List<Aluno> alunos) {
        this.id = id;
        this.orientador = orientador;
        this.coorientador = coorientador;
        this.curso = assegurarPresenca(curso, "curso");
        this.disciplina = assegurarPresenca(disciplina, "disciplina");
        this.temaTcc = assegurarPresenca(temaTcc, "tema de TCC");
        this.tipoTcc = assegurarPresenca(tipoTcc, "tipo de TCC");
        this.alunos = new ArrayList<>(validarAlunos(alunos));
    }

    // Métodos Factory ---------------------------------------------------------

    public static GrupoTcc novo(Curso curso, Disciplina disciplina,
                                TemaTcc temaTcc, TipoTcc tipoTcc,
                                List<Aluno> alunos) {
        return new GrupoTcc(null, null, null, curso, disciplina, temaTcc,
                tipoTcc, alunos);
    }

    public static GrupoTcc carregar(GrupoTccId id, Professor orientador,
                                    Coorientador coorientador, Curso curso,
                                    Disciplina disciplina, TemaTcc temaTcc,
                                    TipoTcc tipoTcc, List<Aluno> alunos) {
        if(id == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "ID do grupo");
        }
        return new GrupoTcc(id, orientador, coorientador, curso, disciplina,
                temaTcc, tipoTcc, alunos);
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

    public void atualizarTemaTcc(TemaTcc novoTemaTcc) {
        this.temaTcc = assegurarPresenca(novoTemaTcc, "tema de TCC");
    }

    public void atualizarTipoTcc(TipoTcc novoTipoTcc) {
        this.tipoTcc = assegurarPresenca(novoTipoTcc, "tipo de TCC");
    }

    public void atualizarAlunos(List<Aluno> novosAlunos) {
        this.alunos = new ArrayList<>(validarAlunos(novosAlunos));
    }

    // Métodos Getters ---------------------------------------------------------

    public GrupoTccId id() { return id; }
    public Professor orientador() { return orientador; }
    public Coorientador coorientador() { return coorientador; }
    public Curso curso() { return curso; }
    public Disciplina disciplina() { return disciplina; }
    public TemaTcc temaTcc() { return temaTcc; }
    public TipoTcc tipoTcc() { return tipoTcc; }
    public List<Aluno> alunos() { return Collections.unmodifiableList(alunos); }
}