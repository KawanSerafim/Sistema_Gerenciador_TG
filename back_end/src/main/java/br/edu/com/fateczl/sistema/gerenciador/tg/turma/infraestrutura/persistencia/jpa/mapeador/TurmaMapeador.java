package br.edu.com.fateczl.sistema.gerenciador.tg.turma.infraestrutura.persistencia.jpa.mapeador;

import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.Ano;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.PeriodoLetivo;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.Semestre;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.infraestrutura.persistencia.jpa.modelo.TurmaModelo;

import java.util.UUID;

public class TurmaMapeador {
    private TurmaMapeador() {}

    public static TurmaModelo paraModelo(Turma dominio) {
        return new TurmaModelo(
                dominio.idTexto(),
                dominio.cursoIdTexto(),
                dominio.disciplina(),
                dominio.turno(),
                dominio.anoLetivoValor(),
                dominio.semestreLetivoValor(),
                dominio.professorTgIdTexto(),
                dominio.statusTurma()
        );
    }

    public static Turma paraDominio(TurmaModelo modelo) {
        var periodoLetivo = new PeriodoLetivo(
                new Ano(modelo.getAno()),
                new Semestre(modelo.getSemestre())
        );

        return Turma.carregar(
                new TurmaId(UUID.fromString(modelo.getId())),
                new CursoId(UUID.fromString(modelo.getCursoId())),
                modelo.getDisciplina(),
                modelo.getTurno(),
                periodoLetivo,
                new ProfessorId(UUID.fromString(modelo.getProfessorId())),
                modelo.getStatusTurma()
        );
    }
}