package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.AutorizacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.CriptografoSenhas;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GeradorToken;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;

public class AutenticarUsuarioCaso {
    private final ContaUsuarioRepositorio repositorio;
    private final CriptografoSenhas criptografo;
    private final GeradorToken geradorToken;

    public AutenticarUsuarioCaso(
            ContaUsuarioRepositorio repositorio,
            CriptografoSenhas criptografo,
            GeradorToken geradorToken
    ) {
        this.repositorio = repositorio;
        this.criptografo = criptografo;
        this.geradorToken = geradorToken;
    }

    public record Comando(String email, String senha) {}

    public String executar(Comando comando) {
        Email emailAlvo = new Email(comando.email());
        ContaUsuario usuario = repositorio.buscarPorEmail(emailAlvo)
                .orElseThrow(() -> new AutorizacaoExcecao(
                        CodigoErro.AU_001_CREDENCIAIS_INVALIDAS
                ));

        usuario.validarSePodeAutenticar();

        boolean senhaValida = criptografo.comparar(
                comando.senha(),
                usuario.senha()
        );

        if(!senhaValida) {
            throw new AutorizacaoExcecao(
                    CodigoErro.AU_001_CREDENCIAIS_INVALIDAS);
        }

        return geradorToken.gerarToken(usuario);
    }
}