package br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.infraestrutura.persistencia.jpa.mapeador;

import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.entidade.SolicitacaoOrientacao;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.objetosvalor.SolicitacaoOrientacaoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.infraestrutura.persistencia.jpa.modelo.SolicitacaoOrientacaoModelo;

import java.util.UUID;

public class SolicitacaoOrientacaoMapeador {
    // Transforma a Entidade de Negócio na Entidade do Banco (Para Salvar)
    public static SolicitacaoOrientacaoModelo paraModelo(SolicitacaoOrientacao dominio) {
        return new SolicitacaoOrientacaoModelo(
                dominio.idTexto(),
                dominio.grupoIdTexto(),
                dominio.professorIdTexto(),
                dominio.status(),
                dominio.dataCriacao()
        );
    }

    // Transforma a Entidade do Banco na Entidade de Negócio (Para Ler e usar nas regras)
    public static SolicitacaoOrientacao paraDominio(SolicitacaoOrientacaoModelo modelo) {
        return SolicitacaoOrientacao.carregar(
                new SolicitacaoOrientacaoId(UUID.fromString(modelo.getId())),
                new GrupoTgId(UUID.fromString(modelo.getGrupoId())),
                new ProfessorId(UUID.fromString(modelo.getProfessorId())),
                modelo.getStatus(),
                modelo.getDataCriacao()
        );
    }
}
