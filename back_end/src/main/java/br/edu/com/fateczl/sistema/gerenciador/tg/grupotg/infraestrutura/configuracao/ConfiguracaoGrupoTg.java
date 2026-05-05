package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.infraestrutura.configuracao;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso.BuscarGrupoTgPorTurmasIdsCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso.GerarGrupoTgCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.servicos.ValidadorComposicaoGrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoGrupoTg {

    @Bean
    public ValidadorComposicaoGrupoTg validadorComposicaoGrupoTg(
            TurmaRepositorio turmaRepositorio,
            GrupoTgRepositorio grupoTgRepositorio
    ) {
        return new ValidadorComposicaoGrupoTg(
                turmaRepositorio,
                grupoTgRepositorio
        );
    }

    @Bean
    public GerarGrupoTgCaso gerarGrupoTgCaso(
            GrupoTgRepositorio grupoTgRepositorio,
            CursoRepositorio cursoRepositorio,
            AlunoRepositorio alunoRepositorio,
            TurmaRepositorio turmaRepositorio,
            ValidadorComposicaoGrupoTg validadorComposicao
    ) {
        return new GerarGrupoTgCaso(
                grupoTgRepositorio,
                cursoRepositorio,
                alunoRepositorio,
                turmaRepositorio,
                validadorComposicao
        );
    }

    @Bean
    public BuscarGrupoTgPorTurmasIdsCaso buscarGrupoTgPorTurmasIdsCaso(
            GrupoTgRepositorio repositorio,
            AlunoRepositorio alunoRepositorio,
            ProfessorRepositorio professorRepositorio
    ) {
        return new BuscarGrupoTgPorTurmasIdsCaso(repositorio, alunoRepositorio, professorRepositorio);
    }
}