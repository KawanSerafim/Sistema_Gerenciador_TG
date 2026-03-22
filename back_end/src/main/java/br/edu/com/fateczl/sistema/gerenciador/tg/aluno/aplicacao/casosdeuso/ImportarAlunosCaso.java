package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.portas.LeitorArquivoAlunos;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.AlunoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ImportarAlunosCaso {
    private final AlunoRepositorio alunoRepositorio;
    private final ProfessorRepositorio professorRepositorio;
    private final TurmaRepositorio turmaRepositorio;
    private final LeitorArquivoAlunos leitorArquivo;

    public ImportarAlunosCaso(AlunoRepositorio alunoRepositorio,
                              ProfessorRepositorio professorRepositorio,
                              TurmaRepositorio turmaRepositorio,
                              LeitorArquivoAlunos leitorArquivo) {
        this.alunoRepositorio = alunoRepositorio;
        this.professorRepositorio = professorRepositorio;
        this.turmaRepositorio = turmaRepositorio;
        this.leitorArquivo = leitorArquivo;
    }

    public record AlunoImportado(String id, String nome, String matricula) {}
    public record Comando(
            String idTurma,
            InputStream arquivoBruto,
            String emailAutor
    ) {}
    public record Resposta(
            String idTurma,
            String nomeCurso,
            Turno turno,
            Disciplina disciplina,
            Integer ano,
            Integer semestre,
            List<AlunoImportado> alunos
    ) {}

    // FLUXO PRINCIPAL ---------------------------------------------------------

    public Resposta executar(Comando comando) {
        TurmaId idTurma = new TurmaId(UUID.fromString(comando.idTurma()));
        Email emailAutor = new Email(comando.emailAutor());

        Turma turma = buscarTurma(idTurma);
        validarAutorizacao(emailAutor, turma);

        var dadosArquivo = leitorArquivo.ler(comando.arquivoBruto());
        validarMetadadosArquivo(dadosArquivo, turma);

        List<AlunoImportado> alunos = processarEGravarAlunos(
                dadosArquivo.alunos(), turma);

        return new Resposta(
                turma.idTexto(),
                turma.nomeCursoTexto(),
                turma.turno(),
                turma.disciplina(),
                turma.anoLetivoValor(),
                turma.semestreLetivoValor(),
                alunos
        );
    }

    // FLUXOS ESPECIALIZADOS ---------------------------------------------------

    private Turma buscarTurma(TurmaId id) {
        return turmaRepositorio.buscarPorId(id)
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "turma"));
    }

    private void validarAutorizacao(Email emailAutor, Turma turma) {
        Professor autor = professorRepositorio.buscarPorEmail(emailAutor)
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                        "professor requisitante"));

        boolean isProfessorTgDaTurma = turma.idProfessorTg().equals(autor.id());
        boolean isCoordenadorDoCurso = turma.idCoordenadorCurso()
                .equals(autor.id());

        if(!isProfessorTgDaTurma && !isCoordenadorDoCurso) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "professor requisitante", "professor de TG ou coordenador" +
                    " de curso");
        }
    }

    private void validarMetadadosArquivo(
            LeitorArquivoAlunos.DadosArquivo arquivo, Turma turma) {
        if(!turma.anoLetivoValor().equals(arquivo.ano())
                || !turma.semestreLetivoValor().equals(arquivo.semestre())
                || !turma.turno().equals(arquivo.turno())) {
            throw new ValidacaoExcecao(CodigoErro.VD_007_CAMPO_NAO_SUPORTADO,
                    "cabeçalho do arquivo", "os dados do ano, semestre ou " +
                    "turno não correspondem à turma selecionada");
        }
    }

    private List<AlunoImportado> processarEGravarAlunos(
            List<LeitorArquivoAlunos.DadosAluno> alunosArquivo, Turma turma) {
        List<AlunoImportado> alunos = new ArrayList<>();

        for(var dado : alunosArquivo) {
            Matricula matricula = new Matricula(dado.matricula());
            Nome nome = new Nome(dado.nome());

            Aluno aluno = alunoRepositorio.buscarPorMatricula(matricula)
                    .orElse(null);

            if(aluno != null) {
                aluno.matricularEmTurma(turma);
            } else {
                aluno = Aluno.novo(new AlunoId(UUID.randomUUID()), nome,
                        matricula, turma);
            }

            alunoRepositorio.salvar(aluno);
            alunos.add(new AlunoImportado(aluno.idTexto(), aluno.nomeTexto(),
                    aluno.matriculaTexto()));
        }
        return alunos;
    }
}