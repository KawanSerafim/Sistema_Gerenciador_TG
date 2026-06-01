package br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class FinalizarTurmasCaso {
    private final TurmaRepositorio turmaRepositorio;
    private final ProfessorRepositorio professorRepositorio;

    public FinalizarTurmasCaso(
            TurmaRepositorio turmaRepositorio,
            ProfessorRepositorio professorRepositorio
    ) {
        this.turmaRepositorio = turmaRepositorio;
        this.professorRepositorio = professorRepositorio;
    }

    public record Comando(
            String emailProfessorLogado,
            List<String> turmasIds // IDs das turmas selecionadas no frontend
    ) {}

    @Transactional // Garante que ou salva todas, ou não salva nenhuma
    public void executar(Comando comando) {
        // 1. Valida quem está logado
        Professor professorTg = professorRepositorio.buscarPorEmail(new Email(comando.emailProfessorLogado()))
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                        "Professor"));

        // Validação de segurança: Ele é realmente o Professor de TG?
         if (!professorTg.podeSerProfessorTg()) { throw new RegraNegocioExcecao(CodigoErro.AU_003_ACAO_NAO_PERMITIDA_MOTIVO,
                 "Finalizar turmas","Usuário logado não é professor TG"); }

        // 2. Busca todas as turmas enviadas pelo frontend
        Set<TurmaId> turmasSet = comando.turmasIds().stream()
                .map(turma -> new TurmaId(UUID.fromString(turma)))
                .collect(Collectors.toUnmodifiableSet());
        List<Turma> turmas = turmaRepositorio.buscarTodasPorIds(turmasSet);

        if (turmas.isEmpty()) {
            throw new RegraNegocioExcecao(CodigoErro.AU_003_ACAO_NAO_PERMITIDA_MOTIVO,
                "Finalizar turmas",
                    "Nenhuma turma válida foi selecionada para finalização.");
        }
        //Verifica se todas são do professor tg
        boolean turmasSaoDoProf = turmas.stream().allMatch(turma ->
            turma.professorTgIdTexto().equals(professorTg.idTexto()));

        if (!turmasSaoDoProf){
            throw new ValidacaoExcecao(CodigoErro.AU_003_ACAO_NAO_PERMITIDA_MOTIVO,
                    "Finalizar turmas", "Uma ou mais turma enviada não é do professor tg logado");
        }
        turmas.forEach(Turma::finalizar);

        // 4. Salva as atualizações no banco
        turmaRepositorio.salvarTodas(turmas);
    }
}
