package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GerenciadorCacheCodigo;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.RemetenteEmail;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.StatusContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;

import java.util.Random;

public class EnviarEmailConfirmacaoCaso {
    private final ContaUsuarioRepositorio repositorio;
    private final GerenciadorCacheCodigo cache;
    private final RemetenteEmail remetente;

    public EnviarEmailConfirmacaoCaso(ContaUsuarioRepositorio repositorio,
                                      GerenciadorCacheCodigo cache,
                                      RemetenteEmail remetente) {
        this.repositorio = repositorio;
        this.cache = cache;
        this.remetente = remetente;
    }

    public record Comando(String email) {}

    public void executar(Comando comando) {
        Email emailAlvo = new Email(comando.email());
        ContaUsuario conta = procurarConta(emailAlvo);
        validarStatusConta(conta);

        String codigo = gerarCodigoAlfanumerico();
        cache.salvarCodigo(emailAlvo, codigo);

        publicarEmail(codigo, emailAlvo);
    }

    private ContaUsuario procurarConta(Email emailAlvo) {
        return repositorio.buscarPorEmail(emailAlvo).orElseThrow(() ->
                new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO));
    }

    private void validarStatusConta(ContaUsuario conta) {
        if(conta.status() == StatusContaUsuario.ATIVO) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO, "conta de " +
                    "usuário", "VERIFICACAO_CODIGO_PENDENTE");
        }
    }

    private String gerarCodigoAlfanumerico() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuv" +
                "wxyz0123456789";
        StringBuilder codigo = new StringBuilder(6);
        Random random = new Random();

        for(int i = 0; i < 6; i++) {
            codigo.append(caracteres.charAt(random.nextInt(
                    caracteres.length())));
        }
        return codigo.toString();
    }

    private void publicarEmail(String codigo, Email emailAlvo) {
        String assunto = "Confirme seu Email - SISTEMA DE TG";
        String mensagem = "Olá e seja-vindo!\n\n"
                + "Você está a um passo de concluir seu cadastro. Insira o "
                + "código abaixo:\n\n"
                + "Código: " + codigo + "\n\n"
                + "Atenção: Este código expira em 15 minutos.";
        remetente.enviarEmailTexto(emailAlvo, assunto, mensagem);
    }
}