package br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.ValidacaoExcecao;

public record TurmaId(Long valor) {
    public TurmaId {
        if(valor == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "ID da turma");
        }
        if(valor <= 0) {
            throw new ValidacaoExcecao(CodigoErro.VD_002_FORMATO_INVALIDO,
                    "ID da turma", "valor positivo");
        }
    }
}