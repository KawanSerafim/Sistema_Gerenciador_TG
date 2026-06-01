package br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.infraestrutura.configuracao;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.casosdeuso.ListarDisciplinasCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.casosdeuso.ListarTurnosCaso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoCompartilhado {

    @Bean
    public ListarDisciplinasCaso listarDisciplinasCaso() {
        return new ListarDisciplinasCaso();
    }

    @Bean
    public ListarTurnosCaso listarTurnoCaso() {
        return new ListarTurnosCaso();
    }
}
