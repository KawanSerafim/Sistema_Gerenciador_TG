package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Pagina;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;


import java.util.List;
import java.util.UUID;

public class BuscarAlunosImportadosCaso {

    private final AlunoRepositorio repositorio;


    public BuscarAlunosImportadosCaso(AlunoRepositorio repositorio) {
        this.repositorio = repositorio;

    }

    public record Comando(String idTurma, Integer pagina, Integer tamanho){}

    public record AlunoImportadoDTO(String nome, String matricula, String estado) {}

    public record Resposta(Pagina<AlunoImportadoDTO> alunos){}

    public Resposta executar(Comando comando){
        //Busca paginas no banco
        Pagina<Aluno> paginaDominio = repositorio.buscarPorTurmaId(
                new TurmaId(UUID.fromString(comando.idTurma())),
                        comando.pagina(),
                        comando.tamanho()
                );
        List<AlunoImportadoDTO> conteudoDTO = paginaDominio.conteudo().stream()
                .map(aluno -> new AlunoImportadoDTO(
                        aluno.nomeTexto(),
                        aluno.matriculaTexto(),
                        aluno.status().name()
                ))
                .toList();

        // 5. Remontamos a Pagina com o novo conteúdo, preservando os metadados
        Pagina<AlunoImportadoDTO> paginaRetorno = new Pagina<>(
                conteudoDTO,
                paginaDominio.paginaAtual(),
                paginaDominio.totalPaginas(),
                paginaDominio.totalElementos()
        );

        return new Resposta(paginaRetorno);
    }
}
