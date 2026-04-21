package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;

import java.util.List;
import java.util.UUID;

import static br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro.VD_002_FORMATO_INVALIDO;

public class BuscarAlunosSemGrupoPorTurmaIdCaso {
    private final AlunoRepositorio repositorio;

    public BuscarAlunosSemGrupoPorTurmaIdCaso(AlunoRepositorio repositorio){
        this.repositorio = repositorio;
    }

    /**
     * Comando que recebe a turmaID vinda da requisição
     * @param turmaId string
     */
    public record Comando(String turmaId){}

    /**
     * DTO do caso de uso buscarAlunosSemGrupoPorTurmaId
     * @param id
     * @param nome
     */
    public record AlunoSemGrupoDto(String id, String nome) {}

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
        List<Aluno> alunos;
        try{
             alunos = repositorio.buscarSemGrupoPorTurmaId(
                    new TurmaId(UUID.fromString(comando.turmaId())));
        }catch (IllegalArgumentException e) {
            throw new ValidacaoExcecao(VD_002_FORMATO_INVALIDO,"turmaId","UUID (hexadecimal)");
        }

        return new Resposta(alunos
                .stream()
                .map(aluno -> new AlunoSemGrupoDto(aluno.idTexto(),aluno.nomeTexto()))
                .toList());
    }
}
