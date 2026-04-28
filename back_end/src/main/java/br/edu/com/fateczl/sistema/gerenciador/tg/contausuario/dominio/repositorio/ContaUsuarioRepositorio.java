package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;

import java.util.Optional;

public interface ContaUsuarioRepositorio {
    void salvar(ContaUsuario contaUsuario);
    Optional<ContaUsuario> buscarPorId(ContaUsuarioId id);
    Optional<ContaUsuario> buscarPorEmail(Email email);
}