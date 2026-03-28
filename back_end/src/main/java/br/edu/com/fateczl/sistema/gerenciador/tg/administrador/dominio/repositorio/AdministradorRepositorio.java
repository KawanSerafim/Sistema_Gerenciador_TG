package br.edu.com.fateczl.sistema.gerenciador.tg.administrador.dominio.repositorio;

import br.edu.com.fateczl.sistema.gerenciador.tg.administrador.dominio.entidade.Administrador;
import br.edu.com.fateczl.sistema.gerenciador.tg.administrador.dominio.objetovalor.AdministradorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;

import java.util.Optional;

public interface AdministradorRepositorio {
    void salvar(Administrador administrador);
    Optional<Administrador> buscarPorId(AdministradorId id);
    Optional<Administrador> buscarPorEmail(Email email);
}