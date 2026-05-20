package br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.entidade.Banca;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.objetosvalor.MembroExterno;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.repositorio.BancaRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListarBancasOrientadorCaso {
    private final GrupoTgRepositorio grupoTgRepositorio;
    private final BancaRepositorio bancaRepositorio;
    private final ProfessorRepositorio professorRepositorio;
    private final AlunoRepositorio alunoRepositorio;

    public ListarBancasOrientadorCaso(
            GrupoTgRepositorio grupoTgRepositorio,
            BancaRepositorio bancaRepositorio,
            ProfessorRepositorio professorRepositorio,
            AlunoRepositorio alunoRepositorio
    ) {
        this.grupoTgRepositorio = grupoTgRepositorio;
        this.bancaRepositorio = bancaRepositorio;
        this.professorRepositorio = professorRepositorio;
        this.alunoRepositorio = alunoRepositorio;
    }

    public record MembroBancaDto(String id, String nome) {}

    // DTO que vai pro React popular a tabela
    public record BancaVisaoDto(
            String idBanca,
            String tema,
            String tipoTg,
            String idGrupo,
            LocalDateTime dataHora,
            String situacao,
            boolean podeAtribuirNota,
            List<String> alunos,
            List<MembroBancaDto> membros
    ) {}

    public record Comando(String emailOrientadorLogado) {}

    public List<BancaVisaoDto> executar(Comando comando) {
        // Descobre quem é o professor
        Professor orientador = professorRepositorio.buscarPorEmail(new Email(comando.emailOrientadorLogado))
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "orientador"));

        // Busca todos os grupos onde ele é orientador
        List<GrupoTg> gruposDoOrientador = grupoTgRepositorio.buscarPorOrientadorId(orientador.id());

        List<BancaVisaoDto> bancasVisao = new ArrayList<>();

        // Para cada grupo, verifica se tem banca e monta a visão
        for (GrupoTg grupo : gruposDoOrientador) {
            // Requer criar o método buscarPorGrupoId no BancaRepositorio
            Optional<Banca> bancaOpt = bancaRepositorio.buscarPorGrupoId(grupo.id());

            if (bancaOpt.isPresent()) {
                Banca banca = bancaOpt.get();

                // Regra de UI: Só pode atribuir nota se a data já passou e se ainda não foi avaliada
                boolean dataJaPassou = banca.dataHora().isBefore(LocalDateTime.now());
                boolean naoAvaliada = banca.status().name().equals("MARCADA");
                boolean podeAtribuir = dataJaPassou && naoAvaliada;

                String situacaoVisual = banca.status().name();
                if (podeAtribuir) situacaoVisual = "Pré Banca realizada";

                // --- Resolução dos Alunos do Grupo ---
                List<Aluno> alunosDoGrupo = alunoRepositorio.buscarTodosPorIds(grupo.alunosIds());
                List<String> nomesAlunos = alunosDoGrupo.stream()
                        .map(Aluno::nomeTexto)
                        .toList();


                // --- Resolução dos Membros da Banca ---
                List<MembroBancaDto> membrosDaBanca = new ArrayList<>();

                // O próprio orientador sempre faz parte da banca (como presidente)
                membrosDaBanca.add(new MembroBancaDto(orientador.idTexto(),
                        orientador.nome().valor() + " (Orientador)"));

                // Resolve Professores Internos Convidados (Adapte para os nomes reais dos métodos da sua entidade)
                if (banca.avaliadoresInternos() != null && !banca.avaliadoresInternos().isEmpty()) {
                    List<Professor> convidados = professorRepositorio.buscarTodosPorIds
                            (banca.avaliadoresInternos());
                    for (Professor convidado : convidados) {
                        membrosDaBanca.add(new MembroBancaDto(convidado.idTexto(), convidado.nome().valor()));
                    }
                }

                // Resolve Membros Externos
                if (banca.avaliadoresExternos() != null) {
                    // Usando o e-mail como ID do membro externo (Mesma estratégia usada no MarcarBanca)
                    for (MembroExterno me : banca.avaliadoresExternos()) {
                        membrosDaBanca.add(new MembroBancaDto(me.email(), me.nome()));
                    }
                }

                // --- Montagem do Objeto Final ---
                bancasVisao.add(new BancaVisaoDto(
                        banca.idTexto(),
                        grupo.nomeTemaTg(), // Adapte o getter do tema
                        grupo.tipoTg().name(),
                        grupo.idTexto(),
                        banca.dataHora(),
                        situacaoVisual,
                        podeAtribuir,
                        nomesAlunos,
                        membrosDaBanca
                ));
            }
        }

        return bancasVisao;
    }
}
