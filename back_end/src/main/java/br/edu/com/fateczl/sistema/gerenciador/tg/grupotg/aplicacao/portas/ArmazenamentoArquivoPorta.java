package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.portas;

public interface ArmazenamentoArquivoPorta {
    /**
     * Salva o arquivo e retorna o caminho/nome que foi gerado.
     * @param nomeOriginal string do nome original
     * @param conteudoArquivo array de bytes com conteudo
     */
    String salvarArquivo(String nomeOriginal, byte[] conteudoArquivo);
}
