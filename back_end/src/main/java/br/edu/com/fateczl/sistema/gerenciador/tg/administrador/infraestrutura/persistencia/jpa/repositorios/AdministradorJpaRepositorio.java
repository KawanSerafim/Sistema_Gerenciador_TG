package br.edu.com.fateczl.sistema.gerenciador.tg.administrador.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.administrador.infraestrutura.persistencia.jpa.modelo.AdministradorModelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministradorJpaRepositorio
        extends JpaRepository<AdministradorModelo, String> {

    @Query(
            "SELECT a FROM AdministradorModelo a JOIN ContaUsuarioModelo c ON"
            + " a.contaUsuarioId = c.id WHERE c.email = :email"
    )
    Optional<AdministradorModelo> findByEmailDaConta(
            @Param("email") String email
    );
}