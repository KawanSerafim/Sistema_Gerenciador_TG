package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;


import java.util.List;
import java.util.UUID;

public class BuscarAlunosImportadosCaso {

    private final AlunoRepositorio repositorio;


    public BuscarAlunosImportadosCaso(AlunoRepositorio repositorio) {
        this.repositorio = repositorio;

    }

    public record Comando(String idTurma){}

    public record AlunoImportadoDTO(String nome, String matricula, String estado) {}

    public record Resposta(List<AlunoImportadoDTO> alunos){}

    public Resposta executar(Comando comando){
        List<Aluno> alunos = repositorio.buscarPorTurmaId
                (new TurmaId(
                        UUID.fromString(comando.idTurma())
                ));
        return new Resposta(
                alunos.stream()
                .map(aluno -> new AlunoImportadoDTO(
                        aluno.nomeTexto(),
                        aluno.matriculaTexto(),
                        aluno.status().name())
                )
                .toList()
        );
    }
}
