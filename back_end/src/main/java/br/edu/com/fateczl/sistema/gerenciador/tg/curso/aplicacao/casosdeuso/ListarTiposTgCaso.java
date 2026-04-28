package br.edu.com.fateczl.sistema.gerenciador.tg.curso.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg;

import java.util.Arrays;
import java.util.List;

public class ListarTiposTgCaso {

    // DTO de Resposta (Lista de Strings puros)
    public record Resposta(List<String> tipos) {}

    public Resposta executar() {
        List<String> tipos = Arrays.stream(TipoTg.values())
                // Extrai o texto do Enum
                .map(Enum::name)
                .toList();

        return new Resposta(tipos);
    }
}