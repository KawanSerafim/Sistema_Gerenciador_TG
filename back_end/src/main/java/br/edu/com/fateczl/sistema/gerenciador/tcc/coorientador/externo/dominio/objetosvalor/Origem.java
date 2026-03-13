package br.edu.com.fateczl.sistema.gerenciador.tcc.coorientador.externo.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes.ValidacaoExcecao;

public record Origem(String valor) {
    public Origem {
        if(valor == null || valor.isBlank()) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "origem do coorientador externo");
        }
        if(valor.length() < 3) {
            throw new ValidacaoExcecao(CodigoErro.VD_002_FORMATO_INVALIDO,
                    "origem do coorientador externo", "3 dígitos ou mais");
        }
        if (!valor.matches("^[a-zA-ZÀ-ÿ0-9\\s.\\-/&]+$")) {
            throw new ValidacaoExcecao(CodigoErro.VD_002_FORMATO_INVALIDO,
                    "origem do coorientador externo",
                    "sem caracteres especiais.");
        }
    }
}