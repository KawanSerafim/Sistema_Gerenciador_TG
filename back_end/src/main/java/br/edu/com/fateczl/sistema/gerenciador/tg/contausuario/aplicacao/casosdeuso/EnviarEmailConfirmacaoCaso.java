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
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                        "conta de usuário"
                ));
    }

    private void publicarEmail(String codigo, Email emailAlvo) {
        String assunto = "Confirmação de Cadastro - Sistema de Gerenciamento de TGs da FATEC ZL";

        String mensagemHtml = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;">
                    <h2 style="color: #b30000; text-align: center;">Bem-vindo(a) ao Sistema de Gerenciamento de TGs da FATEC ZL!</h2>
                    <p style="font-size: 16px; color: #333;">Olá 😄</p>
                    <p style="font-size: 16px; color: #333;">Você está a apenas um passo de concluir o seu cadastro na plataforma de gerenciamento de Trabalhos de Graduação.</p>
                    <p style="font-size: 16px; color: #333;">Por favor, insira o código de verificação abaixo na tela do sistema para confirmar o seu e-mail:</p>
                   \s
                    <div style="text-align: center; margin: 30px 0;">
                        <span style="font-size: 32px; font-weight: bold; background-color: #f0f4f8; padding: 10px 20px; border-radius: 5px; letter-spacing: 5px; color: #000; border: 1px dashed #0056b3;">
                            %s
                        </span>
                    </div>
                   \s
                    <p style="font-size: 14px; color: #666; text-align: center;">Atenção: Este código expira em <strong>5 minutos</strong>.</p>
                   \s
                    <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">
                    <p style="font-size: 12px; color: #999; text-align: center;">Se você não se cadastrou no nosso sistema, por favor, desconsidere este e-mail.</p>
                </div>
               \s""".formatted(codigo);

        remetente.enviarEmail(emailAlvo.valor(), assunto, mensagemHtml);
    }
}