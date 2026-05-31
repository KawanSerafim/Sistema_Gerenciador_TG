package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas;

public interface RemetenteEmail {
    /**
     * Envia emails, seja apenas texto ou hmtl
     * @param destinatario
     * @param assunto
     * @param mensagem
     */
    void enviarEmail(String destinatario, String assunto, String mensagem);

    /**
     * Envia email, apenas com html, e com anexo
     * @param destinatario
     * @param assunto
     * @param mensagem
     * @param anexo
     * @param nomeAnexo
     */
    void enviarEmailComAnexo(String destinatario, String assunto, String mensagem, byte[] anexo, String nomeAnexo);

}