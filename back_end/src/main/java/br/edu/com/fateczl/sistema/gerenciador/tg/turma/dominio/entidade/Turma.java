package br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.PeriodoLetivo;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;

public class Turma {
    private final TurmaId id;
    private final Curso curso;
    private final Disciplina disciplina;
    private final Turno turno;
    private final PeriodoLetivo periodoLetivo;
    private Professor professorTg;

    private Turma(TurmaId id, Curso curso, Disciplina disciplina, Turno turno,
                  PeriodoLetivo periodoLetivo, Professor professorTg) {
        this.id = assegurarPresenca(id, "ID");
        this.curso = assegurarPresenca(curso, "curso");
        this.disciplina = validarDisciplina(disciplina, curso);
        this.turno = validarTurno(turno, curso);
        this.periodoLetivo = assegurarPresenca(periodoLetivo, "período letivo");
        this.professorTg = validarProfessorTg(professorTg);
    }

    // Métodos Factory ---------------------------------------------------------

    public static Turma novo(TurmaId id, Curso curso, Disciplina disciplina,
                             Turno turno,
                             PeriodoLetivo periodoLetivo,
                             Professor professorTg) {
        return new Turma(id, curso, disciplina, turno, periodoLetivo,
                professorTg);
    }

    public static Turma carregar(TurmaId id, Curso curso, Disciplina disciplina,
                                 Turno turno, PeriodoLetivo periodoLetivo,
                                 Professor professorTg) {
        return new Turma(id, curso, disciplina, turno, periodoLetivo,
                professorTg);
    }

    // Métodos especiais -------------------------------------------------------

    private <T> T assegurarPresenca(T objeto, String campo) {
        if(objeto == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    campo);
        }
        return objeto;
    }

    private Disciplina validarDisciplina(Disciplina disciplina, Curso curso) {
        assegurarPresenca(disciplina, "disciplina");

        if(!curso.validarDisciplina(disciplina)) {
            throw new ValidacaoExcecao(CodigoErro.VD_007_CAMPO_NAO_SUPORTADO,
                    "disciplina", "o curso associado não a possui");
        }
        return disciplina;
    }

    private Turno validarTurno(Turno turno, Curso curso) {
        assegurarPresenca(turno, "turno");

        if(!curso.validarTurno(turno)) {
            throw new ValidacaoExcecao(CodigoErro.VD_007_CAMPO_NAO_SUPORTADO,
                    "turno", "o curso associado não o possui");
        }
        return turno;
    }

    private Professor validarProfessorTg(Professor professorTg) {
        assegurarPresenca(professorTg, "professor");

        if(!professorTg.podeSerProfessorTg()) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO, "professor",
                    "professor de TG");
        }
        return professorTg;
    }

    // Métodos de Atualização --------------------------------------------------

    public void atualizarProfessorTg(Professor novoProfessor) {
        this.professorTg = assegurarPresenca(novoProfessor, "professor de TG");
    }

    // Métodos Getters de Delegação --------------------------------------------

    public String nomeCursoTexto() { return curso.nomeTexto(); }
    public ProfessorId idProfessorTg() { return professorTg.id(); }
    public ProfessorId idCoordenadorCurso() { return curso.idCoordenador(); }
    public Integer anoLetivoValor() { return periodoLetivo.anoValor(); }
    public Integer semestreLetivoValor() {
        return periodoLetivo.semestreValor();
    }

    // Métodos Getters ---------------------------------------------------------

    public TurmaId id() { return id; }
    public String idTexto() { return id.valor().toString(); }
    public Curso curso() { return curso; }
    public Disciplina disciplina() { return disciplina; }
    public Turno turno() { return turno; }
    public PeriodoLetivo periodoLetivo() { return periodoLetivo; }
    public Professor professorTg() { return professorTg; }
}