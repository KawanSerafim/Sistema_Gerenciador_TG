package br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.identificadores.Coorientador;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.objetosvalor.CoorientadorExternoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.objetosvalor.Origem;

public class CoorientadorExterno implements Coorientador {
    private final CoorientadorExternoId id;
    private final Nome nome;
    private final Origem origem;

    private CoorientadorExterno(
            CoorientadorExternoId id,
            Nome nome,
            Origem origem
    ) {
        this.id = assegurarPresenca(id, "ID");
        this.nome = assegurarPresenca(nome, "nome");
        this.origem = assegurarPresenca(origem, "origem");
    }

    // Métodos Factory ---------------------------------------------------------

    public static CoorientadorExterno novo(
            CoorientadorExternoId id,
            Nome nome,
            Origem origem
    ) {
        return new CoorientadorExterno(id, nome, origem);
    }

    public static CoorientadorExterno carregar(
            CoorientadorExternoId id,
            Nome nome,
            Origem origem
    ) {
        return new CoorientadorExterno(id, nome, origem);
    }

    // Métodos especiais -------------------------------------------------------

    private <T> T assegurarPresenca(T objeto, String campo) {
        if(objeto == null) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    campo
            );
        }
        return objeto;
    }

    // Métodos de contrato -----------------------------------------------------

    @Override public Nome nome() { return nome; }
    @Override public String identificacao() { return this.origem.valor(); }

    // Métodos Getters ---------------------------------------------------------

    public CoorientadorExternoId id() { return id; }
    public String idTexto() { return id.valor().toString(); }
    public String nomeTexto() { return nome.valor(); }
    public Origem origem() { return origem; }
}