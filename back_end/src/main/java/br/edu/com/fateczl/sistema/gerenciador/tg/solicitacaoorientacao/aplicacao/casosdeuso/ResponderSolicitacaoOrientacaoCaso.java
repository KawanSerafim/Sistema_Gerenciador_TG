package br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.entidade.SolicitacaoOrientacao;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.objetosvalor.SolicitacaoOrientacaoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.repositorio.SolicitacaoOrientacaoRepositorio;

import java.util.UUID;

public class ResponderSolicitacaoOrientacaoCaso {
    private final ProfessorRepositorio professorRepositorio;
    private final SolicitacaoOrientacaoRepositorio solicitacaoRepositorio;
    private final GrupoTgRepositorio grupoTgRepositorio;

    public ResponderSolicitacaoOrientacaoCaso(
            ProfessorRepositorio professorRepositorio,
            SolicitacaoOrientacaoRepositorio solicitacaoRepositorio,
            GrupoTgRepositorio grupoTgRepositorio
    ) {
        this.professorRepositorio = professorRepositorio;
        this.solicitacaoRepositorio = solicitacaoRepositorio;
        this.grupoTgRepositorio = grupoTgRepositorio;
    }

    public record Comando(String emailUsuarioLogado, String idSolicitacao, boolean aceita) {}

    public void executar(Comando comando) {
        Professor professor = buscarProfessor(comando.emailUsuarioLogado());
        SolicitacaoOrientacao solicitacao = buscarSolicitacao(comando.idSolicitacao());

        //O professor só pode responder solicitações direcionadas a ele
        if (!solicitacao.professorId().equals(professor.id())) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "solicitação",
                    "pertencente a este professor"
            );
        }

        //Decisão, aceitar ou recusar
        if (comando.aceita()) {
            solicitacao.aceitar();

            GrupoTg grupo = buscarGrupo(solicitacao.grupoId());
            grupo.vincularOrientador(professor.id());

            grupoTgRepositorio.salvar(grupo);
        } else {
            solicitacao.recusar();
        }

        //salva resultado
        solicitacaoRepositorio.salvar(solicitacao);
    }

    private Professor buscarProfessor(String email) {
        return professorRepositorio.buscarPorEmail(new Email(email))
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "professor logado"));
    }

    private SolicitacaoOrientacao buscarSolicitacao(String id) {
        return solicitacaoRepositorio.buscarPorId(new SolicitacaoOrientacaoId(UUID.fromString(id)))
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "solicitação de orientação"));
    }

    private GrupoTg buscarGrupo(GrupoTgId id) {
        return grupoTgRepositorio.buscarPorIdGrupo(id)
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "grupo"));
    }
}
