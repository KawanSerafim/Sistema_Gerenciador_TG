package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.eventos.ContaAtividadeEvento;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.portas.PublicadorEventos;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GerenciadorCacheCodigo;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;

public class ValidarCodigoCaso {
    private final ContaUsuarioRepositorio repositorio;
    private final GerenciadorCacheCodigo cache;
    private final PublicadorEventos publicador;

    public ValidarCodigoCaso(ContaUsuarioRepositorio repositorio,
                             GerenciadorCacheCodigo cache,
                             PublicadorEventos publicador) {
        this.repositorio = repositorio;
        this.cache = cache;
        this.publicador = publicador;
    }

    public record Comando(String email, String codigoInformado) {}

    public void executar(Comando comando) {
        Email emailAlvo = new Email(comando.email());
        String codigoSalvo = cache.buscarCodigo(emailAlvo);
        validarCodigo(codigoSalvo, comando.codigoInformado);

        ContaUsuario conta = buscarContaUsuario(emailAlvo);
        conta.confirmarEmail();
        conta.ativar();

        repositorio.salvar(conta);
        publicador.publicar(new ContaAtividadeEvento(conta.id()));
        cache.removerCodigo(emailAlvo);
    }

    private void validarCodigo(String codigoSalvo, String codigoInformado) {
        if(codigoSalvo == null || !codigoSalvo.equals(codigoInformado)) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_005_CODIGO_CONFIRMACAO_INVALIDA_EXPIRADO,
                    codigoInformado);
        }
    }

    private ContaUsuario buscarContaUsuario(Email emailAlvo) {
        return repositorio.buscarPorEmail(emailAlvo)
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO));
    }
}