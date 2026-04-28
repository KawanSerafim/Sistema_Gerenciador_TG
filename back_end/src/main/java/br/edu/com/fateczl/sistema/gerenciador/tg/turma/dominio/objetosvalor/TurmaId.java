package br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;

import java.util.UUID;

public record TurmaId(UUID valor) {
    public TurmaId {
        if(valor == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "ID da turma");
        }
    }

    public String texto() { return this.valor.toString(); }
}