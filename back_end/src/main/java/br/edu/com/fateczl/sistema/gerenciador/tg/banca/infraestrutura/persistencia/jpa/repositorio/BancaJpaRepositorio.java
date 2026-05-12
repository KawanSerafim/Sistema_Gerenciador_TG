package br.edu.com.fateczl.sistema.gerenciador.tg.banca.infraestrutura.persistencia.jpa.repositorio;

import br.edu.com.fateczl.sistema.gerenciador.tg.banca.infraestrutura.persistencia.jpa.modelo.BancaModelo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BancaJpaRepositorio extends JpaRepository<BancaModelo, String> {
    boolean existsByGrupoId(String grupoId);
}
