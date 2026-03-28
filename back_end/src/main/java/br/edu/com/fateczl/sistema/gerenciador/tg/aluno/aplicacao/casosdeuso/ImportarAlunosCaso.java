package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.portas.LeitorArquivoAlunos;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.AlunoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.servicos.ValidadorAutorizacaoImportacao;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.servicos.ValidadorCabecalhoArquivo;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
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
    private final ValidadorAutorizacaoImportacao validadorAutorizacao;
    private final ValidadorCabecalhoArquivo validadorCabecalho;

    public ImportarAlunosCaso(
            AlunoRepositorio alunoRepositorio,
            ProfessorRepositorio professorRepositorio,
            TurmaRepositorio turmaRepositorio,
            LeitorArquivoAlunos leitorArquivo,
            ValidadorAutorizacaoImportacao validadorAutorizacao,
            ValidadorCabecalhoArquivo validadorCabecalho
    ) {
        this.alunoRepositorio = alunoRepositorio;
        this.professorRepositorio = professorRepositorio;
        this.turmaRepositorio = turmaRepositorio;
        this.leitorArquivo = leitorArquivo;
        this.validadorAutorizacao = validadorAutorizacao;
        this.validadorCabecalho = validadorCabecalho;
    }

    public record AlunoImportado(String id, String nome, String matricula) {}
    public record Comando(
            String idTurma,
            InputStream arquivoBruto,
            String emailAutor
    ) {}
    public record Resposta(List<AlunoImportado> alunos) {}

    // FLUXO PRINCIPAL ---------------------------------------------------------

    public Resposta executar(Comando comando) {
        Professor autor = buscarProfessorPorEmail(
                new Email(comando.emailAutor())
        );
        Turma turma = buscarTurma(
                new TurmaId(UUID.fromString(comando.idTurma()))
        );

        validadorAutorizacao.validar(autor, turma);
        var arquivoLido = leitorArquivo.ler(comando.arquivoBruto());
        validadorCabecalho.validar(arquivoLido, turma);

        List<AlunoImportado> alunosImportados = processarEGravarAlunos(
                arquivoLido.alunos(),
                turma
        );

        return new Resposta(alunosImportados);
    }

    // FLUXOS ESPECIALIZADOS ---------------------------------------------------

    private Professor buscarProfessorPorEmail(Email email) {
        return professorRepositorio.buscarPorEmail(email)
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                        "professor autor"
                ));
    }

    private Turma buscarTurma(TurmaId turmaId) {
        return turmaRepositorio.buscarPorId(turmaId)
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                        "turma de destino"
                ));
    }

    private List<AlunoImportado> processarEGravarAlunos(
            List<LeitorArquivoAlunos.DadosAluno> alunosArquivo,
            Turma turma
    ) {
        List<AlunoImportado> alunos = new ArrayList<>();

        for(var dado : alunosArquivo) {
            Matricula matricula = new Matricula(dado.matricula());
            Nome nome = new Nome(dado.nome());

            Aluno aluno = alunoRepositorio.buscarPorMatricula(matricula)
                    .orElse(null);

            if(aluno != null) {
                aluno.matricularEmTurma(turma.id());
            } else {
                aluno = Aluno.novo(
                        new AlunoId(UUID.randomUUID()),
                        nome,
                        matricula,
                        turma.id()
                );
            }

            alunoRepositorio.salvar(aluno);

            alunos.add(new AlunoImportado(
                    aluno.idTexto(),
                    aluno.nomeTexto(),
                    aluno.matriculaTexto()
            ));
        }
        return alunos;
    }
}