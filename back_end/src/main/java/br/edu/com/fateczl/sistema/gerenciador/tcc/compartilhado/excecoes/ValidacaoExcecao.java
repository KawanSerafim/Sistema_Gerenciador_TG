package br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes;

public final class ValidacaoExcecao extends DominioExcecao {
    public ValidacaoExcecao(CodigoErro codigoErro, Object... args) {
        super(codigoErro, args);
    }
}