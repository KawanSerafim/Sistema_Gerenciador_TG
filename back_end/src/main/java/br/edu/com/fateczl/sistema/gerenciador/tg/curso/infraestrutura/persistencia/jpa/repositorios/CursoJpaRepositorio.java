package br.edu.com.fateczl.sistema.gerenciador.tg.curso.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.curso.infraestrutura.persistencia.jpa.modelo.CursoModelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CursoJpaRepositorio
        extends JpaRepository<CursoModelo, String> {
    Optional<CursoModelo> findByNome(String nome);
    Optional<CursoModelo> findByCoordenadorId(String coordenadorId);
}