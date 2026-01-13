package br.edu.com.fateczl.sistema.gerenciador.tcc.coorientador.externo.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tcc.coorientador.externo.objetosvalor.CoorientadorExternoId;
import br.edu.com.fateczl.sistema.gerenciador.tcc.coorientador.externo.objetosvalor.Origem;

public class CoorientadorExterno {
    private final CoorientadorExternoId id;
    private final Nome nome;
    private final Origem origem;

    private CoorientadorExterno(CoorientadorExternoId id, Nome nome,
                                Origem origem) {
        this.id = id;
        this.nome = assegurarPresenca(nome, "nome");
        this.origem = assegurarPresenca(origem, "origem");
    }

    // Métodos Factory ---------------------------------------------------------

    public static CoorientadorExterno novo(Nome nome, Origem origem) {
        return new CoorientadorExterno(null, nome, origem);
    }

    public static CoorientadorExterno carregar(CoorientadorExternoId id,
                                               Nome nome, Origem origem) {
        if(id == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "ID do coorientador externo");
        }

        return new CoorientadorExterno(id, nome, origem);
    }

    // Métodos especiais -------------------------------------------------------

    private <T> T assegurarPresenca(T objeto, String campo) {
        if(objeto == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    campo);
        }
        return objeto;
    }

    // Métodos Getters ---------------------------------------------------------

    public CoorientadorExternoId id() { return id; }
    public Nome nome() { return nome; }
    public Origem origem() { return origem; }
}