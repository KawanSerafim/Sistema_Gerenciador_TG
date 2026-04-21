package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.infraestrutura.persistencia.jpa.modelo.AlunoModelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoJpaRepositorio
        extends JpaRepository<AlunoModelo, String> {
    Optional<AlunoModelo> findByMatricula(String matricula);
    List<AlunoModelo> findByMatriculaIn(List<String> matriculas);
    Optional<AlunoModelo> findByContaUsuarioId(String contaUsuarioId);
    List<AlunoModelo> findByTurmasIdsContaining(String turmasId);

    // "Busque Alunos que estejam na Turma X,
    // ONDE o ID do aluno NÃO ESTEJA na lista de alunosIds de nenhum GrupoTG"
    @Query("""
        SELECT a FROM AlunoModelo a
        JOIN a.turmasIds t
        WHERE t = :turmaId
        AND a.id NOT IN (
            SELECT alunosIds FROM GrupoTgModelo g JOIN g.alunosIds alunosIds
        )
    """)
    List<AlunoModelo> findAlunosSemGrupoPorTurma(@Param("turmaId") String turmaId);
}