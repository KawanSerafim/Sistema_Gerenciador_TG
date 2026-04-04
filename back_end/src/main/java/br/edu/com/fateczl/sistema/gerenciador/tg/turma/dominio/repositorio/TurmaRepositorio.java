package br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.PeriodoLetivo;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TurmaRepositorio {
    void salvar(Turma turma);
    Optional<Turma> buscarPorId(TurmaId id);
    List<Turma> buscarTodasPorIds(Set<TurmaId> ids);
    Optional<Turma> buscarPorCursoIdEDisciplinaETurnoEAnoESemestre(
            CursoId cursoId,
            Disciplina disciplina,
            Turno turno,
            PeriodoLetivo periodoLetivo
    );
}