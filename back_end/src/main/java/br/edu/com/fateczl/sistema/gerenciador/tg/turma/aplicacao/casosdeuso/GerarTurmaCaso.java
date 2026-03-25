package br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.Ano;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.PeriodoLetivo;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.Semestre;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;

import java.util.UUID;

public class GerarTurmaCaso {
    private final TurmaRepositorio turmaRepositorio;
    private final CursoRepositorio cursoRepositorio;
    private final ProfessorRepositorio professorRepositorio;

    public GerarTurmaCaso(TurmaRepositorio turmaRepositorio,
                          CursoRepositorio cursoRepositorio,
                          ProfessorRepositorio professorRepositorio) {
        this.turmaRepositorio = turmaRepositorio;
        this.cursoRepositorio = cursoRepositorio;
        this.professorRepositorio = professorRepositorio;
    }

    public record Comando(
            String emailCoordenador,
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
        Email emailCoordenador = new Email(comando.emailCoordenador());
        Matricula matriculaProfessorTg = new Matricula(
                comando.matriculaProfessorTg());
        PeriodoLetivo periodoLetivo = new PeriodoLetivo(new Ano(comando.ano()),
                new Semestre(comando.semestre()));

        Professor coordenador = buscarEValidarCoordenador(emailCoordenador);
        Curso curso = buscarCursoDoCoordenador(coordenador.id());
        Professor professorTg = buscarEValidarProfessorTg(matriculaProfessorTg);

        if(!curso.validarDisciplina(comando.disciplina())) {
            throw new ValidacaoExcecao(CodigoErro.VD_007_CAMPO_NAO_SUPORTADO,
                    "disciplina", "o curso associado não o possui");
        }

        if(!curso.validarTurno(comando.turno())) {
            throw new ValidacaoExcecao(CodigoErro.VD_007_CAMPO_NAO_SUPORTADO,
                    "turno", "o curso associado não o possui");
        }

        validarUnicidadeTurma(curso.id(), comando.disciplina(),
                comando.turno(), periodoLetivo);

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

    private Professor buscarEValidarCoordenador(Email email) {
        Professor professor = professorRepositorio.buscarPorEmail(email)
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                        "professor"));

        if(!professor.podeSerCoordenadorCurso()) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO, "professor",
                    "coordenador de curso");
        }
        return professor;
    }

    private Professor buscarEValidarProfessorTg(Matricula matricula) {
        Professor professorTg = professorRepositorio
                .buscarPorMatricula(matricula)
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "professor" +
                        " de TG"));

        if(!professorTg.podeSerProfessorTg()) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO, "professor",
                    "professor de TG");
        }
        return professorTg;
    }

    private Curso buscarCursoDoCoordenador(ProfessorId coordenadorId) {
        return cursoRepositorio.buscarPorCoordenadorId(coordenadorId)
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                        "curso associado a este coordenador"));
    }

    private void validarUnicidadeTurma(CursoId cursoId, Disciplina disciplina,
                                       Turno turno,
                                       PeriodoLetivo periodoLetivo) {
        turmaRepositorio.buscarPorCursoIdEDisciplinaETurnoEAnoESemestre(cursoId,
                disciplina, turno, periodoLetivo).ifPresent(turma -> {
                    throw new RegraNegocioExcecao(
                            CodigoErro.RN_002_REGISTRO_DUPLICADO, "turma");
        });
    }
}