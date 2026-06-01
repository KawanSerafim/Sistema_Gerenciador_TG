package br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;

public record AjusteTipoTg(TipoTg tipoTg, Integer maxAlunosGrupo) {
    public AjusteTipoTg {
        if(tipoTg == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "tipo de TG");
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