package br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.repositorio;

import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.entidade.SolicitacaoOrientacao;

import java.util.List;

public interface SolicitacaoOrientacaoRepositorio {
    void salvar(SolicitacaoOrientacao solicitacao);

    // Para validarmos se o grupo já enviou um pedido que ainda não foi respondido
    boolean existeSolicitacaoPendenteParaGrupo(String grupoId);

    // (Será usado depois na visão do professor)
    List<SolicitacaoOrientacao> buscarPendentesPorProfessor(String professorId);
}
