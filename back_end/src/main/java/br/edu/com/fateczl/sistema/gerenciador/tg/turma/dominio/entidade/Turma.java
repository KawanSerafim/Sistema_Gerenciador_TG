package br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.PeriodoLetivo;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.StatusTurma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;

public class Turma {
    private final TurmaId id;
    private final CursoId cursoId;
    private final Disciplina disciplina;
    private final Turno turno;
    private final PeriodoLetivo periodoLetivo;
    private ProfessorId professorTgId;
    private StatusTurma statusTurma;

    private Turma(
            TurmaId id,
            CursoId cursoId,
            Disciplina disciplina,
            Turno turno,
            PeriodoLetivo periodoLetivo,
            ProfessorId professorTgId,
            StatusTurma statusTurma
    ) {
        this.id = assegurarPresenca(id, "ID");
        this.cursoId = assegurarPresenca(cursoId, "ID do curso");
        this.disciplina = assegurarPresenca(disciplina, "disciplina");
        this.turno = assegurarPresenca(turno, "turno");
        this.periodoLetivo = assegurarPresenca(periodoLetivo, "período letivo");
        this.professorTgId = assegurarPresenca(
                professorTgId,
                "ID do professor de TG"
        );
        this.statusTurma = assegurarPresenca(statusTurma, "Status da turma");
    }

    // MÉTODOS FACTORY ---------------------------------------------------------

    public static Turma novo(
            TurmaId id,
            CursoId cursoId,
            Disciplina disciplina,
            Turno turno,
            PeriodoLetivo periodoLetivo,
            ProfessorId professorTgId,
            StatusTurma statusTurma
    ) {
        return new Turma(
                id,
                cursoId,
                disciplina,
                turno,
                periodoLetivo,
                professorTgId,
                statusTurma
        );
    }

    public static Turma carregar(
            TurmaId id,
            CursoId cursoId,
            Disciplina disciplina,
            Turno turno,
            PeriodoLetivo periodoLetivo,
            ProfessorId professorTgId,
            StatusTurma statusTurma
    ) {
        return new Turma(
                id,
                cursoId,
                disciplina,
                turno,
                periodoLetivo,
                professorTgId,
                statusTurma
        );
    }

    // MÉTODOS PARA GARANTIR PRESENÇA ------------------------------------------

    private <T> T assegurarPresenca(T objeto, String campo) {
        if(objeto == null) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    campo
            );
        }
        return objeto;
    }

    // MÉTODOS DE ATUALIZAÇÃO --------------------------------------------------

    public void atualizarProfessorTg(ProfessorId novoProfessorId) {
        this.professorTgId = assegurarPresenca(
                novoProfessorId,
                "ID do professor de TG"
        );
    }

    public void finalizar() {
        if (this.statusTurma == StatusTurma.FINALIZADA) {
            throw new RegraNegocioExcecao(CodigoErro.AU_003_ACAO_NAO_PERMITIDA_MOTIVO,
                    "Finalizar turma","Esta turma já está finalizada.");
        }
        this.statusTurma = StatusTurma.FINALIZADA;
    }

    public boolean isAtiva() {
        return this.statusTurma == StatusTurma.ATIVA;
    }

    // MÉTODOS GETTERS DE DELEGAÇÃO --------------------------------------------

    public String idTexto() { return id.texto(); }
    public String cursoIdTexto() { return cursoId.texto(); }
    public String professorTgIdTexto() { return professorTgId.texto(); }
    public Integer anoLetivoValor() { return periodoLetivo.anoValor(); }
    public Integer semestreLetivoValor() {
        return periodoLetivo.semestreValor();
    }

    // MÉTODOS GETTERS ---------------------------------------------------------

    public TurmaId id() { return id; }
    public CursoId cursoId() { return cursoId; }
    public Disciplina disciplina() { return disciplina; }
    public Turno turno() { return turno; }
    public PeriodoLetivo periodoLetivo() { return periodoLetivo; }
    public ProfessorId professorTgId() { return professorTgId; }
    public StatusTurma statusTurma() { return statusTurma;}
}