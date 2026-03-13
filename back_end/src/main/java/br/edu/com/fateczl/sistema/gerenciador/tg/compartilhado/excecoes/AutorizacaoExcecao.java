package br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes;

public final class AutorizacaoExcecao extends DominioExcecao {
    public AutorizacaoExcecao(CodigoErro codigoErro, Object... args) {
        super(codigoErro, args);
    }
}