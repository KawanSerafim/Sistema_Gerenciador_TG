package br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.configuracao;

import br.edu.com.fateczl.sistema.gerenciador.tg.administrador.dominio.repositorio.AdministradorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso.AtribuirMandatoDiretorCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso.BuscarMandatoDiretorVigenteCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso.RetirarMandatoDiretorCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.MandatoDiretorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoMandatoDiretor {
    @Bean
    public AtribuirMandatoDiretorCaso atribuirMandatoDiretorCaso(
            ProfessorRepositorio professorRepositorio,
            MandatoDiretorRepositorio mandatoDiretorRepositorio,
            AdministradorRepositorio administradorRepositorio
    ){
        return new AtribuirMandatoDiretorCaso(professorRepositorio,
                mandatoDiretorRepositorio,
                administradorRepositorio);
    }

    @Bean
    public BuscarMandatoDiretorVigenteCaso buscarMandatoDiretorVigenteCaso(
            MandatoDiretorRepositorio mandatoDiretorRepositorio,
            AdministradorRepositorio administradorRepositorio
    ){
        return new BuscarMandatoDiretorVigenteCaso(mandatoDiretorRepositorio, administradorRepositorio);
    }

    @Bean
    public RetirarMandatoDiretorCaso retirarMandatoDiretorCaso(
            MandatoDiretorRepositorio mandatoDiretorRepositorio,
            AdministradorRepositorio administradorRepositorio
    ){
        return new RetirarMandatoDiretorCaso(mandatoDiretorRepositorio, administradorRepositorio);
    }
}
