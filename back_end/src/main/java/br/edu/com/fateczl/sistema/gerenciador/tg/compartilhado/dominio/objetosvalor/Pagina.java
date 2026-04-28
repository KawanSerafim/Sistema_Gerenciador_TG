package br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor;

import java.util.List;

public record Pagina<T>(
        List<T> conteudo,
        Integer paginaAtual,
        Integer totalPaginas,
        Long totalElementos
) { }
