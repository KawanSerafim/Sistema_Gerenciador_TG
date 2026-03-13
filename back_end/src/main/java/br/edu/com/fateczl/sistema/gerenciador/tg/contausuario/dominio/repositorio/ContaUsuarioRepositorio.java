package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;

import java.util.Optional;

public interface ContaUsuarioRepositorio {
    ContaUsuario salvar(ContaUsuario contaUsuario);
    Optional<ContaUsuario> buscarPorEmail(Email email);
}