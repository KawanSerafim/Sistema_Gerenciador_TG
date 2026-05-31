package br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.portas;

import java.util.List;

/**
 * Porta que fará a implementação da geração da ata da banca para pdf
 */
public interface GeradorAtaBancaPdf {
    record MembroBancaAta(String nome, String instituicao) {}
    record DadosAta(String tema,
                    String curso,
                    String dia,
                    String mes,
                    String ano,
                    List<String> alunos,
                    List<MembroBancaAta> membrosBanca)
    {}

    byte[] gerar(DadosAta dadosAta);
}
