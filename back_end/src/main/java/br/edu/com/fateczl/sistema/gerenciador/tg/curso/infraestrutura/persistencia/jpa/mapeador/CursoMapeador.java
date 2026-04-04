package br.edu.com.fateczl.sistema.gerenciador.tg.curso.infraestrutura.persistencia.jpa.mapeador;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.AjusteTipoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.ParametrosCurso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.infraestrutura.persistencia.jpa.modelo.AjusteTipoTgModelo;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.infraestrutura.persistencia.jpa.modelo.CursoModelo;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.infraestrutura.persistencia.jpa.modelo.ParametrosCursoModelo;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CursoMapeador {
    private CursoMapeador() {}

    public static CursoModelo paraModelo(Curso dominio) {
        Set<AjusteTipoTgModelo> ajustesModelo = dominio.parametros()
                .ajustesTipoTg().stream()
                .map(a -> new AjusteTipoTgModelo(
                        a.tipoTg(),
                        a.maxAlunosGrupo()
                ))
                .collect(Collectors.toSet());

        ParametrosCursoModelo parametrosModelo = new ParametrosCursoModelo(
                Set.copyOf(dominio.parametros().turnos()),
                Set.copyOf(dominio.parametros().disciplinas()),
                ajustesModelo
        );

        return new CursoModelo(
                dominio.idTexto(),
                dominio.nomeTexto(),
                dominio.coordenadorIdTexto(),
                parametrosModelo
        );
    }

    public static Curso paraDominio(CursoModelo modelo) {
        List<AjusteTipoTg> ajustes = modelo.getParametros().getAjusteTipoTg()
                .stream()
                .map(a -> new AjusteTipoTg(
                        a.getTipoTg(),
                        a.getMaxAlunosGrupo()
                ))
                .toList();

        ParametrosCurso parametros = new ParametrosCurso(
                List.copyOf(modelo.getParametros().getTurnos()),
                List.copyOf(modelo.getParametros().getDisciplinas()),
                ajustes
        );

        return Curso.carregar(
                new CursoId(UUID.fromString(modelo.getId())),
                new Nome(modelo.getNome()),
                parametros,
                new ProfessorId(UUID.fromString(modelo.getCoordenadorId()))
        );
    }
}