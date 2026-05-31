package br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.administrador.dominio.repositorio.AdministradorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.MandatoDiretor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.MandatoDiretorRepositorio;
import org.springframework.transaction.annotation.Transactional;

public class RetirarMandatoDiretorCaso {
    private final MandatoDiretorRepositorio mandatoDiretorRepositorio;
    private final AdministradorRepositorio administradorRepositorio;

    public RetirarMandatoDiretorCaso(
            MandatoDiretorRepositorio mandatoDiretorRepositorio,
            AdministradorRepositorio administradorRepositorio) {
        this.mandatoDiretorRepositorio = mandatoDiretorRepositorio;
        this.administradorRepositorio = administradorRepositorio;
    }

    public record Comando(String emailAdministradorLogado) {}

    @Transactional
    public void executar(Comando comando) {
        // Validação de Segurança: Apenas administradores podem retirar mandatos
        administradorRepositorio.buscarPorEmail(new Email(comando.emailAdministradorLogado()))
                .orElseThrow(() -> new RegraNegocioExcecao(CodigoErro.AU_003_ACAO_NAO_PERMITIDA_MOTIVO,
                        "retirar mandato de diretor",
                        "usuário logado não tem permissão de administrador"));

        // Busca o mandato que está ativo neste exato momento
        MandatoDiretor mandatoVigente = mandatoDiretorRepositorio.buscarMandatoVigente()
                .orElseThrow(() -> new RegraNegocioExcecao(
                        CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                        "Mandato de Diretor",
                        "ter professor com mandato vigente"
                ));

        // Executa a regra de negócio na entidade (Soft State Change)
        mandatoVigente.encerrar();

        // Salva a atualização no banco de dados
        mandatoDiretorRepositorio.salvar(mandatoVigente);
    }
}
