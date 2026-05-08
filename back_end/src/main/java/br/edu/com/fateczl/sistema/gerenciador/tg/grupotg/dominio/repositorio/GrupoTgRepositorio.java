package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.AlunoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;

import java.util.List;
import java.util.Optional;

public interface GrupoTgRepositorio {
    void salvar(GrupoTg grupoTg);
    Optional<GrupoTg> buscarPorAlunoECurso(
            AlunoId alunoId,
            CursoId cursoId
    );
    List<GrupoTg> buscarPorTurmasIds(List<TurmaId> turmasIds);
    Optional<GrupoTg> buscarPorAlunoId(AlunoId alunoId);
}