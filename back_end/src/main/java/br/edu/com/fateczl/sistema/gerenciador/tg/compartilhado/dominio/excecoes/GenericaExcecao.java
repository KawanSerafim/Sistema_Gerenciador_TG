package br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes;

public final class GenericaExcecao extends DominioExcecao {
    public GenericaExcecao(CodigoErro codigoErro, Object... args) {
        super(codigoErro, args);
    }
}
