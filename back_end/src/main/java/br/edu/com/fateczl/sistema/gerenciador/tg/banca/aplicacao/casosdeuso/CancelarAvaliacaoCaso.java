package br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.entidade.Banca;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.objetosvalor.BancaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.repositorio.BancaRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;


import java.util.UUID;

public class CancelarAvaliacaoCaso {
    private final BancaRepositorio bancaRepositorio;
    private final GrupoTgRepositorio grupoTgRepositorio;
    private final ProfessorRepositorio professorRepositorio;

    public CancelarAvaliacaoCaso(
            BancaRepositorio bancaRepositorio,
            GrupoTgRepositorio grupoTgRepositorio,
            ProfessorRepositorio professorRepositorio
    ) {
        this.bancaRepositorio = bancaRepositorio;
        this.grupoTgRepositorio = grupoTgRepositorio;
        this.professorRepositorio = professorRepositorio;
    }

    public record Comando(
            String emailOrientadorLogado,
            String idBanca
    ) {}

    public void executar(Comando comando) {
        // Validações de segurança do Orientador
        Professor orientadorLogado = professorRepositorio.buscarPorEmail(new Email(comando.emailOrientadorLogado()))
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "orientador"));

        Banca banca = bancaRepositorio.buscarPorId(new BancaId(UUID.fromString(comando.idBanca())))
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "banca"));

        GrupoTg grupo = grupoTgRepositorio.buscarPorIdGrupo(banca.grupoId())
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "grupo"));

        if (!grupo.orientadorId().equals(orientadorLogado.id())) {
            throw new
                    RegraNegocioExcecao(CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "orientadorId", "ser orientador do grupotg dessa banca");
        }

        // Aciona o Domínio para fazer o cancelamento da banca
        banca.cancelarAvaliacao();

        // Salva a Banca atualizada
        bancaRepositorio.salvar(banca);
    }
}
