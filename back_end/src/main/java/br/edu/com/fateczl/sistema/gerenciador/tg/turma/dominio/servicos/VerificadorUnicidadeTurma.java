package br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.servicos;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.PeriodoLetivo;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;

public class VerificadorUnicidadeTurma {
    private final TurmaRepositorio turmaRepositorio;

    public VerificadorUnicidadeTurma(TurmaRepositorio turmaRepositorio) {
        this.turmaRepositorio = turmaRepositorio;
    }

    public void verificar(
            CursoId cursoId,
            Disciplina disciplina,
            Turno turno,
            PeriodoLetivo periodoLetivo
    ) {
        turmaRepositorio.buscarPorCursoIdEDisciplinaETurnoEAnoESemestre(
                cursoId,
                disciplina,
                turno,
                periodoLetivo
        ).ifPresent(turma -> {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_002_REGISTRO_DUPLICADO,
                    "turma"
            );
        });
    }
}