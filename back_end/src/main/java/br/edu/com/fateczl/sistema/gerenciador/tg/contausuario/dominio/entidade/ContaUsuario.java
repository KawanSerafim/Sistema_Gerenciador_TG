package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.*;

public class ContaUsuario {
    private final ContaUsuarioId id;
    private Email email;
    private Senha senha;
    private StatusContaUsuario status;

    private ContaUsuario(ContaUsuarioId id, Email email, Senha senha,
                         StatusContaUsuario status) {
        this.id = assegurarPresenca(id, "ID");
        this.email = assegurarPresenca(email, "email");
        this.senha = assegurarPresenca(senha, "senha");
        this.status = assegurarPresenca(status, "status");
    }

    // Métodos Factory ---------------------------------------------------------

    public static ContaUsuario novo(ContaUsuarioId id, Email email,
                                    Senha senha) {
        return new ContaUsuario(id, email, senha,
                StatusContaUsuario.VERIFICACAO_CODIGO_PENDENTE);
    }

    public static ContaUsuario carregar(ContaUsuarioId id, Email email,
                                        Senha senha,
                                        StatusContaUsuario status) {
        return new ContaUsuario(id, email, senha, status);
    }

    // Métodos especiais -------------------------------------------------------

    private <T> T assegurarPresenca(T objeto, String campo) {
        if(objeto == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    campo);
        }
        return objeto;
    }

    // Métodos de Atualização --------------------------------------------------

    public void atualizarStatus(StatusContaUsuario novoStatus) {
        this.status = assegurarPresenca(novoStatus, "status");
    }

    public void atualizarEmail(Email novoEmail) {
        this.email = assegurarPresenca(novoEmail, "email");
    }

    public void atualizarSenha(Senha novaSenha) {
        this.senha = assegurarPresenca(novaSenha, "senha");
    }

    // Métodos Getters ---------------------------------------------------------

    public ContaUsuarioId id() { return id; }
    public String idTexto() { return id.valor().toString(); }
    public Email email() { return email; }
    public String emailTexto() { return email.valor(); }
    public Senha senha() { return senha; }
    public StatusContaUsuario status() { return status; }
}