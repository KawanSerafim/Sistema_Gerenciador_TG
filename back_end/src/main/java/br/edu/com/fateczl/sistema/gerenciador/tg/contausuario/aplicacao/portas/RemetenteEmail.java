package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;

public interface RemetenteEmail {
    void enviarEmailTexto(Email destinatario, String assunto, String mensagem);
}