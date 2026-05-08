package br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.AlunoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.entidade.SolicitacaoOrientacao;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.objetosvalor.SolicitacaoOrientacaoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.repositorio.SolicitacaoOrientacaoRepositorio;

import java.util.UUID;

public class SolicitarOrientacaoCaso {
    private final AlunoRepositorio alunoRepositorio;
    private final GrupoTgRepositorio grupoTgRepositorio;
    private final ProfessorRepositorio professorRepositorio;
    private final SolicitacaoOrientacaoRepositorio solicitacaoRepositorio;

    public SolicitarOrientacaoCaso(
            AlunoRepositorio alunoRepositorio,
            GrupoTgRepositorio grupoTgRepositorio,
            ProfessorRepositorio professorRepositorio,
            SolicitacaoOrientacaoRepositorio solicitacaoRepositorio
    ) {
        this.alunoRepositorio = alunoRepositorio;
        this.grupoTgRepositorio = grupoTgRepositorio;
        this.professorRepositorio = professorRepositorio;
        this.solicitacaoRepositorio = solicitacaoRepositorio;
    }

    public record Comando(
            String idContaUsuario,
            String idProfessor
    ) {}

    // FLUXO PRINCIPAL ---------------------------------------------------------

    public void executar(Comando comando) {
        // Identifica quem está solicitando
        Aluno alunoAutor = buscarAlunoAutor(comando.idContaUsuario());

        // Localiza o grupo do qual esse aluno faz parte
        GrupoTg grupo = buscarGrupoDoAluno(alunoAutor.id());
        // Validações de regra de negócio cruciais
        validarGrupoSemOrientador(grupo);
        validarSolicitacaoPendente(grupo.id());

        // Busca o professor alvo da solicitação
        Professor professor = buscarProfessor(comando.idProfessor());

        // Instancia a nova solicitação de domínio (status já nasce PENDENTE na factory)
        SolicitacaoOrientacao novaSolicitacao = SolicitacaoOrientacao.nova(
                new SolicitacaoOrientacaoId(UUID.randomUUID()),
                grupo.id(),
                professor.id()
        );

        // Persiste no banco de dados
        solicitacaoRepositorio.salvar(novaSolicitacao);
    }

    // FLUXOS ESPECIALIZADOS ---------------------------------------------------

    private Aluno buscarAlunoAutor(String id) {
        return alunoRepositorio.buscarPorContaId(new ContaUsuarioId(UUID.fromString(id)))
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                        "aluno solicitante"
                ));
    }

    private GrupoTg buscarGrupoDoAluno(AlunoId alunoId) {
        return grupoTgRepositorio.buscarPorAlunoId(alunoId)
                .orElseThrow(() -> new RegraNegocioExcecao(
                        CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                        "aluno",
                        "vinculado a um grupo de TG"
                ));
    }

    private Professor buscarProfessor(String id) {
        return professorRepositorio.buscarPorId(new ProfessorId(UUID.fromString(id)))
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                        "professor orientador"
                ));
    }

    private void validarGrupoSemOrientador(GrupoTg grupo) {
        if (grupo.orientadorId() != null) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "grupo",
                    "sem orientador definido"
            );
        }
    }

    private void validarSolicitacaoPendente(GrupoTgId grupoId) {
        // Essa verificação evita o spam de convites. O grupo tem que aguardar a resposta.
        boolean possuiSolicitacaoPendente = solicitacaoRepositorio.existeSolicitacaoPendenteParaGrupo(grupoId.texto());

        if (possuiSolicitacaoPendente) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "grupo",
                    "sem solicitações pendentes de resposta"
            );
        }
    }
}
