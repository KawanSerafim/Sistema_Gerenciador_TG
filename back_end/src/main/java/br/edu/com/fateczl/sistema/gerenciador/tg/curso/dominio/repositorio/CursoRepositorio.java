package br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Pagina;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;

import java.util.Optional;

public interface CursoRepositorio {
    void salvar(Curso curso);
    Optional<Curso> buscarPorId(CursoId id);
    Optional<Curso> buscarPorNome(Nome nome);
    Optional<Curso> buscarPorCoordenadorId(ProfessorId professorId);
    Pagina<Curso> buscarTodos(int numeroPagina, int tamanhoPagina);
}