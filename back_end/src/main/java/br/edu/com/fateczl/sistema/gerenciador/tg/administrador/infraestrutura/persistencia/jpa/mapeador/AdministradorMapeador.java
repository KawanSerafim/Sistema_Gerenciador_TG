package br.edu.com.fateczl.sistema.gerenciador.tg.administrador.infraestrutura.persistencia.jpa.mapeador;

import br.edu.com.fateczl.sistema.gerenciador.tg.administrador.dominio.entidade.Administrador;
import br.edu.com.fateczl.sistema.gerenciador.tg.administrador.dominio.objetovalor.AdministradorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.administrador.infraestrutura.persistencia.jpa.modelo.AdministradorModelo;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;

import java.util.UUID;

public class AdministradorMapeador {
    private AdministradorMapeador() {}

    public static AdministradorModelo paraModelo(Administrador dominio) {
        return new AdministradorModelo(
                dominio.idTexto(),
                dominio.nomeTexto(),
                dominio.contaUsuarioIdTexto()
        );
    }

    public static Administrador paraDominio(AdministradorModelo modelo) {
        AdministradorId id = new AdministradorId(
                UUID.fromString(modelo.getId())
        );
        Nome nome = new Nome(modelo.getNome());
        ContaUsuarioId contaUsuarioId = new ContaUsuarioId(
                UUID.fromString(modelo.getContaUsuarioId())
        );

        return Administrador.carregar(id, nome, contaUsuarioId);
    }
}