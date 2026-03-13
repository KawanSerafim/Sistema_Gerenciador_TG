package br.edu.com.fateczl.sistema.gerenciador.tg.curso.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.ValidacaoExcecao;

public record AjusteTipoTg(TipoTcc tipoTcc, Integer maxAlunosGrupo) {
    public AjusteTipoTg {
        if(tipoTcc == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "tipo de TCC");
        }
        if(maxAlunosGrupo == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "máximo de alunos por grupo");
        }
        if(maxAlunosGrupo < 1) {
            throw new ValidacaoExcecao(CodigoErro.VD_005_PADRAO_INVALIDO,
                    "máximo de alunos por grupo", "valor >= 1");
        }
    }

    public boolean validarQtdAlunosGrupo(Integer quantidade) {
        if(quantidade == null) return false;

        return quantidade > 0 && quantidade <= maxAlunosGrupo;
    }
}