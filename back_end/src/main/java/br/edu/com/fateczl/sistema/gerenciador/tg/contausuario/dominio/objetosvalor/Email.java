package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.ValidacaoExcecao;

public record Email(String valor) {
    public Email {
        if(valor == null || valor.isBlank()) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "email");
        }
        if(!valor.contains("@fatec.sp.gov.br")) {
            throw new ValidacaoExcecao(CodigoErro.VD_002_FORMATO_INVALIDO,
                    "email", "@fatec.sp.gov.br");
        }
    }
}