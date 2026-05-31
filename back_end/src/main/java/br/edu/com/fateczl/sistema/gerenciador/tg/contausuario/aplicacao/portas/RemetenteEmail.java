package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;

public interface RemetenteEmail {
    /**
     * Envia emails, seja apenas texto ou hmtl
     * @param destinatario
     * @param assunto
     * @param mensagem
     */
    void enviarEmail(Email destinatario, String assunto, String mensagem);

    /**
     * Envia email, apenas com html, e com anexo
     * @param destinatario
     * @param assunto
     * @param mensagem
     * @param anexo
     * @param nomeAnexo
     */
    void enviarEmailComAnexo(Email destinatario, String assunto, String mensagem, byte[] anexo, String nomeAnexo);

}