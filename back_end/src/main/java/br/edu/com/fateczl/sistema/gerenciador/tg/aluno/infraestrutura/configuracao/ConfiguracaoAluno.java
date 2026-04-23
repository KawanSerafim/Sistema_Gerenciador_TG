package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.infraestrutura.configuracao;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso.BuscarAlunosPorTurmaIdCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso.BuscarAlunosSemGrupoPorTurmasIdsCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso.ImportarAlunosCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso.SolicitarAcessoAlunoCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.portas.LeitorArquivoAlunos;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.servicos.ValidadorAutorizacaoImportacao;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.servicos.ValidadorCabecalhoArquivo;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.portas.PublicadorEventos;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.CriptografoSenhas;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.servicos.VerificadorUnicidadeEmail;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoAluno {

    @Bean
    public ValidadorAutorizacaoImportacao validadorAutorizacaoImportacao(
            CursoRepositorio cursoRepositorio
    ) {
        return new ValidadorAutorizacaoImportacao(cursoRepositorio);
    }

    @Bean
    public ValidadorCabecalhoArquivo validadorCabecalhoArquivo() {
        return new ValidadorCabecalhoArquivo();
    }

    @Bean
    public ImportarAlunosCaso importarAlunosCaso(
            AlunoRepositorio alunoRepositorio,
            ProfessorRepositorio professorRepositorio,
            TurmaRepositorio turmaRepositorio,
            LeitorArquivoAlunos leitorArquivo,
            ValidadorAutorizacaoImportacao validadorAutorizacao,
            ValidadorCabecalhoArquivo validadorCabecalho
    ) {
        return new ImportarAlunosCaso(
                alunoRepositorio,
                professorRepositorio,
                turmaRepositorio,
                leitorArquivo,
                validadorAutorizacao,
                validadorCabecalho
        );
    }

    @Bean
    public SolicitarAcessoAlunoCaso solicitarAcessoAlunoCaso(
            AlunoRepositorio alunoRepositorio,
            CriptografoSenhas criptografo,
            PublicadorEventos publicador,
            VerificadorUnicidadeEmail verificadorUnicidadeEmail,
            ContaUsuarioRepositorio contaUsuarioRepositorio
    ) {
        return new SolicitarAcessoAlunoCaso(
                alunoRepositorio,
                contaUsuarioRepositorio,
                criptografo,
                publicador,
                verificadorUnicidadeEmail
        );
    }

    @Bean
    public BuscarAlunosPorTurmaIdCaso buscarAlunosPorTurmaIdCaso(
            AlunoRepositorio repositorio
    ){
        return new BuscarAlunosPorTurmaIdCaso(repositorio);
    }

    @Bean
    public BuscarAlunosSemGrupoPorTurmasIdsCaso buscarAlunosPorTurmaIdSemGrupoCaso(
            AlunoRepositorio repositorio
    ) {
        return new BuscarAlunosSemGrupoPorTurmasIdsCaso(repositorio);
    }
}