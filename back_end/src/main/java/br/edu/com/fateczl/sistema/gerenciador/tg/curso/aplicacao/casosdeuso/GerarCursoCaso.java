package br.edu.com.fateczl.sistema.gerenciador.tg.curso.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.AjusteTipoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.ParametrosCurso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.servicos.ValidadorCoordenadorCurso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.servicos.VerificadorUnicidadeCurso;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;

import java.util.List;
import java.util.UUID;

public class GerarCursoCaso {
    private final CursoRepositorio cursoRepositorio;
    private final ProfessorRepositorio professorRepositorio;
    private final VerificadorUnicidadeCurso verificadorUnicidade;
    private final ValidadorCoordenadorCurso validadorCoordenador;

    public GerarCursoCaso(
            CursoRepositorio cursoRepositorio,
            ProfessorRepositorio professorRepositorio,
            VerificadorUnicidadeCurso verificadorUnicidade,
            ValidadorCoordenadorCurso validadorCoordenador
    ) {
        this.cursoRepositorio = cursoRepositorio;
        this.professorRepositorio = professorRepositorio;
        this.verificadorUnicidade = verificadorUnicidade;
        this.validadorCoordenador = validadorCoordenador;
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
        verificadorUnicidade.verificar(nome);

        Matricula matriculaCoordenador = new Matricula(
                comando.matriculaCoordenador()
        );
        Professor coordenador = buscarCoordenador(matriculaCoordenador);
        validadorCoordenador.validar(coordenador);

        ParametrosCurso parametros = gerarParametros(comando);

        Curso novoCurso = Curso.novo(
                new CursoId(UUID.randomUUID()),
                nome,
                parametros,
                coordenador.id()
        );

        cursoRepositorio.salvar(novoCurso);

        return new Resposta(
                novoCurso.idTexto(),
                novoCurso.nomeTexto(),
                coordenador.matriculaTexto(),
                coordenador.nomeTexto()
        );
    }

    // FLUXOS ESPECIALIZADOS ---------------------------------------------------

    private Professor buscarCoordenador(Matricula matricula) {
        return professorRepositorio.buscarPorMatricula(matricula)
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                        "professor")
                );
    }

    private ParametrosCurso gerarParametros(Comando comando) {
        List<AjusteTipoTg> ajustes = comando.ajustes().stream()
                .map(a -> new AjusteTipoTg(a.tipoTg(), a.maxAlunosGrupo()))
                .toList();

        return new ParametrosCurso(
                comando.turnos(),
                comando.disciplinas(),
                ajustes
        );
    }
}