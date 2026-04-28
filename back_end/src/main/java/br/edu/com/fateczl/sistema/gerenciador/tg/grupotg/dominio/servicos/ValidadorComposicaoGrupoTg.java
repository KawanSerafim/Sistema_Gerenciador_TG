package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.servicos;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ValidadorComposicaoGrupoTg {
    private final TurmaRepositorio turmaRepositorio;
    private final GrupoTgRepositorio grupoTgRepositorio;

    public ValidadorComposicaoGrupoTg(
            TurmaRepositorio turmaRepositorio,
            GrupoTgRepositorio grupoTgRepositorio
    ) {
        this.turmaRepositorio = turmaRepositorio;
        this.grupoTgRepositorio = grupoTgRepositorio;
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
            var grupoTg = grupoTgRepositorio.buscarPorAlunoECurso(
                    aluno.id(),
                    curso.id()
            );

            if(grupoTg.isPresent()) {
                throw new RegraNegocioExcecao(
                        CodigoErro.RN_003_CONDICAO_ACAO_NAO_ATENDIDA,
                        "aluno " + aluno.nomeTexto(),
                        "de não participar de mais de um grupo de TG no mesmo"
                        + " curso"
                );
            }
        }

        Set<TurmaId> todasTurmasIds = alunos.stream()
                .flatMap(aluno -> aluno.turmasIds().stream())
                .collect(Collectors.toSet());

        Map<TurmaId, Turma> turmasCache = turmaRepositorio.buscarTodasPorIds(
                todasTurmasIds
        ).stream().collect(Collectors.toMap(Turma::id, Function.identity()));

        for(Aluno aluno : alunos) {
            Set<Disciplina> disciplinasDoAluno = aluno.turmasIds().stream()
                    .map(turmaId -> {
                        Turma turma = turmasCache.get(turmaId);
                        if(turma == null) {
                            throw new GenericaExcecao(
                                    CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                                    "turma do aluno"
                            );
                        }
                        return turma.disciplina();
                    })
                    .collect(Collectors.toSet());

            if(!disciplinasDoAluno.equals(disciplinasDoGrupo)) {
                throw new RegraNegocioExcecao(
                        CodigoErro.RN_003_CONDICAO_ACAO_NAO_ATENDIDA,
                        "aluno " + aluno.nomeTexto(),
                        "de estar matriculado exatamente nas mesmas disciplinas"
                        + " exigidas pelo grupo"
                );
            }
        }
    }
}