package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;

import java.util.List;
import java.util.UUID;

import static br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro.VD_002_FORMATO_INVALIDO;

public class BuscarAlunosSemGrupoPorTurmasIdsCaso {
    private final AlunoRepositorio repositorio;

    public BuscarAlunosSemGrupoPorTurmasIdsCaso(AlunoRepositorio repositorio){
        this.repositorio = repositorio;
    }

    /**
     * Comando que recebe as turmasIDs vinda da requisição
     *
     * @param turmasIds lista de strings
     */
    public record Comando(List<String> turmasIds){}

    /**
     * DTO do caso de uso buscarAlunosSemGrupoPorTurmasIds
     *
     * @param id
     * @param nome
     * @param turmaId
     */
    public record AlunoSemGrupoDto(String id, String nome, String turmaId) {}

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
        List<TurmaId> idsDasTurmas;

        //Validação da lista de UUIDs
        try{
            idsDasTurmas = comando.turmasIds().stream()
                    .map(idStr -> new TurmaId(UUID.fromString(idStr)))
                    .toList();
        }catch (IllegalArgumentException e) {
            throw new ValidacaoExcecao(VD_002_FORMATO_INVALIDO,"turmasIds","UUID (hexadecimal)");
        }

        //Busca alunos no bd
        List<Aluno> alunos = repositorio.buscarSemGrupoPorTurmasIds(idsDasTurmas);

        // Mapeia para DTO cruzando as turmas
        List<AlunoSemGrupoDto> dtos = alunos.stream()
                .map(aluno -> {
                    // Acha a qual turma pesquisada esse aluno pertence
                    String turmaAtualId = aluno.turmasIds().stream()
                            .map(TurmaId::texto)
                            .filter(comando.turmasIds()::contains)
                            .findFirst()
                            .orElse("Desconhecida");

                    return new AlunoSemGrupoDto(
                            aluno.idTexto(),
                            aluno.nomeTexto(),
                            turmaAtualId
                    );
                })
                .toList();


        return new Resposta(dtos);
    }
}
