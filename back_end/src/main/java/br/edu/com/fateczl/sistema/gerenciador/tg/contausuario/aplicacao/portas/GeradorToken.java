package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;

public interface GeradorToken {
    String gerarToken(ContaUsuario usuario);
    String extrairTopico(String token);
    String extrairId(String token);
}