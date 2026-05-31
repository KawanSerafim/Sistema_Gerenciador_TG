package br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;

import java.io.Serializable;
import java.util.UUID;

public record MandatoDiretorId(UUID valor) implements Serializable {

    public MandatoDiretorId {
        if(valor == null){
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "ID do mandato do diretor");
        }
    }
    public String texto() {
        return valor.toString();
    }
}
