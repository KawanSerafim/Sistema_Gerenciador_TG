package br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.repositorio;

import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.entidade.SolicitacaoOrientacao;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.objetosvalor.SolicitacaoOrientacaoId;

import java.util.List;
import java.util.Optional;

public interface SolicitacaoOrientacaoRepositorio {
    void salvar(SolicitacaoOrientacao solicitacao);

    // Para validar se o grupo já enviou um pedido que ainda não foi respondido
    boolean existeSolicitacaoPendenteParaGrupo(String grupoId);

    List<SolicitacaoOrientacao> buscarPendentesPorProfessor(String professorId);

    Optional<SolicitacaoOrientacao> buscarPorId(SolicitacaoOrientacaoId solicitacaoOrientacaoId);
}
