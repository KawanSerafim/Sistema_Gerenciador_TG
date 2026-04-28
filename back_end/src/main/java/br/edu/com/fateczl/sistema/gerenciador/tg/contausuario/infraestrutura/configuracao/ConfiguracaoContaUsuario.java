package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.configuracao;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.portas.PublicadorEventos;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso.AutenticarUsuarioCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso.EnviarEmailConfirmacaoCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso.ValidarCodigoCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.*;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.servicos.VerificadorUnicidadeEmail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoContaUsuario {

    @Bean
    public VerificadorUnicidadeEmail verificadorUnicidadeEmail(
            ContaUsuarioRepositorio repositorio
    ) {
        return new VerificadorUnicidadeEmail(repositorio);
    }

    @Bean
    public AutenticarUsuarioCaso autenticarUsuarioCaso(
            ContaUsuarioRepositorio repositorio,
            CriptografoSenhas criptografo,
            GeradorToken geradorToken
    ) {
        return new AutenticarUsuarioCaso(
                repositorio,
                criptografo,
                geradorToken
        );
    }

    @Bean
    public EnviarEmailConfirmacaoCaso enviarEmailConfirmacaoCaso(
            ContaUsuarioRepositorio repositorio,
            GeradorCodigoOTP geradorOTP,
            GerenciadorCacheCodigo cacheCodigo,
            RemetenteEmail remetenteEmail
    ) {
        return new EnviarEmailConfirmacaoCaso(
                repositorio,
                cacheCodigo,
                remetenteEmail,
                geradorOTP
        );
    }

    @Bean
    public ValidarCodigoCaso validarCodigoCaso(
            ContaUsuarioRepositorio repositorio,
            GerenciadorCacheCodigo cacheCodigo,
            PublicadorEventos publicadorEventos
    ) {
        return new ValidarCodigoCaso(
                repositorio,
                cacheCodigo,
                publicadorEventos
        );
    }
}