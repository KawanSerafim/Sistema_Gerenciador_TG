package br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.objetosvalor.Turno;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.PeriodoLetivo;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;

import java.util.Optional;

public interface TurmaRepositorio {
    Turma salvar(Turma turma);
    Optional<Turma> buscarPorId(TurmaId id);
    Optional<Turma> buscarPorCursoEDisciplinaETurnoEAnoESemestre(Curso curso,
            Disciplina disciplina, Turno turno, PeriodoLetivo periodoLetivo);
}