package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.infraestrutura.persistencia.jpa.mapeador;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.AlunoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.TipoRedeSocial;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.infraestrutura.persistencia.jpa.modelo.AlunoModelo;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;

import java.util.*;
import java.util.stream.Collectors;

public class AlunoMapeador {
    private AlunoMapeador() {}

    public static AlunoModelo paraModelo(Aluno dominio) {
        Set<String> turmasIdsTexto = dominio.turmasIds().stream()
                .map(TurmaId::texto)
                .collect(Collectors.toSet());

        return new AlunoModelo(
                dominio.idTexto(),
                dominio.matriculaTexto(),
                dominio.nomeTexto(),
                //Lidando com alunos vindos de arquivo, sem conta ainda, deixa claro nulo
                dominio.contaUsuarioId() != null ? dominio.contaUsuarioId().texto() : null,
                turmasIdsTexto,
                dominio.status(),
                dominio.redesSociais()
        );
    }

    public static Aluno paraDominio(AlunoModelo modelo) {
        List<TurmaId> turmasIds = modelo.getTurmasIds().stream()
                .map(id -> new TurmaId(UUID.fromString(id)))
                .toList();
        ContaUsuarioId contaId = modelo.getContaUsuarioId() != null
                ? new ContaUsuarioId(UUID.fromString(
                modelo.getContaUsuarioId()))
                : null;
        // Cria a cópia independente usando o EnumMap para performance
        Map<TipoRedeSocial, String> redesSociaisCopia = new EnumMap<>(TipoRedeSocial.class);

        // Se vier dados do banco, copia para dentro do nosso novo mapa
        if (modelo.getRedesSociais() != null) {
            redesSociaisCopia.putAll(modelo.getRedesSociais());
        }

        return Aluno.carregar(
                new AlunoId(UUID.fromString(modelo.getId())),
                new Nome(modelo.getNome()),
                new Matricula(modelo.getMatricula()),
                contaId,
                modelo.getStatus(),
                turmasIds,
                redesSociaisCopia
        );
    }
}