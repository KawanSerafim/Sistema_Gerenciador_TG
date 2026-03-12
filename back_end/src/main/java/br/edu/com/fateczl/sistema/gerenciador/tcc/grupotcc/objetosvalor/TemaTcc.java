package br.edu.com.fateczl.sistema.gerenciador.tcc.grupotcc.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes.ValidacaoExcecao;

public record TemaTcc(String nome, String descricao) {
    public TemaTcc {
        if(nome == null || descricao == null ||
                nome.isBlank() || descricao.isBlank()) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "tema de tcc");
        }
        if(nome.length() < 3) {
            throw new ValidacaoExcecao(CodigoErro.VD_002_FORMATO_INVALIDO,
                    "nome do tema de tcc", "3 dígitos ou mais");
        }
        if(descricao.length() < 50) {
            throw new ValidacaoExcecao(CodigoErro.VD_002_FORMATO_INVALIDO,
                    "descrição do tema do tcc", "3 dígitos ou mais");
        }
        if (!nome.matches("^[a-zA-ZÀ-ÿ0-9\\s.\\-/&]+$") &&
                !descricao.matches("^[a-zA-ZÀ-ÿ0-9\\s.\\-/&]+$")) {
            throw new ValidacaoExcecao(CodigoErro.VD_002_FORMATO_INVALIDO,
                    "nome ou descrição do tema de tcc",
                    "sem caracteres especiais.");
        }
    }
}