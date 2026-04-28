package br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.infraestrutura.persistencia.jpa.modelo.CoorientadorExternoModelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoorientadorExternoJpaRepositorio
        extends JpaRepository<CoorientadorExternoModelo, String> {
    Optional<CoorientadorExternoModelo> findByNomeAndOrigem(
            String nome,
            String origem
    );
}