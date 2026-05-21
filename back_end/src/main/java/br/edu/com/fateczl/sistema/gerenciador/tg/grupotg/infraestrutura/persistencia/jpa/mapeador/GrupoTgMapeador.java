package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.infraestrutura.persistencia.jpa.mapeador;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.AlunoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.TemaTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.infraestrutura.persistencia.jpa.modelo.GrupoTgModelo;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;

import java.util.List;
import java.util.UUID;

public class GrupoTgMapeador {
    private GrupoTgMapeador() {}

    public static GrupoTgModelo paraModelo(GrupoTg dominio) {
        List<String> alunosIdsTexto = dominio.alunosIds().stream()
                .map(id -> id.valor().toString())
                .toList();

        return new GrupoTgModelo(
                dominio.idTexto(),
                dominio.orientadorIdTexto(),
                dominio.coorientadorIdTexto(),
                dominio.tipoCoorientador(),
                dominio.cursoIdTexto(),
                dominio.disciplinas(),
                dominio.nomeTemaTg(),
                dominio.descricaoTemaTg(),
                dominio.tipoTg(),
                alunosIdsTexto,
                dominio.caminhoArquivoTrabalho()
        );
    }

    public static GrupoTg paraDominio(GrupoTgModelo modelo) {
        List<AlunoId> alunosIds = modelo.getAlunosIds().stream()
                .map(idTexto -> new AlunoId(UUID.fromString(idTexto)))
                .toList();

        var orientadorId = modelo.getOrientadorId() != null
                ? new ProfessorId(UUID.fromString(modelo.getOrientadorId()))
                : null;

        var temaTg = new TemaTg(
                modelo.getNomeTemaTg(),
                modelo.getDescricaoTemaTg()
        );

        return GrupoTg.carregar(
                new GrupoTgId(UUID.fromString(modelo.getId())),
                orientadorId,
                modelo.getCoorientadorId(),
                modelo.getTipoCoorientador(),
                new CursoId(UUID.fromString(modelo.getCursoId())),
                modelo.getDisciplinas(),
                temaTg,
                modelo.getTipoTg(),
                alunosIds,
                modelo.getCaminhoArquivoTg()
        );
    }
}