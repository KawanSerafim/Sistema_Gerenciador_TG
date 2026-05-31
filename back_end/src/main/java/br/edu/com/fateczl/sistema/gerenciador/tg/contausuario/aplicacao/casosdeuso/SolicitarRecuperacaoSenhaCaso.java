package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GeradorCodigoOTP;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GerenciadorCacheCodigo;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.RemetenteEmail;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;

public class SolicitarRecuperacaoSenhaCaso {
    private final ContaUsuarioRepositorio repositorio;
    private final GerenciadorCacheCodigo cache;
    private final RemetenteEmail remetente;
    private final GeradorCodigoOTP geradorCodigo;

    public SolicitarRecuperacaoSenhaCaso(
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
        validarSeContaExiste(emailAlvo);

        String codigo = geradorCodigo.gerar(6);
        cache.salvarCodigo(emailAlvo, codigo);

        publicarEmail(codigo, emailAlvo);
    }

    private void validarSeContaExiste(Email emailAlvo) {
        repositorio.buscarPorEmail(emailAlvo)
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "conta de usuário"
                ));
        //Se chegou aqui conta existe, pode prosseguir
    }

    private void publicarEmail(String codigo, Email emailAlvo) {
        String assunto = "Recuperação de Senha - SISTEMA DE TG";

        // RemetenteEmail aceita HTML
        String mensagemHtml = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;">
                    <h2 style="color: #b30000; text-align: center;">Recuperação de Senha</h2>
                    <p style="font-size: 16px; color: #333;">Olá,</p>
                    <p style="font-size: 16px; color: #333;">Recebemos uma solicitação para redefinir a senha da sua conta no sistema de gerenciamento de TG.</p>
                    <p style="font-size: 16px; color: #333;">Use o código de verificação abaixo para criar uma nova senha:</p>
                   \s
                    <div style="text-align: center; margin: 30px 0;">
                        <span style="font-size: 32px; font-weight: bold; background-color: #f4f4f4; padding: 10px 20px; border-radius: 5px; letter-spacing: 5px; color: #000;">
                            %s
                        </span>
                    </div>
                   \s
                    <p style="font-size: 14px; color: #666; text-align: center;">Este código é válido por <strong>5 minutos</strong>.</p>
                    <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">
                    <p style="font-size: 12px; color: #999; text-align: center;">Se você não solicitou esta alteração, ignore este e-mail. Nenhuma mudança será feita na sua conta.</p>
                </div>
               \s""".formatted(codigo);

        remetente.enviarEmail(emailAlvo, assunto, mensagemHtml);
    }
}
