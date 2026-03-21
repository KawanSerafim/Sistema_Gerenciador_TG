package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.StatusAluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.StatusContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.TemaTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;

import java.util.List;
import java.util.UUID;

public class GerarGrupoTgCaso {
    private final GrupoTgRepositorio grupoTgRepositorio;
    private final CursoRepositorio cursoRepositorio;
    private final AlunoRepositorio alunoRepositorio;

    public GerarGrupoTgCaso(GrupoTgRepositorio grupoTgRepositorio,
                            CursoRepositorio cursoRepositorio,
                            AlunoRepositorio alunoRepositorio) {
        this.grupoTgRepositorio = grupoTgRepositorio;
        this.cursoRepositorio = cursoRepositorio;
        this.alunoRepositorio = alunoRepositorio;
    }

    public record Comando(
            String idCurso,
            Disciplina disciplina,
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

        GrupoTg novoGrupo = GrupoTg.novo(curso, comando.disciplina(), tema,
                comando.tipoTg(), alunos);

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

            if(aluno.statusContaUsuario() != StatusContaUsuario.ATIVO) {
                throw new RegraNegocioExcecao(
                        CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO, "conta " +
                        "do aluno", "ATIVO");
            }
        }
        return alunosEncontrados;
    }
}