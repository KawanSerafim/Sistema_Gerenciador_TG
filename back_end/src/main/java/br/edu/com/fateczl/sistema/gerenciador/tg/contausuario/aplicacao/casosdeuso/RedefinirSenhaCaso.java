package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.CriptografoSenhas;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GerenciadorCacheCodigo;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Senha;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;

public class RedefinirSenhaCaso {

    private final ContaUsuarioRepositorio contaRepositorio;
    private final GerenciadorCacheCodigo cacheCodigo;
    private final CriptografoSenhas criptografoSenhas;

    public RedefinirSenhaCaso(
            ContaUsuarioRepositorio contaRepositorio,
            GerenciadorCacheCodigo cacheCodigo,
            CriptografoSenhas criptografoSenhas
    ) {
        this.contaRepositorio = contaRepositorio;
        this.cacheCodigo = cacheCodigo;
        this.criptografoSenhas = criptografoSenhas;
    }

    public record Comando(String emailStr, String codigoInformado, String novaSenhaPlana) {}

    public void executar(Comando comando) {
        Email email = new Email(comando.emailStr());

        // Busca o código que está no seu Cache
        String codigoSalvoNoCache = cacheCodigo.buscarCodigo(email);

        // Valida se expirou (seu cache já retorna null) ou se está incorreto
        if (codigoSalvoNoCache == null || !codigoSalvoNoCache.equals(comando.codigoInformado())) {
            throw new RegraNegocioExcecao(CodigoErro.RN_005_CODIGO_CONFIRMACAO_INVALIDA_EXPIRADO, "Código de verificação inválido ou expirado.");
        }

        // Busca a conta no banco
        ContaUsuario conta = contaRepositorio.buscarPorEmail(email)
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "conta de usuário"));

        // Criptografa e altera a senha
        Senha senhaCriptografada = criptografoSenhas.criptografar(comando.novaSenhaPlana());
        conta.atualizarSenha(senhaCriptografada);

        contaRepositorio.salvar(conta);

        // Destrói o código do cache para ninguém usar de novo!
        cacheCodigo.removerCodigo(email);
    }
}
