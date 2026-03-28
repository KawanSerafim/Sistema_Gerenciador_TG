package br.edu.com.fateczl.sistema.gerenciador.tg.administrador.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.administrador.dominio.objetovalor.AdministradorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;

public class Administrador {
    private final AdministradorId id;
    private final Nome nome;
    private final ContaUsuarioId contaUsuarioId;

    private Administrador(
            AdministradorId id,
            Nome nome,
            ContaUsuarioId contaUsuarioId
    ) {
        this.id = assegurarPresenca(id, "ID do administrador");
        this.nome = assegurarPresenca(nome, "nome");
        this.contaUsuarioId = assegurarPresenca(
                contaUsuarioId, "ID da conta de usuário"
        );
    }

    // Métodos Factory ---------------------------------------------------------

    public static Administrador carregar(
            AdministradorId id,
            Nome nome,
            ContaUsuarioId contaUsuarioId
    ) {
        return new Administrador(id, nome, contaUsuarioId);
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

    // Métodos Getters ---------------------------------------------------------

    public AdministradorId id() { return id; }
    public String idTexto() { return id.valor().toString(); }
    public Nome nome() { return nome; }
    public String nomeTexto() { return nome.valor(); }
    public ContaUsuarioId contaUsuarioId() { return contaUsuarioId; }
}