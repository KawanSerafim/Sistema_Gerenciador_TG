package br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;

import java.io.Serializable;
import java.util.UUID;

public record BancaId(UUID valor) implements Serializable {
    public BancaId{
        if(valor == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "ID da banca");
        }
    }
    public String texto() { return this.valor.toString(); }
}
