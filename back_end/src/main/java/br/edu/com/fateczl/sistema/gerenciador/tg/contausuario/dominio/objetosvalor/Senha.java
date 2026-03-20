package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;

public record Senha(String valor) {
    public Senha {
        if(valor == null || valor.isBlank()) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "senha");
        }
    }
}