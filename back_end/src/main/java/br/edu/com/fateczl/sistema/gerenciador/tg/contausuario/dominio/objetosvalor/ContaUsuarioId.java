package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;

import java.util.UUID;

public record ContaUsuarioId(UUID valor) {
    public ContaUsuarioId {
        if(valor == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "ID da conta de usuário");
        }
    }
}