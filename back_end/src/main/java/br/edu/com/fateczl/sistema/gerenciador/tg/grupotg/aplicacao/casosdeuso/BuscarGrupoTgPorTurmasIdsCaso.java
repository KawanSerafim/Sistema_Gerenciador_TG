package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro.VD_005_PADRAO_INVALIDO;

public class BuscarGrupoTgPorTurmasIdsCaso {
    private final GrupoTgRepositorio repositorio;
    private final AlunoRepositorio alunoRepositorio;
    private final ProfessorRepositorio professorRepositorio;

    public BuscarGrupoTgPorTurmasIdsCaso
            (GrupoTgRepositorio repositorio,
             AlunoRepositorio alunoRepositorio,
             ProfessorRepositorio professorRepositorio) {
        this.repositorio = repositorio;
        this.alunoRepositorio = alunoRepositorio;
        this.professorRepositorio = professorRepositorio;
    }

    public record Comando(List<String> turmasIds){}

    // 2. Os DTOs
    public record IntegranteDTO(String id, String nome) {}

    public record GrupoResumoDTO(
            String idGrupo,
            String tipoTg,
            String tema,
            String nomeOrientador,
            String turmaId, // Front-end usa isso para saber de qual turma é o grupo
            List<IntegranteDTO> integrantes
    ) {}

    public record Resposta(List<GrupoResumoDTO> grupos) {}

    // 3. A Execução (O Maestro)
    public Resposta executar(Comando comando) {
        List<TurmaId> idsDasTurmas;

        // Validação da lista de UUIDs
        try {
            idsDasTurmas = comando.turmasIds().stream()
                    .map(idStr -> new TurmaId(UUID.fromString(idStr)))
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new ValidacaoExcecao(VD_005_PADRAO_INVALIDO, "turmasIds","UUID (hexadecimal)");
        }

        // Busca os grupos que cruzam com essas turmas
        List<GrupoTg> grupos = repositorio.buscarPorTurmasIds(idsDasTurmas);

        // Mapeia para o DTO enriquecido
        List<GrupoResumoDTO> dtos = grupos.stream().map(grupo -> {

            // A. Busca o nome do Orientador (Se existir)
            String nomeOrientador = "Sem orientador";
            if (grupo.orientadorId() != null) {
                nomeOrientador = professorRepositorio.buscarPorId(grupo.orientadorId())
                        .map(Professor::nomeTexto)
                        .orElse("Orientador não encontrado");
            }

            // B. Busca os nomes dos Alunos
            List<IntegranteDTO> integrantes = grupo.alunosIds().stream()
                    .map(alunoRepositorio::buscarPorId)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(aluno -> new IntegranteDTO(aluno.idTexto(), aluno.nomeTexto()))
                    .toList();

            // C. Descobre a qual turma esse grupo pertence baseando-se no primeiro aluno
            String turmaDoGrupo = "Desconhecida";
            if (!grupo.alunosIds().isEmpty()) {
                Optional<Aluno> primeiroAluno = alunoRepositorio.buscarPorId(grupo.alunosIds().getFirst());
                if (primeiroAluno.isPresent()) {
                    //Busca a lista de ids de turmas do aluno
                    turmaDoGrupo = primeiroAluno.get().turmasIds().stream()
                            //trasnforma em string
                            .map(TurmaId::texto)
                            //filtra apenas os ids que estão na lista da requisição
                            .filter(comando.turmasIds()::contains)
                            //Pega o primeiro id
                            .findFirst()
                            .orElse("Desconhecida");
                }
            }

            return new GrupoResumoDTO(
                    grupo.idTexto(),
                    //Depara caso haja campos nulos
                    grupo.tipoTg() != null ? grupo.tipoTg().name() : "NÃO_DEFINIDO",
                    grupo.temaTg() != null ? grupo.temaTg().nome() : "Sem tema definido",
                    nomeOrientador,
                    turmaDoGrupo,
                    integrantes
            );
        }).toList();

        return new Resposta(dtos);
    }


}
