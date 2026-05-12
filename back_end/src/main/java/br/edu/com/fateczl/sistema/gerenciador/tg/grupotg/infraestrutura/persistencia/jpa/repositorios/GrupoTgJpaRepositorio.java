package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.infraestrutura.persistencia.jpa.modelo.GrupoTgModelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrupoTgJpaRepositorio
        extends JpaRepository<GrupoTgModelo, String> {

    @Query("SELECT g FROM GrupoTgModelo g JOIN g.alunosIds a "
            + "WHERE a = :alunoId AND g.cursoId = :cursoId")
    Optional<GrupoTgModelo> findByAlunoAndCurso(
            @Param("alunoId") String alunoId,
            @Param("cursoId") String cursoId
    );

    // Busca grupos distintos onde tenha o id de aluno que faça parte da/s turma/s informadas
    @Query("""
            SELECT DISTINCT g FROM GrupoTgModelo g
        JOIN g.alunosIds alunoId
        WHERE alunoId IN (
            SELECT a.id FROM AlunoModelo a JOIN a.turmasIds t WHERE t IN :turmasIds
        )
    """)
    List<GrupoTgModelo> findGruposByTurmasIds(@Param("turmasIds") List<String> turmasIds);

    @Query("SELECT g FROM GrupoTgModelo g JOIN g.alunosIds a WHERE a = :alunoId ")
    Optional<GrupoTgModelo> findByAluno(@Param("alunoId") String alunoIdTexto);

    List<GrupoTgModelo> findByOrientadorId(String orientadorId);

}