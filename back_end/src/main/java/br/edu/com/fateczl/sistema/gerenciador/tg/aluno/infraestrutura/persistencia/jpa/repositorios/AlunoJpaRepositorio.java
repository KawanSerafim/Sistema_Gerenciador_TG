package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.infraestrutura.persistencia.jpa.modelo.AlunoModelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoJpaRepositorio
        extends JpaRepository<AlunoModelo, String> {
    Optional<AlunoModelo> findByMatricula(String matricula);
    List<AlunoModelo> findByMatriculaIn(List<String> matriculas);
    Optional<AlunoModelo> findByContaUsuarioId(String contaUsuarioId);
}