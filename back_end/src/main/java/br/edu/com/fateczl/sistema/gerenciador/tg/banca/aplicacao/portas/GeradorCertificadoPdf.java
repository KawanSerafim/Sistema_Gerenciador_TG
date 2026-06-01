package br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.portas;

import java.util.List;

public interface GeradorCertificadoPdf {
    record MembroCertificado(String papel, String nome) {}

    byte[] gerar(String tema, String alunosStr, String curso, String dataDefesa,
                 List<MembroCertificado> membros, String dataEmissao,
                 String nomeDiretor, String base64AssinaturaDiretor);
}
