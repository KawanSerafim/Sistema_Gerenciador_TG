package br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;

public record Matricula(String valor) {
    public Matricula {
        if(valor == null || !valor.matches("\\d{13}")) {
            throw new ValidacaoExcecao(CodigoErro.VD_002_FORMATO_INVALIDO,
                    "matrícula", "13 dígitos");
        }
    }
}