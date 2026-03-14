package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;

import java.util.List;
import java.util.Optional;

public interface GrupoTgRepositorio {
    GrupoTg salvar(GrupoTg grupoTg);
    Optional<GrupoTg> buscarPorAlunosETurma(List<Aluno> alunos, Turma turma);
}