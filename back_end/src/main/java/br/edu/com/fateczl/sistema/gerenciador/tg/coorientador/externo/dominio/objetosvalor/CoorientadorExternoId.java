package br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;

import java.util.UUID;

public record CoorientadorExternoId(UUID valor) {
    public CoorientadorExternoId {
        if(valor == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "ID do coorientador externo");
        }
    }

    public String texto() { return this.valor.toString(); }
}