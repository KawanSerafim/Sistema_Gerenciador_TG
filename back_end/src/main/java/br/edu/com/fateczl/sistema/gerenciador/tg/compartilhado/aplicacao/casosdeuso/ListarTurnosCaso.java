package br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;

import java.util.Arrays;
import java.util.List;

public class ListarTurnosCaso {

    public record Resposta(List<String> turnos){}

    public Resposta executar() {
        List<String> turnos = Arrays.stream(Turno.values())
                .map(Enum::name)
                .toList();
        return new Resposta(turnos);
    }
}
