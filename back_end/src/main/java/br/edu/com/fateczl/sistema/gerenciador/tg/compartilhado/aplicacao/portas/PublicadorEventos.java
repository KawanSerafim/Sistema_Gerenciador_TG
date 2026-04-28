package br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.portas;

public interface PublicadorEventos {
    void publicar(Object evento);
}