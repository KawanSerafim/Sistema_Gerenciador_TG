package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GerenciadorCacheCodigo;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.StatusContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;

public class ValidarCodigoCaso {
    private final ContaUsuarioRepositorio repositorio;
    private final GerenciadorCacheCodigo cache;

    public ValidarCodigoCaso(ContaUsuarioRepositorio repositorio,
                             GerenciadorCacheCodigo cache) {
        this.repositorio = repositorio;
        this.cache = cache;
    }

    public record Comando(String email, String codigoInformado) {}

    public void executar(Comando comando) {
        Email emailAlvo = new Email(comando.email());
        String codigoSalvo = cache.buscarCodigo(emailAlvo);
        validarCodigo(codigoSalvo, comando.codigoInformado);

        ContaUsuario conta = procurarEValidarContaUsuario(emailAlvo);
        conta.atualizarStatus(StatusContaUsuario.ATIVO);
        repositorio.salvar(conta);
        cache.removerCodigo(emailAlvo);
    }

    private void validarCodigo(String codigoSalvo, String codigoInformado) {
        if(codigoSalvo == null || !codigoSalvo.equals(codigoInformado)) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_005_CODIGO_CONFIRMACAO_INVALIDA_EXPIRADO,
                    codigoInformado);
        }
    }

    private ContaUsuario procurarEValidarContaUsuario(Email emailAlvo) {
        ContaUsuario conta = repositorio.buscarPorEmail(emailAlvo)
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO));

        if(conta.status() != StatusContaUsuario.VERIFICACAO_CODIGO_PENDENTE) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO, "conta de " +
                    "usuário", "VERIFICACAO_CODIGO_PENDENTE");
        }
        return conta;
    }
}