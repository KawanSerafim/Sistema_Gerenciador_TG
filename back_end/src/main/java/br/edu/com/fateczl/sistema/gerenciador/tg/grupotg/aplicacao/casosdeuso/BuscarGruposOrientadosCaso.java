package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.AlunoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;

import java.util.List;

public class BuscarGruposOrientadosCaso {
    private final ProfessorRepositorio professorRepositorio;
    private final GrupoTgRepositorio grupoTgRepositorio;
    private final AlunoRepositorio alunoRepositorio;
    private final TurmaRepositorio turmaRepositorio;

    public BuscarGruposOrientadosCaso(
            ProfessorRepositorio professorRepositorio,
            GrupoTgRepositorio grupoTgRepositorio,
            AlunoRepositorio alunoRepositorio,
            TurmaRepositorio turmaRepositorio) {
        this.professorRepositorio = professorRepositorio;
        this.grupoTgRepositorio = grupoTgRepositorio;
        this.alunoRepositorio = alunoRepositorio;
        this.turmaRepositorio = turmaRepositorio;
    }

    public record Comando(
            String emailOrientadorLogado,
            //Ano e semestre são opcionais, se vierem vazios carrega a lista de todos os grupos
            Integer ano,
            Integer semestre
    ) {}

    // DTO de Saída
    public record GrupoOrientadoDTO(
            String idGrupo,
            String tema,
            String tipoTg,
            Integer ano,
            Integer semestre,
            List<String> nomesAlunos
    ) {}
    // Record auxiliar local para transporte temporário de dados
    private record Periodo(Integer ano, Integer semestre) {}

    public List<GrupoOrientadoDTO> executar(Comando comando) {
        Email emailOrientador = new Email(comando.emailOrientadorLogado());
        Professor orientador = professorRepositorio.buscarPorEmail(emailOrientador)
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "orientador"));

        List<GrupoTg> grupos = grupoTgRepositorio.buscarPorOrientadorId(orientador.id());

        // Mapeia as entidades puras para o DTO de visualização
        return grupos.stream()
                .map(grupo -> {
                    // Método auxiliar para navegar no grafo (Grupo -> Aluno -> Turma)
                    Periodo periodo = descobrirPeriodoDoGrupo(grupo);

                    return new GrupoOrientadoDTO(
                            grupo.idTexto(),
                            grupo.nomeTemaTg(),
                            grupo.tipoTg().name(),
                            periodo.ano(),
                            periodo.semestre(),
                            grupo.alunosIds().stream().map(AlunoId::texto).toList()
                    );
                })
                // 4. Filtra pelos parâmetros opcionais que vieram do Frontend
                .filter(dto -> comando.ano() == null || dto.ano().equals(comando.ano()))
                .filter(dto -> comando.semestre() == null ||
                        dto.semestre().equals(comando.semestre()))
                .toList();
    }

    /**
     * Descobre o período do TG olhando apenas para a turma do PRIMEIRO integrante.
     * Como todos os integrantes de um grupo compartilham o mesmo calendário,
     * isso otimiza drasticamente as idas ao banco de dados.
     */
    private Periodo descobrirPeriodoDoGrupo(GrupoTg grupo) {
        // Prevenção de erro caso o grupo venha sem alunos (anomalia de banco)
        if (grupo.alunosIds().isEmpty()) {
            return new Periodo(null, null);
        }

        // A SUA SACADA: Pega apenas o primeiro aluno!
        AlunoId primeiroAlunoId = grupo.alunosIds().get(0);

        var alunoOpt = alunoRepositorio.buscarPorId(primeiroAlunoId);
        if (alunoOpt.isPresent()) {
            Aluno aluno = alunoOpt.get();

            // Varre as turmas desse aluno específico
            for (var turmaId : aluno.turmasIds()) {
                var turmaOpt = turmaRepositorio.buscarPorId(turmaId);

                if (turmaOpt.isPresent()) {
                    Turma turma = turmaOpt.get();
                    // Achou a primeira turma válida do aluno, já devolve o período!
                    // Como ele só tem um grupo ativo, asume que esta turma representa o período atual dele
                    return new Periodo(turma.anoLetivoValor(), turma.semestreLetivoValor());
                }
            }
        }

        // Se por algum motivo bizarro o aluno não tiver turma, retorna nulo para não quebrar a tela
        return new Periodo(null, null);
    }
}
