package br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.objetosvalor.SolicitacaoOrientacaoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.objetosvalor.StatusSolicitacao;

import java.time.LocalDateTime;

public class SolicitacaoOrientacao {
    private final SolicitacaoOrientacaoId id;
    private final GrupoTgId grupoId;
    private final ProfessorId professorId;
    private StatusSolicitacao status;
    private final LocalDateTime dataCriacao;

    private SolicitacaoOrientacao(
            SolicitacaoOrientacaoId id,
            GrupoTgId grupoId,
            ProfessorId professorId,
            StatusSolicitacao status,
            LocalDateTime dataCriacao
    ) {
        this.id = assegurarPresenca(id, "ID da solicitação");
        this.grupoId = assegurarPresenca(grupoId, "ID do grupo");
        this.professorId = assegurarPresenca(professorId, "ID do professor");
        this.status = assegurarPresenca(status, "Status da solicitação");
        this.dataCriacao = assegurarPresenca(dataCriacao, "Data de criação");
    }

    // MÉTODOS FACTORY ---------------------------------------------------------

    public static SolicitacaoOrientacao nova(
            SolicitacaoOrientacaoId id,
            GrupoTgId grupoId,
            ProfessorId professorId
    ) {
        return new SolicitacaoOrientacao(
                id,
                grupoId,
                professorId,
                StatusSolicitacao.PENDENTE,
                LocalDateTime.now()
        );
    }

    public static SolicitacaoOrientacao carregar(
            SolicitacaoOrientacaoId id,
            GrupoTgId grupoId,
            ProfessorId professorId,
            StatusSolicitacao status,
            LocalDateTime dataCriacao
    ) {
        return new SolicitacaoOrientacao(
                id,
                grupoId,
                professorId,
                status,
                dataCriacao
        );
    }

    // MÉTODOS PARA GARANTIR PRESENÇA ------------------------------------------

    private <T> T assegurarPresenca(T objeto, String campo) {
        if (objeto == null) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    campo
            );
        }
        return objeto;
    }

    // MÉTODOS DE COMPORTAMENTO DE NEGÓCIO -------------------------------------

    public void aceitar() {
        assegurarStatusPendente("aceitar");
        this.status = StatusSolicitacao.ACEITA;
    }

    public void recusar() {
        assegurarStatusPendente("recusar");
        this.status = StatusSolicitacao.RECUSADA;
    }

    private void assegurarStatusPendente(String acao) {
        if (this.status != StatusSolicitacao.PENDENTE) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "Não é possível " + acao + " uma solicitação que está com o status: " + this.status
            );
        }
    }

    // MÉTODOS GETTERS DE DELEGAÇÃO --------------------------------------------

    public String idTexto() { return id.texto(); }
    public String grupoIdTexto() { return grupoId.texto(); }
    public String professorIdTexto() { return professorId.texto(); }

    // MÉTODOS GETTERS ---------------------------------------------------------

    public SolicitacaoOrientacaoId id() { return id; }
    public GrupoTgId grupoId() { return grupoId; }
    public ProfessorId professorId() { return professorId; }
    public StatusSolicitacao status() { return status; }
    public LocalDateTime dataCriacao() { return dataCriacao; }
}
