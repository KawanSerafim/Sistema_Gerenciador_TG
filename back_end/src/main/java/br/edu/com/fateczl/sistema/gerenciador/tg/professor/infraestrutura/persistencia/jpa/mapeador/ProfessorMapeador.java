package br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.persistencia.jpa.mapeador;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.persistencia.jpa.modelo.ProfessorModelo;

import java.util.UUID;

public class ProfessorMapeador {
    private ProfessorMapeador() {}

    public static ProfessorModelo paraModelo(Professor dominio) {
        return new ProfessorModelo(
                dominio.idTexto(),
                dominio.matriculaTexto(),
                dominio.nomeTexto(),
                dominio.contaUsuarioIdTexto(),
                dominio.cargo()
        );
    }

    public static Professor paraDominio(ProfessorModelo modelo) {
        ContaUsuarioId contaId = modelo.getContaUsuarioId() != null
                ? new ContaUsuarioId(UUID.fromString(
                modelo.getContaUsuarioId()))
                : null;

        return Professor.carregar(
                new ProfessorId(UUID.fromString(modelo.getId())),
                new Nome(modelo.getNome()),
                new Matricula(modelo.getMatricula()),
                contaId,
                modelo.getCargo()
        );
    }
}