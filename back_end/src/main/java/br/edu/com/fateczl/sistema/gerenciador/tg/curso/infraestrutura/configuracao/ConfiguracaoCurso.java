package br.edu.com.fateczl.sistema.gerenciador.tg.curso.infraestrutura.configuracao;

import br.edu.com.fateczl.sistema.gerenciador.tg.administrador.dominio.repositorio.AdministradorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.aplicacao.casosdeuso.GerarCursoCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.aplicacao.casosdeuso.ListarCursosCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.aplicacao.casosdeuso.ListarTiposTgCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.servicos.ValidadorCoordenadorCurso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.servicos.VerificadorUnicidadeCurso;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoCurso {

    @Bean
    public ValidadorCoordenadorCurso validadorCoordenadorCurso() {
        return new ValidadorCoordenadorCurso();
    }

    @Bean
    public VerificadorUnicidadeCurso verificadorUnicidadeCurso(
            CursoRepositorio repositorio
    ) {
        return new VerificadorUnicidadeCurso(repositorio);
    }

    @Bean
    public GerarCursoCaso gerarCursoCaso(
            AdministradorRepositorio administradorRepositorio,
            CursoRepositorio cursoRepositorio,
            ProfessorRepositorio professorRepositorio,
            VerificadorUnicidadeCurso verificadorUnicidade,
            ValidadorCoordenadorCurso validadorCoordenador
    ) {
        return new GerarCursoCaso(
                administradorRepositorio,
                cursoRepositorio,
                professorRepositorio,
                verificadorUnicidade,
                validadorCoordenador
        );
    }

    @Bean
    public ListarCursosCaso listarCursosCaso(
            CursoRepositorio repositorio,
            ProfessorRepositorio professorRepositorio
    ){
        return new ListarCursosCaso(repositorio,professorRepositorio);
    }

    @Bean
    public ListarTiposTgCaso listarTiposTgCaso() {
        return new ListarTiposTgCaso();
    }
}