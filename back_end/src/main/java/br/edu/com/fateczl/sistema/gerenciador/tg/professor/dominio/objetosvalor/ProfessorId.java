package br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.ValidacaoExcecao;

import java.io.Serializable;
import java.util.UUID;

public record ProfessorId(UUID valor) implements Serializable {
    public ProfessorId {
        if(valor == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "ID do professor");
        }
    }
}