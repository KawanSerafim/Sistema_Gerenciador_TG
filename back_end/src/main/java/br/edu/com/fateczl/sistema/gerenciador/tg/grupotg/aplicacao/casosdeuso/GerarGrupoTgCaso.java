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
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.TemaTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.servicos.ValidadorComposicaoGrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class GerarGrupoTgCaso {
    private final GrupoTgRepositorio grupoTgRepositorio;
    private final CursoRepositorio cursoRepositorio;
    private final AlunoRepositorio alunoRepositorio;
    private final TurmaRepositorio turmaRepositorio;
    private final ValidadorComposicaoGrupoTg validadorComposicao;

    public GerarGrupoTgCaso(
            GrupoTgRepositorio grupoTgRepositorio,
            CursoRepositorio cursoRepositorio,
            AlunoRepositorio alunoRepositorio,
            TurmaRepositorio turmaRepositorio,
            ValidadorComposicaoGrupoTg validadorComposicao
    ) {
        this.grupoTgRepositorio = grupoTgRepositorio;
        this.cursoRepositorio = cursoRepositorio;
        this.alunoRepositorio = alunoRepositorio;
        this.turmaRepositorio = turmaRepositorio;
        this.validadorComposicao = validadorComposicao;
    }

    public record Comando(
            String idContaUsuario,
            String tema,
            String descricaoTema,
            TipoTg tipoTg,
            List<String> matriculasAlunos
    ) {}

    // FLUXO PRINCIPAL ---------------------------------------------------------

    public void executar(Comando comando) {
        // Busca o autor baseado no ID da conta que veio no JWT
        Aluno alunoAutor = buscarAlunoAutor(comando.idContaUsuario());

        // Lógica de Data para Turma Atual
        int anoAtual = LocalDate.now().getYear();
        int mesAtual = LocalDate.now().getMonthValue();
        int semestreAtual = mesAtual <= 6 ? 1 : 2;

        // Busca os detalhes das Turmas que o aluno está vinculado
        List<Turma> turmasDoAluno = turmaRepositorio.buscarTodasPorIds(alunoAutor.turmasIds()
                .stream().collect(Collectors.toUnmodifiableSet()));

        // Filtra apenas as turmas do semestre e ano vigentes
        List<Turma> turmasAtuais = turmasDoAluno.stream()
                .filter(t -> t.anoLetivoValor() == anoAtual && t.semestreLetivoValor() == semestreAtual)
                .toList();

        if (turmasAtuais.isEmpty()) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "aluno autor",
                    "matriculado no semestre atual"
            );
        }

        // 5. Extrai o ID do Curso (pressupõe que as turmas atuais pertencem ao mesmo curso)
        String idCurso = turmasAtuais.get(0).cursoId().texto();
        Curso curso = buscarCurso(idCurso);

        // 6. Extrai o Set de Disciplinas (Se ele estiver em TG1 e TG2, as duas entram no Set)
        Set<Disciplina> disciplinasAtuais = turmasAtuais.stream()
                .map(Turma::disciplina)
                .collect(Collectors.toSet());

        TemaTg tema = new TemaTg(comando.tema(), comando.descricaoTema());

        List<Aluno> alunos = buscarEValidarAlunos(comando.matriculasAlunos());

        validadorComposicao.validar(
                curso,
                comando.tipoTg(),
                disciplinasAtuais,
                alunos
        );

        List<AlunoId> alunoIds = alunos.stream().map(Aluno::id).toList();

        GrupoTg novoGrupo = GrupoTg.novo(
                new GrupoTgId(UUID.randomUUID()),
                curso.id(),
                disciplinasAtuais,
                tema,
                comando.tipoTg(),
                alunoIds
        );

        grupoTgRepositorio.salvar(novoGrupo);
    }

    // FLUXOS ESPECIALIZADOS ---------------------------------------------------

    private Aluno buscarAlunoAutor(String id){
        return  alunoRepositorio.buscarPorContaId(new ContaUsuarioId(UUID.fromString(id)))
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "aluno autor"));
    }

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