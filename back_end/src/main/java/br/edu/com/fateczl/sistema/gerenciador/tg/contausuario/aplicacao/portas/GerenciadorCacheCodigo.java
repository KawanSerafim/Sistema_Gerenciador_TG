package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;

public interface GerenciadorCacheCodigo {
    void salvarCodigo(Email email, String codigo);
    String buscarCodigo(Email email);
    void removerCodigo(Email email);
}