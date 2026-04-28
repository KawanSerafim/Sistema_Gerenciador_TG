package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;

import java.io.Serializable;
import java.util.UUID;

public record AlunoId(UUID valor) implements Serializable {
    public AlunoId {
        if(valor == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "ID do aluno");
        }
    }

    public String texto() { return this.valor.toString(); }
}