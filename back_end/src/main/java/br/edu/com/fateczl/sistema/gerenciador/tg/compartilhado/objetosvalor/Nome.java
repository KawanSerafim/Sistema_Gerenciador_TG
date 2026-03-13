package br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.ValidacaoExcecao;

public record Nome(String valor) {
    public Nome {
        if(valor == null || valor.isBlank()) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "nome");
        }
        if(valor.length() < 3) {
            throw new ValidacaoExcecao(CodigoErro.VD_002_FORMATO_INVALIDO,
                    "nome", "3 dígitos ou mais");
        }
        if (!valor.matches("^[a-zA-ZÀ-ÿ\\s]+$")) {
            throw new ValidacaoExcecao(CodigoErro.VD_002_FORMATO_INVALIDO,
                    "nome", "sem caracteres especiais.");
        }
    }
}