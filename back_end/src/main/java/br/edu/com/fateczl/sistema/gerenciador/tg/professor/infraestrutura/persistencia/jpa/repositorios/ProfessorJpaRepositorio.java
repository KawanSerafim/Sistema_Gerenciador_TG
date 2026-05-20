package br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Autoridade;
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
    @Query("SELECT p FROM ProfessorModelo p " +
            "JOIN ContaUsuarioModelo c ON p.contaUsuarioId = c.id " +
            "JOIN c.autoridades a " +
            "WHERE a = :autoridadeBuscada")
    List<ProfessorModelo> buscarPorAutoridade(@Param("autoridadeBuscada") Autoridade autoridadeBuscada);
}