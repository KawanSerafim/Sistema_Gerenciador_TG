package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
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
import java.util.stream.Stream;

public class BuscarVisaoGruposProfessorCaso {

        private final GrupoTgRepositorio grupoTgRepositorio;
        private final AlunoRepositorio alunoRepositorio;
        private final ProfessorRepositorio professorRepositorio;
        private final TurmaRepositorio turmaRepositorio;

        public BuscarVisaoGruposProfessorCaso(
                GrupoTgRepositorio grupoTgRepositorio,
                AlunoRepositorio alunoRepositorio,
                ProfessorRepositorio professorRepositorio,
                TurmaRepositorio turmaRepositorio) {
            this.grupoTgRepositorio = grupoTgRepositorio;
            this.alunoRepositorio = alunoRepositorio;
            this.professorRepositorio = professorRepositorio;
            this.turmaRepositorio = turmaRepositorio;
        }

        // Comando recebe o ID da conta do professor logado (via JWT)
        public record Comando(String emailUsuarioLogado) {}

        public record IntegranteDTO(String id, String nome) {}

        public record GrupoResumoDTO(
                String idGrupo,
                String tipoTg,
                String tema,
                String nomeOrientador,
                String turmaId,
                List<IntegranteDTO> integrantes
        ) {}

        public record Resposta(List<GrupoResumoDTO> grupos) {}

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
                return new Resposta(Collections.emptyList());
            }

            List<TurmaId> idsDasTurmasAtivas = turmasAtivasDoProfessor.stream().map(Turma::id).toList();

            // =========================================================
            //  BUSCA OS GRUPOS FORMADOS
            // =========================================================
            List<GrupoTg> grupos = grupoTgRepositorio.buscarPorTurmasIds(idsDasTurmasAtivas);

            List<GrupoResumoDTO> dtosGrupos = grupos.stream().map(grupo -> {
                String nomeOrientador = "Sem orientador";
                if (grupo.orientadorId() != null) {
                    nomeOrientador = professorRepositorio.buscarPorId(grupo.orientadorId())
                            .map(Professor::nomeTexto)
                            .orElse("Orientador não encontrado");
                }

                List<IntegranteDTO> integrantes = grupo.alunosIds().stream()
                        .map(alunoRepositorio::buscarPorId)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(aluno -> new IntegranteDTO(aluno.idTexto(), aluno.nomeTexto()))
                        .toList();

                return new GrupoResumoDTO(
                        grupo.idTexto(),
                        grupo.tipoTg() != null ? grupo.tipoTg().name() : "NÃO_DEFINIDO",
                        grupo.temaTg() != null ? grupo.temaTg().nome() : "Sem tema definido",
                        nomeOrientador,
                        "TURMA_ATIVA", // Simplificado: você pode cruzar com o ID real depois
                        integrantes
                );
            }).toList();

            // =========================================================
            // BUSCA OS ALUNOS SEM GRUPO
            // =========================================================
            List<GrupoResumoDTO> dtosAlunosAvulsos = buscaAlunosSemGrupos(idsDasTurmasAtivas);


            // =========================================================
            // JUNTA TUDO E RETORNA
            // =========================================================
            List<GrupoResumoDTO> listaFinal = Stream.concat(dtosGrupos.stream(), dtosAlunosAvulsos.stream())
                    .toList();

            return new Resposta(listaFinal);
        }

        // METODOS AUXILIARES

        private List<GrupoResumoDTO> buscaAlunosSemGrupos(List<TurmaId> idsDasTurmasAtivas){
            List<Aluno> alunosSemGrupo = alunoRepositorio.buscarSemGrupoPorTurmasIds(idsDasTurmasAtivas);
            // Cria um DTO falso representando um aluno sem grupo
            List<GrupoResumoDTO> listaDtos = new ArrayList<>();
            for (Aluno aluno : alunosSemGrupo) {
                GrupoResumoDTO turmaAtiva = new GrupoResumoDTO(
                        "-", // idGrupo falso
                        "",  // Sem tipo
                        "",  // Sem tema
                        "",  // Sem orientador
                        "TURMA_ATIVA",
                        // O aluno sozinho na lista
                        List.of(new IntegranteDTO(aluno.idTexto(), aluno.nomeTexto()))
                );
                listaDtos.add(turmaAtiva);
            }
            return listaDtos;
        }

    }
