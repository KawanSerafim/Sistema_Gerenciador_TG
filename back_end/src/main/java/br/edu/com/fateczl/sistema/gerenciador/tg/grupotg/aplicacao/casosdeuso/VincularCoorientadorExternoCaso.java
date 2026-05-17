package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.AlunoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.entidade.CoorientadorExterno;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.objetosvalor.CoorientadorExternoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.objetosvalor.Origem;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.repositorio.CoorientadorExternoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.TipoCoorientador;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;

import java.util.UUID;

public class VincularCoorientadorExternoCaso {
    private final AlunoRepositorio alunoRepositorio;
    private final GrupoTgRepositorio grupoTgRepositorio;
    private final CoorientadorExternoRepositorio coorientadorRepositorio;

    public VincularCoorientadorExternoCaso(
            AlunoRepositorio alunoRepositorio,
            GrupoTgRepositorio grupoTgRepositorio,
            CoorientadorExternoRepositorio coorientadorRepositorio
    ) {
        this.alunoRepositorio = alunoRepositorio;
        this.grupoTgRepositorio = grupoTgRepositorio;
        this.coorientadorRepositorio = coorientadorRepositorio;
    }

    public record Comando(
            String idContaAlunoLogado,
            String nomeCoorientador,
            String origemCoorientador
    ) {}

    public void executar(Comando comando) {
        // Identificar o Aluno logado
        ContaUsuarioId contaId = new ContaUsuarioId(UUID.fromString(comando.idContaAlunoLogado()));
        Aluno aluno = alunoRepositorio.buscarPorContaId(contaId)
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "aluno"));

        // Buscar o Grupo
        GrupoTg grupo = buscarGrupoAluno(aluno.id());

        // validação: O grupo já possui orientador principal?
        if (grupo.orientadorId() == null) {
            throw new RegraNegocioExcecao(CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "grupo", "ainda não possui orientador vinculado");
        }

        // Instanciar Objetos de Valor do Coorientador
        Nome nome = new Nome(comando.nomeCoorientador());
        Origem origem = new Origem(comando.origemCoorientador());

        // Busca ou cria o Coorientador Externo
        CoorientadorExterno coorientadorExterno = coorientadorRepositorio.buscarPorNomeEOrigem(nome, origem)
                .orElseGet(() -> {
                    // Se não existir no banco, cria um novo e salva
                    CoorientadorExterno novo = CoorientadorExterno.novo(
                            new CoorientadorExternoId(UUID.randomUUID()),
                            nome,
                            origem
                    );
                    coorientadorRepositorio.salvar(novo);
                    return novo;
                });

        // Vincula o Coorientador ao Grupo
        grupo.vincularCoorientador(coorientadorExterno.idTexto(), TipoCoorientador.EXTERNO);

        // Salva o Grupo atualizado
        grupoTgRepositorio.salvar(grupo);
    }

    //Metodos auxiliares
    public GrupoTg buscarGrupoAluno(AlunoId alunoId){
        return grupoTgRepositorio.buscarPorAlunoId(alunoId)
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "grupo do aluno"));
    }
}
