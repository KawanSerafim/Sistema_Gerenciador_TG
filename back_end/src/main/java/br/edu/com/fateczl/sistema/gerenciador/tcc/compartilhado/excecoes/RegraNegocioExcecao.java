package br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes;

public final class RegraNegocioExcecao extends DominioExcecao {
    public RegraNegocioExcecao(CodigoErro codigoErro, Object... args) {
        super(codigoErro, args);
    }
}