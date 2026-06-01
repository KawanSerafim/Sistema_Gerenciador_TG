package br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.infraestrutura.configuracao;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.aplicacao.casosdeuso.BuscarSolicitacoesPendentesProfessorCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.aplicacao.casosdeuso.ResponderSolicitacaoOrientacaoCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.aplicacao.casosdeuso.SolicitarOrientacaoCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.repositorio.SolicitacaoOrientacaoRepositorio;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoSolicitacaoOrientacao {

    @Bean
    public SolicitarOrientacaoCaso solicitarOrientacaoCaso(
            AlunoRepositorio alunoRepositorio,
            GrupoTgRepositorio grupoTgRepositorio,
            ProfessorRepositorio professorRepositorio,
            SolicitacaoOrientacaoRepositorio solicitacaoRepositorio
    ){
        return new SolicitarOrientacaoCaso(alunoRepositorio,
                grupoTgRepositorio, professorRepositorio, solicitacaoRepositorio);
    }

    @Bean
    public BuscarSolicitacoesPendentesProfessorCaso buscarSolicitacoesPendentesProfessorCaso(
            ProfessorRepositorio professorRepositorio,
            SolicitacaoOrientacaoRepositorio solicitacaoRepositorio,
            GrupoTgRepositorio grupoTgRepositorio,
            AlunoRepositorio alunoRepositorio,
            ContaUsuarioRepositorio contaUsuarioRepositorio
    ){
        return new BuscarSolicitacoesPendentesProfessorCaso(
                professorRepositorio,solicitacaoRepositorio,grupoTgRepositorio,
                alunoRepositorio,contaUsuarioRepositorio);
    }

    @Bean
    public ResponderSolicitacaoOrientacaoCaso responderSolicitacaoOrientacaoCaso(
            ProfessorRepositorio professorRepositorio,
            SolicitacaoOrientacaoRepositorio solicitacaoRepositorio,
            GrupoTgRepositorio grupoTgRepositorio
    ){
        return new ResponderSolicitacaoOrientacaoCaso(professorRepositorio,solicitacaoRepositorio,grupoTgRepositorio);
    }
}
