package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.identificadores.Coorientador;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.TemaTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;

import java.util.*;
import java.util.stream.Collectors;

public class GrupoTg {
    private final GrupoTgId id;
    private Professor orientador;
    private Coorientador coorientador;
    private final Curso curso;
    private Disciplina disciplina;
    private TemaTg temaTg;
    private TipoTg tipoTg;
    private List<Aluno> alunos;

    private GrupoTg(GrupoTgId id, Professor orientador,
                    Coorientador coorientador, Curso curso,
                    Disciplina disciplina, TemaTg temaTg, TipoTg tipoTg,
                    List<Aluno> alunos) {
        this.id = assegurarPresenca(id, "ID");
        this.orientador = orientador;
        this.coorientador = coorientador;
        this.curso = assegurarPresenca(curso, "curso");
        this.disciplina = assegurarPresenca(disciplina, "disciplina");
        this.temaTg = assegurarPresenca(temaTg, "tema de TG");
        this.tipoTg = assegurarPresenca(tipoTg, "tipo de TG");
        this.alunos = new ArrayList<>(validarAlunos(alunos));
    }

    // Métodos Factory ---------------------------------------------------------

    public static GrupoTg novo(GrupoTgId id, Curso curso, Disciplina disciplina,
                               TemaTg temaTg, TipoTg tipoTg,
                               List<Aluno> alunos) {
        return new GrupoTg(id, null, null, curso, disciplina, temaTg, tipoTg,
                alunos);
    }

    public static GrupoTg carregar(GrupoTgId id, Professor orientador,
                                   Coorientador coorientador, Curso curso,
                                   Disciplina disciplina, TemaTg temaTg,
                                   TipoTg tipoTg, List<Aluno> alunos) {
        return new GrupoTg(id, orientador, coorientador, curso, disciplina,
                temaTg, tipoTg, alunos);
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

        Set<Disciplina> disciplinasMolde = alunos.getFirst().turmas().stream()
                .map(Turma::disciplina)
                .collect(Collectors.toSet());

        for(Aluno aluno : alunos) {
            Set<Disciplina> disciplinasDesteAluno = aluno.turmas().stream()
                    .map(Turma::disciplina)
                    .collect(Collectors.toSet());

            if(!disciplinasDesteAluno.equals(disciplinasMolde)) {
                throw new RegraNegocioExcecao(
                        CodigoErro.RN_003_CONDICAO_ACAO_NAO_ATENDIDA,
                        "alunos do grupo", "estar matriculado exatamente na " +
                        "mesma composição de disciplinas");
            }
        }

        if(!curso.validarQtdAlunosGrupo(tipoTg, alunos.size())) {
            throw new RegraNegocioExcecao(CodigoErro
                    .RN_004_LIMITE_ALUNOS_EXCEDIDO, alunos.size(),
                    tipoTg.name(),
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

    public void atualizarTemaTg(TemaTg novoTemaTg) {
        this.temaTg = assegurarPresenca(novoTemaTg, "tema de TG");
    }

    public void atualizarTipoTg(TipoTg novoTipoTg) {
        this.tipoTg = assegurarPresenca(novoTipoTg, "tipo de TG");
    }

    public void atualizarAlunos(List<Aluno> novosAlunos) {
        this.alunos = new ArrayList<>(validarAlunos(novosAlunos));
    }

    // Métodos Getters ---------------------------------------------------------

    public GrupoTgId id() { return id; }
    public String idTexto() { return id.valor().toString(); }
    public Professor orientador() { return orientador; }
    public Coorientador coorientador() { return coorientador; }
    public Curso curso() { return curso; }
    public Disciplina disciplina() { return disciplina; }
    public TemaTg temaTg() { return temaTg; }
    public TipoTg tipoTg() { return tipoTg; }
    public List<Aluno> alunos() { return Collections.unmodifiableList(alunos); }
}