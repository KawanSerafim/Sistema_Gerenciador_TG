package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.*;

public class ContaUsuario {
    private final Email email;
    private Senha senha;
    private StatusContaUsuario status;

    private ContaUsuario(Email email, Senha senha, StatusContaUsuario status) {
        this.email = assegurarPresenca(email, "email");
        this.senha = assegurarPresenca(senha, "senha");
        this.status = assegurarPresenca(status, "status");
    }

    // Métodos Factory ---------------------------------------------------------

    public static ContaUsuario novo(Email email, Senha senha) {
        return new ContaUsuario(email, senha,
                StatusContaUsuario.VERIFICACAO_CODIGO_PENDENTE);
    }

    public static ContaUsuario carregar(Email email, Senha senha,
                                        StatusContaUsuario status) {
        return new ContaUsuario(email, senha, status);
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

    public void atualizarSenha(Senha novaSenha) {
        this.senha = assegurarPresenca(novaSenha, "senha");
    }

    // Métodos Getters ---------------------------------------------------------

    public Email email() { return email; }
    public Senha senha() { return senha; }
    public StatusContaUsuario status() { return status; }
}