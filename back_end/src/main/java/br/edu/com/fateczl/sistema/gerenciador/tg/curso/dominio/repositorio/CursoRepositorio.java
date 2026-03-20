package br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;

import java.util.Optional;

public interface CursoRepositorio {
    Curso salvar(Curso curso);
    Optional<Curso> buscarPorNome(Nome nome);
    Optional<Curso> buscarPorCoordenador(Professor professor);
}