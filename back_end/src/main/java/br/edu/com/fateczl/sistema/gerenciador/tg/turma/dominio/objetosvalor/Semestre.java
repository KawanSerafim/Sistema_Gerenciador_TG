package br.edu.com.fateczl.sistema.gerenciador.tg.turma.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.ValidacaoExcecao;

public record Semestre(Integer valor) {
    public Semestre {
        if(valor == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "semestre");
        }
        if(valor < 1 || valor > 2) {
            throw new ValidacaoExcecao(CodigoErro.VD_005_PADRAO_INVALIDO,
                    "semestre", "1 ou 2");
        }
    }
}
