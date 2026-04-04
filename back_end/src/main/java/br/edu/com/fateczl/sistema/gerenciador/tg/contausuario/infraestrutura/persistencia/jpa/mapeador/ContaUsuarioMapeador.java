package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.persistencia.jpa.mapeador;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Senha;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.persistencia.jpa.modelo.ContaUsuarioModelo;

import java.util.UUID;

public class ContaUsuarioMapeador {
    private ContaUsuarioMapeador() {}

    public static ContaUsuarioModelo paraModelo(ContaUsuario dominio) {
        return new ContaUsuarioModelo(
                dominio.idTexto(),
                dominio.emailTexto(),
                dominio.senha().valor(),
                dominio.status()
        );
    }

    public static ContaUsuario paraDominio(ContaUsuarioModelo modelo) {
        return ContaUsuario.carregar(
                new ContaUsuarioId(UUID.fromString(modelo.getId())),
                new Email(modelo.getEmail()),
                new Senha(modelo.getSenha()),
                modelo.getStatus()
        );
    }
}