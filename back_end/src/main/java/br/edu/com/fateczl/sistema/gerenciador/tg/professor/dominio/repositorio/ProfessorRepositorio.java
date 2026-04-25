package br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.CargoProfessor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;

import java.util.List;
import java.util.Optional;

public interface ProfessorRepositorio {
    void salvar(Professor professor);
    Optional<Professor> buscarPorMatricula(Matricula matricula);
    Optional<Professor> buscarPorEmail(Email email);
    List<Professor> listarPorCargoProfessor(CargoProfessor cargoProfessor);
    Optional<Professor> buscarPorId(ProfessorId professorId);
}