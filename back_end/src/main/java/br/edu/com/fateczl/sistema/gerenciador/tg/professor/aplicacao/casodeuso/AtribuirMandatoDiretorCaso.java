package br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.administrador.dominio.repositorio.AdministradorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.MandatoDiretor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.MandatoDiretorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.MandatoDiretorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

public class AtribuirMandatoDiretorCaso {
    private final ProfessorRepositorio professorRepositorio;
    private final MandatoDiretorRepositorio mandatoDiretorRepositorio;
    private final AdministradorRepositorio administradorRepositorio;

    public AtribuirMandatoDiretorCaso(
            ProfessorRepositorio professorRepositorio,
            MandatoDiretorRepositorio mandatoDiretorRepositorio,
            AdministradorRepositorio administradorRepositorio) {
        this.professorRepositorio = professorRepositorio;
        this.mandatoDiretorRepositorio = mandatoDiretorRepositorio;
        this.administradorRepositorio = administradorRepositorio;
    }

    public record Comando(
            String emailAdministradorLogado,
            String matriculaProfessor,
            LocalDate dataInicio,
            LocalDate dataFim,
            String assinaturaBase64
    ) {}

    @Transactional
    public void executar(Comando comando) {
        //Apenas adminsitradores podem atribuir mandatos ao professor
        administradorRepositorio.buscarPorEmail
                (new Email(comando.emailAdministradorLogado()))
                .orElseThrow(() -> new RegraNegocioExcecao(CodigoErro.AU_003_ACAO_NAO_PERMITIDA_MOTIVO,
                        "atribuir mandato diretor a professor",
                        "usuário logado não tem permissão de administrador"));

        // Garante que o professor existe no sistema
        Professor professor = professorRepositorio.buscarPorMatricula(new Matricula(comando.matriculaProfessor()))
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "Professor"));

        // Validação cronológica
        if (comando.dataFim() != null && comando.dataInicio().isAfter(comando.dataFim())) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "Mandato",
                    "Data de início não posterior à data de fim."
            );
        }

        // Regra de Negócio: Impede que a instituição tenha múltiplos diretores vigentes simultaneamente
        mandatoDiretorRepositorio.buscarMandatoVigente().ifPresent(mandatoAtivo -> {
            // Se o mandato ativo for de OUTRO professor, lançamos erro
            if (!mandatoAtivo.professorId().equals(professor.id())) {
                throw new RegraNegocioExcecao(
                        CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                        "Mandato Diretor",
                        "sem mandato vigente na faculdade"
                );
            }
        });

        // Instancia e persiste o novo mandato
        MandatoDiretor novoMandato = MandatoDiretor.novo(
                new MandatoDiretorId(UUID.randomUUID()),
                professor.id(),
                comando.dataInicio(),
                comando.dataFim(),
                comando.assinaturaBase64()
        );

        mandatoDiretorRepositorio.salvar(novoMandato);
    }
}
