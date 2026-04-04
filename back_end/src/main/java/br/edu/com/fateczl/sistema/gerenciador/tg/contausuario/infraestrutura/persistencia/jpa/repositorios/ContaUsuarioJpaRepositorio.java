package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.persistencia.jpa.modelo.ContaUsuarioModelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContaUsuarioJpaRepositorio
        extends JpaRepository<ContaUsuarioModelo, String> {
    Optional<ContaUsuarioModelo> findByEmail(String email);
}