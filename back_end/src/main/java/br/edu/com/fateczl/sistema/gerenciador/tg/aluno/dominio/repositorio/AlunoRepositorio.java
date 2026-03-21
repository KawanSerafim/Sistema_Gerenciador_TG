package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;

import java.util.List;
import java.util.Optional;

public interface AlunoRepositorio {
    Aluno salvar(Aluno aluno);
    Optional<Aluno> buscarPorMatricula(Matricula matricula);
    Optional<List<Aluno>> buscarPorMatriculas(List<Matricula> matriculas);
    Optional<Aluno> buscarPorEmail(Email email);
}