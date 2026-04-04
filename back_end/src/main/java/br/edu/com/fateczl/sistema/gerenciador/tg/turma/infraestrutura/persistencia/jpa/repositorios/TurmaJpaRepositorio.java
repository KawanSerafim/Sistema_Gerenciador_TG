package br.edu.com.fateczl.sistema.gerenciador.tg.turma.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.infraestrutura.persistencia.jpa.modelo.TurmaModelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TurmaJpaRepositorio
        extends JpaRepository<TurmaModelo, String> {
    Optional<TurmaModelo> findByCursoIdAndDisciplinaAndTurnoAndAnoAndSemestre(
            String cursoId,
            Disciplina disciplina,
            Turno turno,
            Integer ano,
            Integer semestre
    );
}