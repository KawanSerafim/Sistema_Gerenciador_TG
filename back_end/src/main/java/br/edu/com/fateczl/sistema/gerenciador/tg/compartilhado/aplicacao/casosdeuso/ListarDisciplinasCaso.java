package br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;

import java.util.Arrays;
import java.util.List;

public class ListarDisciplinasCaso {

    public record Resposta(List<String> nomes){}

    public Resposta executar(){
        List<String> disciplinas = Arrays.stream(Disciplina.values())
                .map(Enum::name)
                .toList();
        return new Resposta(disciplinas);
    }
}
