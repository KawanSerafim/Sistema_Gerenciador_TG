package br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.PeriodoLetivo;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;

public class Turma {
    private final TurmaId id;
    private final CursoId cursoId;
    private final Disciplina disciplina;
    private final Turno turno;
    private final PeriodoLetivo periodoLetivo;
    private ProfessorId professorTgId;

    private Turma(TurmaId id, CursoId cursoId, Disciplina disciplina,
                  Turno turno,
                  PeriodoLetivo periodoLetivo, ProfessorId professorTgId) {
        this.id = assegurarPresenca(id, "ID");
        this.cursoId = assegurarPresenca(cursoId, "ID do curso");
        this.disciplina = assegurarPresenca(disciplina, "disciplina");
        this.turno = assegurarPresenca(turno, "turno");
        this.periodoLetivo = assegurarPresenca(periodoLetivo, "período letivo");
        this.professorTgId = assegurarPresenca(professorTgId, "ID do " +
                "professor de" +
                " TG");
    }

    // Métodos Factory ---------------------------------------------------------

    public static Turma novo(TurmaId id, CursoId cursoId, Disciplina disciplina,
                             Turno turno,
                             PeriodoLetivo periodoLetivo,
                             ProfessorId professorTgId) {
        return new Turma(id, cursoId, disciplina, turno, periodoLetivo,
                professorTgId);
    }

    public static Turma carregar(TurmaId id, CursoId cursoId,
                                 Disciplina disciplina,
                                 Turno turno, PeriodoLetivo periodoLetivo,
                                 ProfessorId professorTgId) {
        return new Turma(id, cursoId, disciplina, turno, periodoLetivo,
                professorTgId);
    }

    // Métodos especiais -------------------------------------------------------

    private <T> T assegurarPresenca(T objeto, String campo) {
        if(objeto == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    campo);
        }
        return objeto;
    }

    // Métodos de Atualização --------------------------------------------------

    public void atualizarProfessorTg(ProfessorId novoProfessorId) {
        this.professorTgId = assegurarPresenca(novoProfessorId, "ID do " +
                "professor de TG");
    }

    // Métodos Getters de Delegação --------------------------------------------

    public Integer anoLetivoValor() { return periodoLetivo.anoValor(); }
    public Integer semestreLetivoValor() {
        return periodoLetivo.semestreValor();
    }

    // Métodos Getters ---------------------------------------------------------

    public TurmaId id() { return id; }
    public String idTexto() { return id.valor().toString(); }
    public CursoId cursoId() { return cursoId; }
    public String cursoIdTexto() { return cursoId.valor().toString(); }
    public Disciplina disciplina() { return disciplina; }
    public Turno turno() { return turno; }
    public PeriodoLetivo periodoLetivo() { return periodoLetivo; }
    public ProfessorId professorTgId() { return professorTgId; }
    public String professorTgIdTexto() {
        return professorTgId.valor().toString();
    }
}