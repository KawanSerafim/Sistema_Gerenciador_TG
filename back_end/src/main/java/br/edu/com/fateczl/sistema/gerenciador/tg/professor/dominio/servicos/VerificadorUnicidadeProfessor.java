package br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.servicos;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;

public class VerificadorUnicidadeProfessor {
    private final ProfessorRepositorio repositorio;

    public VerificadorUnicidadeProfessor(ProfessorRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void verificar(Matricula matricula) {
        repositorio.buscarPorMatricula(matricula).ifPresent(professor -> {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_002_REGISTRO_DUPLICADO,
                    "matrícula do professor"
            );
        });
    }
}