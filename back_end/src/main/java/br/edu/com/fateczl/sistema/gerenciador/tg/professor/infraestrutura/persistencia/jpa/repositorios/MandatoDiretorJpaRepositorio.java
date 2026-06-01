package br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.persistencia.jpa.modelo.MandatoDiretorModelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface MandatoDiretorJpaRepositorio extends JpaRepository<MandatoDiretorModelo, String> {

    // Busca quem é o diretor na data atual (data_fim pode ser nula ou maior que hoje)
    @Query("SELECT m FROM MandatoDiretorModelo m WHERE m.dataInicio <= :hoje AND (m.dataFim IS NULL OR m.dataFim >= :hoje)")
    Optional<MandatoDiretorModelo> buscarMandatoVigenteNaData(@Param("hoje") LocalDate hoje);

}
