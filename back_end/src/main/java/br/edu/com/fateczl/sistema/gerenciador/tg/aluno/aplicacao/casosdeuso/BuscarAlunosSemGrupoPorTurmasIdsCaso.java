package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BuscarAlunosSemGrupoPorTurmasIdsCaso {
    private final AlunoRepositorio repositorio;
    private final TurmaRepositorio turmaRepositorio;

    public BuscarAlunosSemGrupoPorTurmasIdsCaso(
            AlunoRepositorio repositorio,
            TurmaRepositorio turmaRepositorio
    ){
        this.repositorio = repositorio;
        this.turmaRepositorio = turmaRepositorio;
    }

    /**
     * Comando que recebe apenas o ID da conta do usuário logado (via JWT)
     *
     * @param idContaUsuario string com o id da conta usuario logado
     */
    public record Comando(String idContaUsuario){}

    /**
     * DTO do caso de uso buscarAlunosSemGrupoPorTurmasIds
     * @param id
     * @param nome
     * @param turmaId
     */
    public record AlunoSemGrupoDto(String id, String nome, String matricula, String turmaId) {}

    /**
     * Resposta que envia uma lista de AlunoDTO
     * @param alunoDtos Lista de AlunoSemGrupoDto
     */
    public record Resposta(List<AlunoSemGrupoDto> alunoDtos) {}

    /**
     * Executar que recebe o IdTurma do comando e retorna lista de AlunoSemGrupoDto
     * @param comando Comando (String turmaId)
     * @return (Resposta) List AlunoSemGrupoDto ou ValidacaoExcecao por formato do turmaID invalido
     */
    public Resposta executar(Comando comando) {
        // Busca a entidade do Aluno através do ID do Token
        Aluno alunoLogado = repositorio.buscarPorContaId(new ContaUsuarioId(UUID.fromString(comando.idContaUsuario())))
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "aluno logado"));

        // Determina o semestre e ano vigentes
        int anoAtual = LocalDate.now().getYear();
        int semestreAtual = LocalDate.now().getMonthValue() <= 6 ? 1 : 2;

        // Resgata as turmas e filtra apenas as que estão ativas agora

        List<Turma> turmasAtuais = turmaRepositorio.buscarTodasPorIds(alunoLogado.turmasIds().stream()
                        .collect(Collectors.toUnmodifiableSet())).stream()
                .filter(t -> t.anoLetivoValor() == anoAtual && t.semestreLetivoValor() == semestreAtual)
                .toList();

        // Se o aluno não estiver matriculado no semestre atual, não há colegas para buscar
        if (turmasAtuais.isEmpty()) {
            return new Resposta(Collections.emptyList());
        }

        // Extrai os IDs das turmas para a busca no banco
        List<TurmaId> idsDasTurmasAtuais = turmasAtuais.stream().map(Turma::id).toList();
        List<String> idsStrAtuais = idsDasTurmasAtuais.stream().map(TurmaId::texto).toList();

        // Consulta os alunos que estão nessas turmas e ainda não possuem grupo
        List<Aluno> alunosSemGrupo = repositorio.buscarSemGrupoPorTurmasIds(idsDasTurmasAtuais);

        // Mapeia para o DTO cruzando as informações
        List<AlunoSemGrupoDto> dtos = alunosSemGrupo.stream()
                // Regra de negócio extra: Não retornar o próprio aluno na lista!
                .filter(aluno -> !aluno.id().equals(alunoLogado.id()))
                .map(aluno -> {
                    // Acha a qual turma pesquisada esse colega pertence
                    String turmaAtualId = aluno.turmasIds().stream()
                            .map(TurmaId::texto)
                            .filter(idsStrAtuais::contains)
                            .findFirst()
                            .orElse("Desconhecida");

                    return new AlunoSemGrupoDto(
                            aluno.idTexto(),
                            aluno.nomeTexto(),
                            aluno.matricula().valor(),
                            turmaAtualId
                    );
                })
                .toList();

        return new Resposta(dtos);
    }
}
