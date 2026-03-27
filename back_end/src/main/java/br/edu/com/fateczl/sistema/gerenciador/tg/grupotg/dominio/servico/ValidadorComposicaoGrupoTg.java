package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.servico;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidadorComposicaoGrupoTg {

    private final TurmaRepositorio turmaRepositorio;

    public ValidadorComposicaoGrupoTg(
            TurmaRepositorio turmaRepositorio
    ) {
        this.turmaRepositorio = turmaRepositorio;
    }

    public void validar(
            Curso curso,
            TipoTg tipoTg,
            Set<Disciplina> disciplinasDoGrupo,
            List<Aluno> alunos
    ) {
        if(!curso.validarQtdAlunosGrupo(tipoTg, alunos.size())) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_004_LIMITE_ALUNOS_EXCEDIDO,
                    alunos.size(),
                    tipoTg.name(),
                    curso.nomeTexto()
            );
        }

        for(Aluno aluno : alunos) {
            Set<Disciplina> disciplinasDoAluno = aluno.turmasIds().stream()
                    .map(turmaId -> turmaRepositorio.buscarPorId(turmaId)
                            .orElseThrow(() -> new GenericaExcecao(
                                    CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                                    "turma do aluno"
                            ))
                            .disciplina()
                    )
                    .collect(Collectors.toSet());

            if(!disciplinasDoAluno.equals(disciplinasDoGrupo)) {
                throw new RegraNegocioExcecao(
                        CodigoErro.RN_003_CONDICAO_ACAO_NAO_ATENDIDA,
                        "aluno " + aluno.nomeTexto(),
                        "estar matriculado exatamente nas mesmas disciplinas "
                        + "exigidas pelo grupo"
                );
            }
        }
    }
}