package br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.servicos.ValidadorCoordenadorCurso;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.Ano;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.PeriodoLetivo;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.Semestre;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.servicos.ValidadorComposicaoTurma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.servicos.VerificadorUnicidadeTurma;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class GerarTurmaCaso {
    private final TurmaRepositorio turmaRepositorio;
    private final CursoRepositorio cursoRepositorio;
    private final ProfessorRepositorio professorRepositorio;
    private final ValidadorComposicaoTurma validadorComposicao;
    private final VerificadorUnicidadeTurma verificadorUnicidade;
    private final ValidadorCoordenadorCurso validadorCoordenador;

    public GerarTurmaCaso(
            TurmaRepositorio turmaRepositorio,
            CursoRepositorio cursoRepositorio,
            ProfessorRepositorio professorRepositorio,
            ValidadorComposicaoTurma validadorComposicao,
            VerificadorUnicidadeTurma verificadorUnicidade,
            ValidadorCoordenadorCurso validadorCoordenador
    ) {
        this.turmaRepositorio = turmaRepositorio;
        this.cursoRepositorio = cursoRepositorio;
        this.professorRepositorio = professorRepositorio;
        this.validadorComposicao = validadorComposicao;
        this.verificadorUnicidade = verificadorUnicidade;
        this.validadorCoordenador = validadorCoordenador;
    }

    public record Comando(
            String matriculaProfessorTg,
            Disciplina disciplina,
            Turno turno,
            Integer ano,
            Integer semestre
    ) {}

    public record Resposta(
            String id,
            String nomeCurso,
            Disciplina disciplina,
            Turno turno,
            String nomeProfessorTg,
            Integer ano,
            Integer semestre
    ) {}

    // FLUXO PRINCIPAL ---------------------------------------------------------

    public Resposta executar(Comando comando) {
        // Pega o email de quem fez a requisição direto do Token JWT validado
        String emailDoUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        Matricula matriculaProfessorTg = new Matricula(
                comando.matriculaProfessorTg()
        );
        PeriodoLetivo periodoLetivo = new PeriodoLetivo(
                new Ano(comando.ano()),
                new Semestre(comando.semestre())
        );

        Professor coordenador = buscarCoordenador(new Email(emailDoUsuarioLogado));
        validadorCoordenador.validar(coordenador);

        Curso curso = buscarCursoDoCoordenador(coordenador.id());
        Professor professorTg = buscarProfessorTg(matriculaProfessorTg);

        validadorComposicao.validar(
                curso,
                professorTg,
                comando.disciplina(),
                comando.turno()
        );

        verificadorUnicidade.verificar(
                curso.id(),
                comando.disciplina(),
                comando.turno(),
                periodoLetivo
        );

        Turma novaTurma = Turma.novo(
                new TurmaId(UUID.randomUUID()),
                curso.id(),
                comando.disciplina(),
                comando.turno(),
                periodoLetivo,
                professorTg.id()
        );

        turmaRepositorio.salvar(novaTurma);

        return new Resposta(
                novaTurma.idTexto(),
                curso.nomeTexto(),
                novaTurma.disciplina(),
                novaTurma.turno(),
                professorTg.nomeTexto(),
                periodoLetivo.anoValor(),
                periodoLetivo.semestreValor()
        );
    }

    // FLUXOS ESPECIALIZADOS ---------------------------------------------------

    private Professor buscarCoordenador(Email email) {
        return professorRepositorio.buscarPorEmail(email)
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                        "professor"
                ));
    }

    private Professor buscarProfessorTg(Matricula matricula) {
        return professorRepositorio.buscarPorMatricula(matricula)
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                        "professor de TG"
                ));
    }

    private Curso buscarCursoDoCoordenador(ProfessorId coordenadorId) {
        return cursoRepositorio.buscarPorCoordenadorId(coordenadorId)
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                        "curso associado a este coordenador"
                ));
    }
}