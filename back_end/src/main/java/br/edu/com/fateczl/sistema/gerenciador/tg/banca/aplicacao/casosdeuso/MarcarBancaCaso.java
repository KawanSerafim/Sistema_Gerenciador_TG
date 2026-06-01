package br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.entidade.Banca;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.objetosvalor.BancaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.objetosvalor.MembroExterno;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.repositorio.BancaRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.eventos.BancaMarcadaEvento;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.portas.PublicadorEventos;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class MarcarBancaCaso {
    private final BancaRepositorio bancaRepositorio;
    private final GrupoTgRepositorio grupoTgRepositorio;
    private final ProfessorRepositorio professorRepositorio;
    private final PublicadorEventos publicador;

    public MarcarBancaCaso(
            BancaRepositorio bancaRepositorio,
            GrupoTgRepositorio grupoTgRepositorio,
            ProfessorRepositorio professorRepositorio,
            PublicadorEventos publicador
    ) {
        this.bancaRepositorio = bancaRepositorio;
        this.grupoTgRepositorio = grupoTgRepositorio;
        this.professorRepositorio = professorRepositorio;
        this.publicador = publicador;
    }

    public record MembroExternoDto(String nome, String email, String telefone) {}

    public record Comando(
            String emailOrientadorLogado,
            String idGrupo,
            LocalDate data,
            LocalTime hora,
            String local,
            List<String> idsProfessoresConvidados,
            List<MembroExternoDto> convidadosExternos
    ) {}

    public void executar(Comando comando) {
        // Busca quem está tentando marcar a banca (Orientador)
        Professor orientadorLogado = professorRepositorio.buscarPorEmail(new Email(comando.emailOrientadorLogado()))
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "orientador"));

        // Busca o grupo
        GrupoTgId grupoId = new GrupoTgId(UUID.fromString(comando.idGrupo()));
        GrupoTg grupo = grupoTgRepositorio.buscarPorIdGrupo(grupoId)
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "grupo"));

        // BLINDAGEM: Somente o orientador do grupo pode marcar a banca
        if (grupo.orientadorId() == null || !grupo.orientadorId().equals(orientadorLogado.id())) {
            throw new RegraNegocioExcecao(CodigoErro.AU_003_ACAO_NAO_PERMITIDA_MOTIVO,
                    "marcar banca", "orientador logado não é orientador do grupo");
        }

        // Regra: Um grupo só pode ter uma banca ativa
        if (bancaRepositorio.existeBancaParaGrupo(grupoId)) {
            throw new RegraNegocioExcecao(CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "grupo", "sem banca já marcada");
        }

        // Regra: Só pode marcar banca se o trabalho já foi enviado!
        if (grupo.caminhoArquivoTrabalho() == null || grupo.caminhoArquivoTrabalho().isBlank()) {
            throw new RegraNegocioExcecao(CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO, "grupo",
                    "com o arquivo do Trabalho de Graduação enviado");
        }


        // Mapeia os convidados internos para ProfessorId
        List<ProfessorId> avaliadoresInternos = comando.idsProfessoresConvidados().stream()
                .map(id -> new ProfessorId(UUID.fromString(id)))
                //Remove o orientador para evitar duplicatas
                .filter(id -> !id.equals(orientadorLogado.id()))
                .toList();

        // Mapeia os convidados externos para o Value Object
        List<MembroExterno> avaliadoresExternos = comando.convidadosExternos().stream()
                .map(dto -> new MembroExterno(dto.nome(), dto.email(), dto.telefone()))
                .toList();

        // Junta data e hora
        LocalDateTime dataHoraBanca = LocalDateTime.of(comando.data(), comando.hora());

        // Instancia a Banca e Salva
        Banca novaBanca = Banca.novo(
                new BancaId(UUID.randomUUID()),
                grupoId,
                dataHoraBanca,
                comando.local(),
                avaliadoresInternos,
                avaliadoresExternos
        );

        bancaRepositorio.salvar(novaBanca);

        //Inicia o evento de notificar a banca
        publicador.publicar(new BancaMarcadaEvento(novaBanca.idTexto()));
    }
}
