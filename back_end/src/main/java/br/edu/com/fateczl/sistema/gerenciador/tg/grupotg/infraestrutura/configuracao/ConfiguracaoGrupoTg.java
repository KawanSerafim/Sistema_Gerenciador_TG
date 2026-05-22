package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.infraestrutura.configuracao;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.repositorio.BancaRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.repositorio.CoorientadorExternoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso.*;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.portas.ArmazenamentoArquivoPorta;
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
    public BuscarVisaoGruposProfessorCaso buscarVisaoGruposProfessorCaso(
            GrupoTgRepositorio repositorio,
            AlunoRepositorio alunoRepositorio,
            ProfessorRepositorio professorRepositorio,
            TurmaRepositorio turmaRepositorio
    ) {
        return new BuscarVisaoGruposProfessorCaso(repositorio, alunoRepositorio, professorRepositorio, turmaRepositorio);
    }

    @Bean
    public VincularCoorientadorExternoCaso vincularCoorientadorExternoCaso(
            AlunoRepositorio alunoRepositorio,
            GrupoTgRepositorio grupoTgRepositorio,
            CoorientadorExternoRepositorio coorientadorRepositorio
    ) {
        return new VincularCoorientadorExternoCaso(
                alunoRepositorio,
                grupoTgRepositorio,
                coorientadorRepositorio
        );
    }

    @Bean
    public BuscarGrupoAlunoCaso buscarGrupoAlunoCaso(
            AlunoRepositorio alunoRepositorio,
            GrupoTgRepositorio grupoTgRepositorio,
            ProfessorRepositorio professorRepositorio
    ) {
        return new BuscarGrupoAlunoCaso(alunoRepositorio,grupoTgRepositorio,professorRepositorio);
    }

    @Bean
    public EnviarTrabalhoGraduacaoCaso enviarTrabalhoGraduacaoCaso(
            GrupoTgRepositorio grupoTgRepositorio,
            AlunoRepositorio alunoRepositorio,
            ArmazenamentoArquivoPorta armazenamentoPorta
    ){
        return new EnviarTrabalhoGraduacaoCaso(
                grupoTgRepositorio,
                alunoRepositorio,
                armazenamentoPorta);
    }

    @Bean
    public BaixarTrabalhoBancaCaso baixarTrabalhoBancaCaso(
            BancaRepositorio bancaRepositorio,
            GrupoTgRepositorio grupoTgRepositorio,
            ProfessorRepositorio professorRepositorio,
            ArmazenamentoArquivoPorta armazenamentoPorta
    ){
        return new BaixarTrabalhoBancaCaso(bancaRepositorio,
                grupoTgRepositorio,
                professorRepositorio,
                armazenamentoPorta);
    }
}