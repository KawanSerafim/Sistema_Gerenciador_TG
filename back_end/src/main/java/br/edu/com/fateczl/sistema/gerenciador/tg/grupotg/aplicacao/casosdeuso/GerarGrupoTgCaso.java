package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.AlunoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.StatusAluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.TemaTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.servicos.ValidadorComposicaoGrupoTg;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GerarGrupoTgCaso {
    private final GrupoTgRepositorio grupoTgRepositorio;
    private final CursoRepositorio cursoRepositorio;
    private final AlunoRepositorio alunoRepositorio;
    private final ValidadorComposicaoGrupoTg validadorComposicao;

    public GerarGrupoTgCaso(
            GrupoTgRepositorio grupoTgRepositorio,
            CursoRepositorio cursoRepositorio,
            AlunoRepositorio alunoRepositorio,
            ValidadorComposicaoGrupoTg validadorComposicao
    ) {
        this.grupoTgRepositorio = grupoTgRepositorio;
        this.cursoRepositorio = cursoRepositorio;
        this.alunoRepositorio = alunoRepositorio;
        this.validadorComposicao = validadorComposicao;
    }

    public record Comando(
            String idCurso,
            Set<Disciplina> disciplinas,
            String tema,
            String descricaoTema,
            TipoTg tipoTg,
            List<String> matriculasAlunos
    ) {}

    // FLUXO PRINCIPAL ---------------------------------------------------------

    public void executar(Comando comando) {
        TemaTg tema = new TemaTg(comando.tema(), comando.descricaoTema());
        Curso curso = buscarCurso(comando.idCurso());
        List<Aluno> alunos = buscarEValidarAlunos(comando.matriculasAlunos());

        validadorComposicao.validar(
                curso,
                comando.tipoTg(),
                comando.disciplinas(),
                alunos
        );

        List<AlunoId> alunoIds = alunos.stream().map(Aluno::id).toList();

        GrupoTg novoGrupo = GrupoTg.novo(
                new GrupoTgId(UUID.randomUUID()),
                curso.id(),
                comando.disciplinas(),
                tema,
                comando.tipoTg(),
                alunoIds
        );

        grupoTgRepositorio.salvar(novoGrupo);
    }

    // FLUXOS ESPECIALIZADOS ---------------------------------------------------

    private Curso buscarCurso(String id) {
        CursoId cursoId = new CursoId(UUID.fromString(id));
        return cursoRepositorio.buscarPorId(cursoId)
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "curso"));
    }

    private List<Aluno> buscarEValidarAlunos(List<String> matriculasTexto) {
        List<Matricula> matriculas = matriculasTexto.stream()
                .map(Matricula::new).toList();

        List<Aluno> alunosEncontrados = alunoRepositorio.buscarPorMatriculas(
                matriculas).orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                        "um ou mais alunos listados"));

        if(alunosEncontrados.size() != matriculas.size()) {
            throw new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                    "um ou mais alunos listados");
        }

        for(Aluno aluno : alunosEncontrados) {
            if(aluno.status() != StatusAluno.CADASTRADO) {
                throw new RegraNegocioExcecao(
                        CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO, "aluno " +
                        aluno.nomeTexto(), "CADASTRADO");
            }
        }
        return alunosEncontrados;
    }
}