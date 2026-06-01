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
                contaUsuarioId,
                "ID da conta de usuário"
        );
    }

    // MÉTODO FACTORY ----------------------------------------------------------

    public static Administrador carregar(
            AdministradorId id,
            Nome nome,
            ContaUsuarioId contaUsuarioId
    ) {
        return new Administrador(id, nome, contaUsuarioId);
    }

    // MÉTODO PARA GARANTIR PRESENÇA -------------------------------------------

    private <T> T assegurarPresenca(T objeto, String campo) {
        if(objeto == null) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    campo
            );
        }
        return objeto;
    }

    // MÉTODOS GETTERS DE DELEGAÇÃO --------------------------------------------

    public String idTexto() { return id.texto(); }
    public String nomeTexto() { return nome.valor(); }
    public String contaUsuarioIdTexto() { return contaUsuarioId.texto(); }

    // MÉTODOS GETTERS ---------------------------------------------------------

    public AdministradorId id() { return id; }
    public Nome nome() { return nome; }
    public ContaUsuarioId contaUsuarioId() { return contaUsuarioId; }
}