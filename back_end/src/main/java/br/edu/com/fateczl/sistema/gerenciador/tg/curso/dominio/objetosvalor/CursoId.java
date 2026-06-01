package br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;

import java.util.UUID;

public record CursoId(UUID valor) {
    public CursoId {
        if(valor == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "ID do curso");
        }
    }

    public String texto() { return this.valor.toString(); }
}