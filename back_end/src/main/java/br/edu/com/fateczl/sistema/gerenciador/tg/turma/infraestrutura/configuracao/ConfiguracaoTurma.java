package br.edu.com.fateczl.sistema.gerenciador.tg.turma.infraestrutura.configuracao;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.servicos.ValidadorCoordenadorCurso;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso.BuscarGruposOrientadosCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso.BuscarTurmasPorProfessorTgIdCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso.BuscarTurmasProfessorLogadoCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso.FinalizarTurmasCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso.GerarTurmaCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.servicos.ValidadorComposicaoTurma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.servicos.VerificadorUnicidadeTurma;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoTurma {

    @Bean
    public ValidadorComposicaoTurma validadorComposicaoTurma() {
        return new ValidadorComposicaoTurma();
    }

    @Bean
    public VerificadorUnicidadeTurma verificadorUnicidadeTurma(
            TurmaRepositorio repositorio
    ) {
        return new VerificadorUnicidadeTurma(repositorio);
    }

    @Bean
    public GerarTurmaCaso gerarTurmaCaso(
            TurmaRepositorio turmaRepositorio,
            CursoRepositorio cursoRepositorio,
            ProfessorRepositorio professorRepositorio,
            ValidadorComposicaoTurma validadorComposicao,
            VerificadorUnicidadeTurma verificadorUnicidade,
            ValidadorCoordenadorCurso validadorCoordenador
    ) {
        return new GerarTurmaCaso(
                turmaRepositorio,
                cursoRepositorio,
                professorRepositorio,
                validadorComposicao,
                verificadorUnicidade,
                validadorCoordenador
        );
    }

    @Bean
    public BuscarTurmasPorProfessorTgIdCaso buscarTurmasPorProfessorTgIdCaso(TurmaRepositorio repositorio){
        return new BuscarTurmasPorProfessorTgIdCaso(repositorio);
    }

    @Bean
    public BuscarTurmasProfessorLogadoCaso buscarTurmasProfessorLogadoCaso(
            BuscarTurmasPorProfessorTgIdCaso buscarTurmasPorProfessorTgIdCaso,
            ProfessorRepositorio professorRepositorio
    ) {
        return new BuscarTurmasProfessorLogadoCaso(buscarTurmasPorProfessorTgIdCaso, professorRepositorio);
    }

    @Bean
    public BuscarGruposOrientadosCaso buscarGruposOrientadosCaso(
            ProfessorRepositorio professorRepositorio,
            GrupoTgRepositorio grupoTgRepositorio,
            AlunoRepositorio alunoRepositorio,
            TurmaRepositorio turmaRepositorio
    ){
        return new BuscarGruposOrientadosCaso(
                professorRepositorio,grupoTgRepositorio,
                alunoRepositorio,turmaRepositorio
        );
    }

    @Bean
    public FinalizarTurmasCaso finalizarTurmasCaso(
        TurmaRepositorio turmaRepositorio,
        ProfessorRepositorio professorRepositorio
    ) {
        return new FinalizarTurmasCaso(turmaRepositorio, professorRepositorio);
    }
}