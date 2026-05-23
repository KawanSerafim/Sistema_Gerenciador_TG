package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.objetosvalor.CoorientadorExternoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.repositorio.CoorientadorExternoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;

import java.util.List;
import java.util.UUID;

public class BuscarGrupoAlunoCaso {
    private final AlunoRepositorio alunoRepositorio;
    private final GrupoTgRepositorio grupoTgRepositorio;
    private final ProfessorRepositorio professorRepositorio;
    private final CoorientadorExternoRepositorio coorientadorRepositorio; // Descomente se for usar

    public BuscarGrupoAlunoCaso(
            AlunoRepositorio alunoRepositorio,
            GrupoTgRepositorio grupoTgRepositorio,
            ProfessorRepositorio professorRepositorio,
            CoorientadorExternoRepositorio coorientadorRepositorio
    ) {
        this.alunoRepositorio = alunoRepositorio;
        this.grupoTgRepositorio = grupoTgRepositorio;
        this.professorRepositorio = professorRepositorio;
        this.coorientadorRepositorio = coorientadorRepositorio;
    }

    public record Comando(String idAlunoLogado) {}

    public record MeuGrupoDetalhadoDTO(
            String idGrupo,
            String tema,
            String descricao,
            List<String> integrantes,
            String nomeOrientador,
            String nomeCoorientador
    ) {}

    public MeuGrupoDetalhadoDTO executar(Comando comando) {
        // Identifica o Aluno pelo id da conta logada
        ContaUsuarioId contaId = new ContaUsuarioId(UUID.fromString(comando.idAlunoLogado()));

        Aluno aluno = alunoRepositorio.buscarPorContaId(contaId)
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "aluno"));

        // Busca o Grupo Ativo do Aluno
        GrupoTg grupo = grupoTgRepositorio.buscarPorAlunoId(aluno.id())
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "grupo do aluno"));

        // Resolve os Nomes dos Integrantes
        List<String> nomesIntegrantes = alunoRepositorio.buscarTodosPorIds(grupo.alunosIds())
                .stream()
                .map(Aluno::nomeTexto)
                .toList();

        // Resolve o Nome do Orientador (se existir)
        String nomeOrientador = null;
        if (grupo.orientadorId() != null) {
            nomeOrientador = professorRepositorio.buscarPorId(grupo.orientadorId())
                    .map(prof -> prof.nome().valor())
                    .orElse("Orientador não encontrado");
        }

        // Resolve o Nome do Coorientador (se existir)
        String nomeCoorientador = null;
         //Implementação de coorientador-externo
        if (grupo.coorientadorIdTexto() != null) {
            nomeCoorientador = coorientadorRepositorio.buscarPorId(
                    new CoorientadorExternoId(UUID.fromString(grupo.coorientadorIdTexto())))
                    .map(coor -> coor.nome().valor())
                    .orElse("Coorientador não encontrado");
        } else {
            nomeCoorientador = "Grupo sem coorientador";
        }

        // 6. Monta o DTO
        return new MeuGrupoDetalhadoDTO(
                grupo.idTexto(),
                grupo.nomeTemaTg(),
                grupo.descricaoTemaTg(),
                nomesIntegrantes,
                nomeOrientador,
                nomeCoorientador
        );
    }
}
