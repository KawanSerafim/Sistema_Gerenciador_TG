package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
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

    public void validarStatusParaEnviarEmail() {
        if(status == StatusContaUsuario.ATIVO) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "conta de usuário", "VERIFICACAO_CODIGO_PENDENTE"
            );
        }
    }

    public void validarSePodeAutenticar() {
        if(status != StatusContaUsuario.ATIVO) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "status da conta de usuário", "ATIVO"
            );
        }
    }

    // Métodos de Alteração de Status ------------------------------------------

    public void confirmarEmail() {
        if(status != StatusContaUsuario.VERIFICACAO_CODIGO_PENDENTE) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "status de conta de usuário",
                    "VERIFICACAO_CODIGO_PENDENTE");
        }
        this.status = StatusContaUsuario.EMAIL_CONFIRMADO;
    }

    public void ativar() {
        if(status != StatusContaUsuario.EMAIL_CONFIRMADO) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "status de conta de usuário", "EMAIL_CONFIRMADO");
        }
        this.status = StatusContaUsuario.ATIVO;
    }

    public void inativar() {
        if(status == StatusContaUsuario.INATIVO) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "status de conta de usuário", "ATIVO ou demais, sem ser " +
                    "INATIVO");
        }
        this.status = StatusContaUsuario.INATIVO;
    }

    // Métodos de Atualização --------------------------------------------------

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