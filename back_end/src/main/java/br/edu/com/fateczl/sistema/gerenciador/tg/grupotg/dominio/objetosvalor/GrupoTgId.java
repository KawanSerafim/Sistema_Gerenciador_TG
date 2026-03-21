package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;

import java.util.UUID;

public record GrupoTgId(UUID valor) {
    public GrupoTgId {
        if(valor == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "ID do grupo de TG");
        }
    }
}