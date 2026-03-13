package br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes;

public final class ValidacaoExcecao extends DominioExcecao {
    public ValidacaoExcecao(CodigoErro codigoErro, Object... args) {
        super(codigoErro, args);
    }
}