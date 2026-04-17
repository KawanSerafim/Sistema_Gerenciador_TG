package br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.CargoProfessor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.persistencia.jpa.modelo.ProfessorModelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorJpaRepositorio
        extends JpaRepository<ProfessorModelo, String> {
    Optional<ProfessorModelo> findByMatricula(String matricula);

    @Query(
            "SELECT p FROM ProfessorModelo p JOIN ContaUsuarioModelo c ON"
            + " p.contaUsuarioId = c.id WHERE c.email = :email"
    )
    Optional<ProfessorModelo> findByEmailDaConta(
            @Param("email") String email
    );

    List<ProfessorModelo> findByCargo(CargoProfessor cargoProfessor);
}