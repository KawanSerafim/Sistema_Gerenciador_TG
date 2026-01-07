package br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes;

public abstract sealed class DominioExcecao extends RuntimeException
    permits ValidacaoExcecao, RegraNegocioExcecao, AutorizacaoExcecao {

    private final CodigoErro codigoErro;

    protected DominioExcecao(CodigoErro codigoErro, Object... args) {
        super(codigoErro.formatar(args));
        this.codigoErro = codigoErro;
    }

    public CodigoErro getCodigoErro() {
        return codigoErro;
    }
}