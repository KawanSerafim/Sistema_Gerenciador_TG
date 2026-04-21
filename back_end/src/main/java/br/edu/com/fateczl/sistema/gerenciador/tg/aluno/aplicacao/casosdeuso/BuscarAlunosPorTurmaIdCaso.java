package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;

import java.util.List;
import java.util.UUID;

import static br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro.VD_002_FORMATO_INVALIDO;

public class BuscarAlunosPorTurmaIdCaso {

    private final AlunoRepositorio repositorio;

    public BuscarAlunosPorTurmaIdCaso(AlunoRepositorio repositorio){
        this.repositorio = repositorio;
    }

    /**
     * Comando que recebe a turmaID vinda da requisição
     * @param turmaId string
     */
    public record Comando(String turmaId){}


    /**
     * DTO do caso de uso buscarAlunosPorTurmaId
     * @param id string
     * @param nome string
     * @param matricula string
     * @param statusAluno string
     */
    public record AlunoDto(
            String id,
            String nome,
            String matricula,
            String statusAluno
    ){}

    /**
     * Resposta que envia uma lista de AlunoDTO
     * @param alunoDtos Lista de AlunoDto
     */
    public record Resposta(List<AlunoDto> alunoDtos){}


    /**
     * Executar que recebe o IdTurma do comando e retorna lista de AlunoDtos
     * @param comando Comando (String turmaId)
     * @return (Resposta) List AlunoDto ou ValidacaoExcecao por formato do turmaID invalido
     */
    public Resposta executar(Comando comando){
        String turmaIdStr = comando.turmaId();
        List<Aluno> alunos;
        try {
            alunos = repositorio.buscarPorTurmaId
                    (new TurmaId(UUID.fromString(turmaIdStr)));
        }catch (IllegalArgumentException erro){
            throw new ValidacaoExcecao(VD_002_FORMATO_INVALIDO,"turmaId","UUID (hexadecimal)");
        }
        //Se lista contem itens então cria lista com dto de cada aluno
        List<AlunoDto> dtos = alunos.stream()
                .map(aluno ->
                    new AlunoDto(aluno.idTexto(),
                            aluno.nomeTexto(),
                            aluno.matriculaTexto(),
                            aluno.status().name()
                            )
                ).toList();

        return new Resposta(dtos);
    }

}
