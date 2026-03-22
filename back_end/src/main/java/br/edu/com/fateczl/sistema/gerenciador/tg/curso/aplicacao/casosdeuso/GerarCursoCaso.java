package br.edu.com.fateczl.sistema.gerenciador.tg.curso.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.StatusContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.AjusteTipoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.ParametrosCurso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;

import java.util.List;
import java.util.UUID;

public class GerarCursoCaso {
    private final CursoRepositorio cursoRepositorio;
    private final ProfessorRepositorio professorRepositorio;

    public GerarCursoCaso(CursoRepositorio cursoRepositorio,
                          ProfessorRepositorio professorRepositorio) {
        this.cursoRepositorio = cursoRepositorio;
        this.professorRepositorio = professorRepositorio;
    }

    public record AjusteComando(TipoTg tipoTg, Integer maxAlunosGrupo) {}

    public record Comando(
            String nome,
            String matriculaCoordenador,
            List<Turno> turnos,
            List<Disciplina> disciplinas,
            List<AjusteComando> ajustes
    ) {}

    public record Resposta(
            String id,
            String nome,
            String matriculaCoordenador,
            String nomeCoordenador
    ) {}

    // FLUXO PRINCIPAL ---------------------------------------------------------

    public Resposta executar(Comando comando) {
        Nome nome = new Nome(comando.nome());
        Matricula matriculaCoordenador = new Matricula(
                comando.matriculaCoordenador());

        validarUnicidadeNome(nome);
        Professor coordenador = buscarEValidarCoordenador(matriculaCoordenador);
        ParametrosCurso parametros = gerarParametros(comando);

        Curso novoCurso = Curso.novo(new CursoId(UUID.randomUUID()), nome,
                parametros, coordenador);
        cursoRepositorio.salvar(novoCurso);

        return new Resposta(novoCurso.idTexto(), novoCurso.nomeTexto(),
                coordenador.matriculaTexto(), coordenador.nomeTexto());
    }

    // FLUXOS ESPECIALIZADOS ---------------------------------------------------

    private void validarUnicidadeNome(Nome nome) {
        cursoRepositorio.buscarPorNome(nome).ifPresent(curso -> {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_002_REGISTRO_DUPLICADO, "nome do curso"
            );
        });
    }

    private Professor buscarEValidarCoordenador(Matricula matricula) {
        Professor professor = professorRepositorio.buscarPorMatricula(matricula)
                .orElseThrow(() -> new GenericaExcecao(
                CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "professor"));

        if(!professor.podeSerCoordenadorCurso()) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO, "professor",
                    "coordenador de curso");
        }

        if(professor.statusContaUsuario() != StatusContaUsuario.ATIVO) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "conta de usuário do professor",
                    "conta ativa");
        }

        return professor;
    }

    private ParametrosCurso gerarParametros(Comando comando) {
        List<AjusteTipoTg> ajustes = comando.ajustes().stream()
                .map(a -> new AjusteTipoTg(a.tipoTg(), a.maxAlunosGrupo()))
                .toList();

        return new ParametrosCurso(comando.turnos(), comando.disciplinas(),
                ajustes);
    }
}