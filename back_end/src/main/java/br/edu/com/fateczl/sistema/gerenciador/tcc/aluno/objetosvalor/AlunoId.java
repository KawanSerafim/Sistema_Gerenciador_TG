package br.edu.com.fateczl.sistema.gerenciador.tcc.aluno.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes.ValidacaoExcecao;

import java.io.Serializable;

public record AlunoId(Long valor) implements Serializable {
    public AlunoId {
        if(valor == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "ID do aluno");
        }
        if(valor <= 0) {
            throw new ValidacaoExcecao(CodigoErro.VD_002_FORMATO_INVALIDO,
                    "ID do aluno", "valor positivo");
        }
    }
}