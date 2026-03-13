package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.ValidacaoExcecao;

public record GrupoTgId(Long valor) {
    public GrupoTgId {
        if(valor == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "ID do grupo de TG");
        }
        if(valor <= 0) {
            throw new ValidacaoExcecao(CodigoErro.VD_002_FORMATO_INVALIDO,
                    "ID do grupo de TG", "valor positivo");
        }
    }
}