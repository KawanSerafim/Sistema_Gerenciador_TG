package br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.infraestrutura.persistencia.jpa.repositorio;

import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.objetosvalor.StatusSolicitacao;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.infraestrutura.persistencia.jpa.modelo.SolicitacaoOrientacaoModelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitacaoOrientacaoJpaRepositorio extends JpaRepository<SolicitacaoOrientacaoModelo, String> {
    // Retorna true se encontrar pelo menos 1 registro com esse grupoId e status
    boolean existsByGrupoIdAndStatus(String grupoId, StatusSolicitacao status);

    // Retorna a lista de solicitações baseada no professorId e no status
    List<SolicitacaoOrientacaoModelo> findByProfessorIdAndStatus(String professorId, StatusSolicitacao status);
}
