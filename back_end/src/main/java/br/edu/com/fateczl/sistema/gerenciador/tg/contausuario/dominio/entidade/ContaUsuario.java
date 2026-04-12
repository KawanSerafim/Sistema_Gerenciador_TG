package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.*;

import java.util.Collections;
import java.util.Set;

public class ContaUsuario {
    private final ContaUsuarioId id;
    private Email email;
    private Senha senha;
    private StatusContaUsuario status;
    private Set<Autoridade> autoridades;

    private ContaUsuario(
            ContaUsuarioId id,
            Email email,
            Senha senha,
            StatusContaUsuario status,
            Set<Autoridade> autoridades
    ) {
        this.id = assegurarPresenca(id, "ID");
        this.email = assegurarPresenca(email, "email");
        this.senha = assegurarPresenca(senha, "senha");
        this.status = assegurarPresenca(status, "status");
        this.autoridades = assegurarPresencaAutoridades(autoridades);
    }

    // MÉTODOS FACTORY ---------------------------------------------------------

    public static ContaUsuario novo(
            ContaUsuarioId id,
            Email email,
            Senha senha,
            Set<Autoridade> autoridades
    ) {
        return new ContaUsuario(
                id,
                email,
                senha,
                StatusContaUsuario.VERIFICACAO_CODIGO_PENDENTE,
                autoridades
        );
    }

    public static ContaUsuario carregar(
            ContaUsuarioId id,
            Email email,
            Senha senha,
            StatusContaUsuario status,
            Set<Autoridade> autoridades
    ) {
        return new ContaUsuario(id, email, senha, status, autoridades);
    }

    // MÉTODOS PARA GARANTIR PRESENÇA ------------------------------------------

    private <T> T assegurarPresenca(T objeto, String campo) {
        if(objeto == null) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    campo
            );
        }
        return objeto;
    }

    private Set<Autoridade> assegurarPresencaAutoridades(
            Set<Autoridade> autoridades
    ) {
        if(autoridades == null) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "autoridades"
            );
        }
        return autoridades;
    }

    // MÉTODOS DE VALIDAÇÃO ----------------------------------------------------

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

    // MÉTODOS DE ALTERAÇÃO DE STATUS ------------------------------------------

    public void confirmarEmail() {
        if(status != StatusContaUsuario.VERIFICACAO_CODIGO_PENDENTE) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "status de conta de usuário",
                    "VERIFICACAO_CODIGO_PENDENTE"
            );
        }
        this.status = StatusContaUsuario.EMAIL_CONFIRMADO;
    }

    public void ativar() {
        if(status != StatusContaUsuario.EMAIL_CONFIRMADO) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "status de conta de usuário", "EMAIL_CONFIRMADO"
            );
        }
        this.status = StatusContaUsuario.ATIVO;
    }

    public void inativar() {
        if(status == StatusContaUsuario.INATIVO) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "status de conta de usuário", "ATIVO ou demais, sem ser " +
                    "INATIVO"
            );
        }
        this.status = StatusContaUsuario.INATIVO;
    }

    // MÉTODOS DE ATUALIZAÇÃO --------------------------------------------------

    public void atualizarEmail(Email novoEmail) {
        this.email = assegurarPresenca(novoEmail, "email");
    }

    public void atualizarSenha(Senha novaSenha) {
        this.senha = assegurarPresenca(novaSenha, "senha");
    }

    public void atualizarAutoridades(Set<Autoridade> novasAutoridades) {
        this.autoridades = assegurarPresencaAutoridades(novasAutoridades);
    }

    // MÉTODOS GETTERS DE DELEGAÇÃO --------------------------------------------

    public String idTexto() { return id.texto(); }
    public String emailTexto() { return email.valor(); }
    public String senhaTexto() { return senha.valor(); }

    // MÉTODOS GETTERS ---------------------------------------------------------

    public ContaUsuarioId id() { return id; }
    public Email email() { return email; }
    public Senha senha() { return senha; }
    public StatusContaUsuario status() { return status; }
    public Set<Autoridade> autoridades() {
        return Collections.unmodifiableSet(autoridades);
    }
}