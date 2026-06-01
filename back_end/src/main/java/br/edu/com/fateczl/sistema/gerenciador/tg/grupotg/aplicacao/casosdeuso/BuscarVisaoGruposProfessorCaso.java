package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.entidade.Banca;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.repositorio.BancaRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Pagina;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Caso de uso da visão de grupos das turmas do professor TG
 */
public class BuscarVisaoGruposProfessorCaso {

        private final GrupoTgRepositorio grupoTgRepositorio;
        private final AlunoRepositorio alunoRepositorio;
        private final ProfessorRepositorio professorRepositorio;
        private final TurmaRepositorio turmaRepositorio;
        private final BancaRepositorio bancaRepositorio;

        public BuscarVisaoGruposProfessorCaso(
                GrupoTgRepositorio grupoTgRepositorio,
                AlunoRepositorio alunoRepositorio,
                ProfessorRepositorio professorRepositorio,
                TurmaRepositorio turmaRepositorio,
                BancaRepositorio bancaRepositorio) {
            this.grupoTgRepositorio = grupoTgRepositorio;
            this.alunoRepositorio = alunoRepositorio;
            this.professorRepositorio = professorRepositorio;
            this.turmaRepositorio = turmaRepositorio;
            this.bancaRepositorio = bancaRepositorio;
        }

        // Comando recebe o email da conta do professor logado (via JWT)
        public record Comando(
                String emailUsuarioLogado,
                Boolean somenteSemGrupo,
                Integer pagina,
                Integer tamanho
        ) {}

        public record IntegranteDTO(String id, String nome) {}

        //O record Pagina que você criou
        public record GrupoResumoDTO(
                String tipoTg,
                String tema,
                String nomeOrientador,
                String turmaId,
                List<IntegranteDTO> integrantes,
                String situacao
        ) {}
        // Resposta embrulhando a página
        public record Resposta(Pagina<GrupoResumoDTO> grupos) {}

        public Resposta executar(Comando comando) {
            // Busca o Professor logado
            Professor professorLogado = professorRepositorio.buscarPorEmail(new Email(comando.emailUsuarioLogado()))
                    .orElseThrow(() -> new RegraNegocioExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "Professor"));

            // Determina ano e semestre atuais para filtrar as turmas ativas
            int anoAtual = LocalDate.now().getYear();
            int semestreAtual = LocalDate.now().getMonthValue() <= 6 ? 1 : 2;

            List<Turma> turmasAtivasDoProfessor = turmaRepositorio.buscarPorProfessorTgId(professorLogado.id()).stream()
                    .filter(t -> t.anoLetivoValor() == anoAtual && t.semestreLetivoValor() == semestreAtual)
                    .toList();


            if (turmasAtivasDoProfessor.isEmpty()) {
                return new Resposta(new Pagina<>(Collections.emptyList(), comando.pagina(), 0, 0L));
            }

            List<TurmaId> idsDasTurmasAtivas = turmasAtivasDoProfessor.stream().map(Turma::id).toList();

            // =========================================================
            //  BUSCA OS GRUPOS FORMADOS
            // =========================================================
            List<GrupoResumoDTO> listaCompleta = new ArrayList<>();

            // Só busca os grupos formados se o switch de "somente alunos sem grupo" estiver DESLIGADO
            if (Boolean.FALSE.equals(comando.somenteSemGrupo())) {
                List<GrupoTg> grupos = grupoTgRepositorio.buscarPorTurmasIds(idsDasTurmasAtivas);

                // Busca todas as bancas desses grupos em lote para evitar N+1
                List<Banca> bancasGrupos = buscaBancasDosGrupos(grupos);

                List<GrupoResumoDTO> dtosGrupos = grupos.stream().map(grupo -> {
                    String nomeOrientador = grupo.orientadorId() != null ?
                            professorRepositorio.buscarPorId(grupo.orientadorId()).map(Professor::nomeTexto)
                                    .orElse("Orientador não encontrado") : "Sem orientador";

                    List<IntegranteDTO> integrantes = grupo.alunosIds().stream()
                            .map(alunoRepositorio::buscarPorId)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .map(aluno -> new IntegranteDTO(aluno.idTexto(), aluno.nomeTexto()))
                            .toList();

                    // Localiza a banca do grupo atual
                    Optional<Banca> bancaGrupo = bancasGrupos.stream()
                            .filter(banca -> banca.grupoId().equals(grupo.id()))
                            .findFirst();

                    String situacao = mapearSituacaoGrupo(bancaGrupo);

                    return new GrupoResumoDTO(
                            grupo.tipoTg() != null ? grupo.tipoTg().name() : "NÃO_DEFINIDO",
                            grupo.temaTg() != null ? grupo.temaTg().nome() : "Sem tema definido",
                            nomeOrientador,
                            "TURMA_ATIVA",
                            integrantes,
                            situacao
                    );
                }).toList();
                listaCompleta.addAll(dtosGrupos);
            }


            // =========================================================
            // BUSCA OS ALUNOS SEM GRUPO
            // =========================================================
            List<GrupoResumoDTO> dtosAlunosAvulsos = buscaAlunosSemGrupos(idsDasTurmasAtivas);
            listaCompleta.addAll(dtosAlunosAvulsos);

            // =========================================================
            // LÓGICA DE PAGINAÇÃO EM MEMÓRIA
            // =========================================================
            int totalElementos = listaCompleta.size();
            int totalPaginas = (int) Math.ceil((double) totalElementos / comando.tamanho());

            int inicio = comando.pagina() * comando.tamanho();
            int fim = Math.min(inicio + comando.tamanho(), totalElementos);

            List<GrupoResumoDTO> conteudoPaginado = inicio < totalElementos ?
                    listaCompleta.subList(inicio, fim) : Collections.emptyList();

            Pagina<GrupoResumoDTO> pagina = new Pagina<>(
                    conteudoPaginado,
                    comando.pagina(),
                    totalPaginas,
                    (long) totalElementos);

            return new Resposta(pagina);
        }

        // METODOS AUXILIARES


    private List<GrupoResumoDTO> buscaAlunosSemGrupos(List<TurmaId> idsDasTurmasAtivas){
        List<Aluno> alunosSemGrupo = alunoRepositorio.buscarSemGrupoPorTurmasIds(idsDasTurmasAtivas);
        List<GrupoResumoDTO> listaDtos = new ArrayList<>();
        for (Aluno aluno : alunosSemGrupo) {
            listaDtos.add(new GrupoResumoDTO(
                    "", "", "", "TURMA_ATIVA",
                    List.of(new IntegranteDTO(aluno.idTexto(), aluno.nomeTexto())),
                    "Formação pendente"
            ));
        }
        return listaDtos;
    }

    private List<Banca> buscaBancasDosGrupos(List<GrupoTg> grupos) {
        if (grupos.isEmpty()) {
            return Collections.emptyList();
        }
        List<GrupoTgId> grupoTgIds = grupos.stream().map(GrupoTg::id).toList();
        return bancaRepositorio.buscarPorGruposId(grupoTgIds);
    }

    private String mapearSituacaoGrupo(Optional<Banca> bancaOpt) {
        if (bancaOpt.isEmpty()) {
            return "Grupo sem banca marcada";
        }

        Banca banca = bancaOpt.get();
        if ("AVALIADA".equals(banca.status().name())) {
            //Retorna a nota com 2 casas decimais
            return "Avaliado - Nota: " + String.format("%.2f", banca.notaFinal());
        }

        // Retorna o status amigável da banca (AGENDADA, CONFIRMADA, etc.)
        return "Banca " + banca.status().name();
    }

    }
