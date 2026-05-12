package br.edu.com.fateczl.sistema.gerenciador.tg.banca.infraestrutura.configuracao;

import br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.casosdeuso.MarcarBancaCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.repositorio.BancaRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoBanca {
    @Bean
    public MarcarBancaCaso marcarBancaCaso(
            BancaRepositorio bancaRepositorio,
            GrupoTgRepositorio grupoTgRepositorio,
            ProfessorRepositorio professorRepositorio
    ){
        return new MarcarBancaCaso(bancaRepositorio,grupoTgRepositorio,professorRepositorio);
    }
}
