package br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.infraestrutura.persistencia.jpa.repositorio;

import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.entidade.SolicitacaoOrientacao;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.objetosvalor.StatusSolicitacao;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.repositorio.SolicitacaoOrientacaoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.infraestrutura.persistencia.jpa.mapeador.SolicitacaoOrientacaoMapeador;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.infraestrutura.persistencia.jpa.modelo.SolicitacaoOrientacaoModelo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SolicitacaoOrientacaoRepositorioImpl implements SolicitacaoOrientacaoRepositorio {

    private final SolicitacaoOrientacaoJpaRepositorio repositorio;

    @Override
    @Transactional
    public void salvar(SolicitacaoOrientacao solicitacao) {
        var modelo = SolicitacaoOrientacaoMapeador.paraModelo(solicitacao);
        repositorio.save(modelo);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeSolicitacaoPendenteParaGrupo(String grupoIdTexto) {
        return repositorio.existsByGrupoIdAndStatus(grupoIdTexto, StatusSolicitacao.PENDENTE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitacaoOrientacao> buscarPendentesPorProfessor(String professorIdTexto) {
        // Busca os modelos no banco de dados (Entidades JPA)
        List<SolicitacaoOrientacaoModelo> modelos = repositorio.findByProfessorIdAndStatus(
                professorIdTexto,
                StatusSolicitacao.PENDENTE
        );

        // Converte a lista de Modelos JPA para a lista de Entidades de Domínio Rico
        return modelos.stream()
                .map(SolicitacaoOrientacaoMapeador::paraDominio)
                .toList();
    }
}
