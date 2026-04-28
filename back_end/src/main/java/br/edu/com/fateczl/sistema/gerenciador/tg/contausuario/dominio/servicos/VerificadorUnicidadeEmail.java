package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.servicos;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;

public class VerificadorUnicidadeEmail {
    private final ContaUsuarioRepositorio repositorio;

    public VerificadorUnicidadeEmail(ContaUsuarioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void verificar(Email email) {
        repositorio.buscarPorEmail(email).ifPresent(conta -> {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_002_REGISTRO_DUPLICADO,
                    "email"
            );
        });
    }
}