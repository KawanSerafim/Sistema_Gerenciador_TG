package br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.entidade.SolicitacaoOrientacao;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.repositorio.SolicitacaoOrientacaoRepositorio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BuscarSolicitacoesPendentesProfessorCaso {
    private final ProfessorRepositorio professorRepositorio;
    private final SolicitacaoOrientacaoRepositorio solicitacaoRepositorio;
    private final GrupoTgRepositorio grupoTgRepositorio;
    private final AlunoRepositorio alunoRepositorio;
    private final ContaUsuarioRepositorio contaUsuarioRepositorio;

    public BuscarSolicitacoesPendentesProfessorCaso(
            ProfessorRepositorio professorRepositorio,
            SolicitacaoOrientacaoRepositorio solicitacaoRepositorio,
            GrupoTgRepositorio grupoTgRepositorio,
            AlunoRepositorio alunoRepositorio,
            ContaUsuarioRepositorio contaUsuarioRepositorio
    ) {
        this.professorRepositorio = professorRepositorio;
        this.solicitacaoRepositorio = solicitacaoRepositorio;
        this.grupoTgRepositorio = grupoTgRepositorio;
        this.alunoRepositorio = alunoRepositorio;
        this.contaUsuarioRepositorio = contaUsuarioRepositorio;
    }

    public record Comando(String emailUsuarioLogado) {}

    public record Resposta(
            String idSolicitacao,
            String idGrupo,
            String tema,
            String tipoTg,
            String nomeAlunoRepresentante,
            String emailContato,
            Map<br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.TipoRedeSocial, String> redesSociais,
            List<String> nomesIntegrantes
    ) {}

    public List<Resposta> executar(Comando comando) {
        Professor professor = buscarProfessor(comando.emailUsuarioLogado());

        List<SolicitacaoOrientacao> pendentes = solicitacaoRepositorio
                .buscarPendentesPorProfessor(professor.id().texto());

        List<Resposta> visaoCompleta = new ArrayList<>();

        for (SolicitacaoOrientacao solicitacao : pendentes) {
            GrupoTg grupo = buscarGrupo(solicitacao.grupoId().texto());

            // Busca os alunos convertendo a lista de AlunoId para Set/List conforme seu repositório
            List<Aluno> integrantes = alunoRepositorio.buscarTodosPorIds(grupo.alunosIds());

            // Aluno representante do grupo será o primeiro aluno da lista
            Aluno representante = integrantes.getFirst();
            ContaUsuario contaRepresentante = contaUsuarioRepositorio.buscarPorId(representante.contaUsuarioId()).get();

            List<String> nomesIntegrantes = integrantes.stream().map(Aluno::nomeTexto).toList();

            visaoCompleta.add(new Resposta(
                    solicitacao.idTexto(),
                    grupo.idTexto(),
                    grupo.nomeTemaTg(),
                    grupo.tipoTg().name(),
                    representante.nomeTexto(),
                    contaRepresentante.email().valor(),
                    representante.redesSociais(),
                    nomesIntegrantes
            ));
        }

        return visaoCompleta;
    }

    private Professor buscarProfessor(String email) {
        return professorRepositorio.buscarPorEmail(new Email(email))
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "professor logado"));
    }

    private GrupoTg buscarGrupo(String id) {
        // Ajuste para o método correto do seu repositorio de grupo
        return grupoTgRepositorio.buscarPorIdGrupo(new GrupoTgId(UUID.fromString(id)))
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "grupo da solicitacao"));
    }
}

