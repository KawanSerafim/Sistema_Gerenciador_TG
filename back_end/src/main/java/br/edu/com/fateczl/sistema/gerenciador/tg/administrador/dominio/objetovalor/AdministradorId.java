package br.edu.com.fateczl.sistema.gerenciador.tg.administrador.dominio.objetovalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;

import java.util.UUID;

public record AdministradorId(UUID valor) {
    public AdministradorId {
        if(valor == null) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "ID do administrador"
            );
        }
    }
}