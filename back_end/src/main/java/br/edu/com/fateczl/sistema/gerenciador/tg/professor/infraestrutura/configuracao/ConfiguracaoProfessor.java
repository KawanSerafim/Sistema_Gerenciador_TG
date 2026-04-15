package br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.configuracao;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.portas.PublicadorEventos;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.CriptografoSenhas;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.servicos.VerificadorUnicidadeEmail;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso.CadastrarProfessorCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso.ListarCargosProfessorCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.servicos.IdentificadorAutoridadesProfessor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.servicos.VerificadorUnicidadeProfessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoProfessor {

    @Bean
    public IdentificadorAutoridadesProfessor identificadorAutoridadesProfessor() {
        return new IdentificadorAutoridadesProfessor();
    }

    @Bean
    public VerificadorUnicidadeProfessor verificadorUnicidadeProfessor(
            ProfessorRepositorio repositorio
    ) {
        return new VerificadorUnicidadeProfessor(repositorio);
    }

    @Bean
    public CadastrarProfessorCaso cadastrarProfessorCaso(
            ProfessorRepositorio professorRepositorio,
            ContaUsuarioRepositorio contaUsuarioRepositorio,
            CriptografoSenhas criptografo,
            PublicadorEventos publicador,
            VerificadorUnicidadeEmail verificadorEmail,
            VerificadorUnicidadeProfessor verificadorProfessor,
            IdentificadorAutoridadesProfessor identificadorAutoridades
    ) {
        return new CadastrarProfessorCaso(
                professorRepositorio,
                contaUsuarioRepositorio,
                criptografo,
                publicador,
                verificadorEmail,
                verificadorProfessor,
                identificadorAutoridades
        );
    }

    @Bean
    public ListarCargosProfessorCaso listarCargosProfessorCaso() {
        return new ListarCargosProfessorCaso();
    }
}