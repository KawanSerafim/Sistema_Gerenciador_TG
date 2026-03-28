package br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.servicos;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;

public class VerificadorUnicidadeCurso {
    private final CursoRepositorio cursoRepositorio;

    public VerificadorUnicidadeCurso(CursoRepositorio cursoRepositorio) {
        this.cursoRepositorio = cursoRepositorio;
    }

    public void verificar(Nome nome) {
        cursoRepositorio.buscarPorNome(nome).ifPresent(curso -> {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_002_REGISTRO_DUPLICADO,
                    "nome do curso"
            );
        });
    }
}