package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GeradorCodigoOTP;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GerenciadorCacheCodigo;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.RemetenteEmail;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;

public class EnviarEmailConfirmacaoCaso {
    private final ContaUsuarioRepositorio repositorio;
    private final GerenciadorCacheCodigo cache;
    private final RemetenteEmail remetente;
    private final GeradorCodigoOTP geradorCodigo;

    public EnviarEmailConfirmacaoCaso(
            ContaUsuarioRepositorio repositorio,
            GerenciadorCacheCodigo cache,
            RemetenteEmail remetente,
            GeradorCodigoOTP geradorCodigo
    ) {
        this.repositorio = repositorio;
        this.cache = cache;
        this.remetente = remetente;
        this.geradorCodigo = geradorCodigo;
    }

    public record Comando(String email) {}

    public void executar(Comando comando) {
        Email emailAlvo = new Email(comando.email());
        ContaUsuario conta = procurarConta(emailAlvo);
        conta.validarStatusParaEnviarEmail();

        String codigo = geradorCodigo.gerar(6);
        cache.salvarCodigo(emailAlvo, codigo);

        publicarEmail(codigo, emailAlvo);
    }

    private ContaUsuario procurarConta(Email emailAlvo) {
        return repositorio.buscarPorEmail(emailAlvo)
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO
                ));
    }

    private void publicarEmail(String codigo, Email emailAlvo) {
        String assunto = "Confirme seu Email - SISTEMA DE TG";
        String mensagem = "Olá e seja-vindo!\n\n"
                + "Você está a um passo de concluir seu cadastro. Insira o "
                + "código abaixo:\n\n"
                + "Código: " + codigo + "\n\n"
                + "Atenção: Este código expira em 5 minutos.";
        remetente.enviarEmailTexto(emailAlvo, assunto, mensagem);
    }
}