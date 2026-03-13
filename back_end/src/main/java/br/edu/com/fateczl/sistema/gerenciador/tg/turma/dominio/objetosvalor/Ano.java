package br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.ValidacaoExcecao;

import java.time.Year;

public record Ano(Integer valor) {
    public Ano {
        if(valor == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "ano");
        }
        if(valor < Year.now().getValue()) {
            throw new ValidacaoExcecao(CodigoErro.VD_004_DATA_INVALIDA, valor,
                    "ano inserido >= ano atual");
        }
    }
}