package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GeradorCodigoOTP;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GerenciadorCacheCodigo;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.RemetenteEmail;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnviarEmailConfirmacaoCasoTest {

    @Mock private ContaUsuarioRepositorio repositorio;
    @Mock private GerenciadorCacheCodigo cache;
    @Mock private RemetenteEmail remetente;
    @Mock private GeradorCodigoOTP geradorCodigo;

    @InjectMocks
    private EnviarEmailConfirmacaoCaso enviarEmailConfirmacaoCaso;

    @Test
    void deveGerarCodigoEEnviarEmailComSucesso() {
        EnviarEmailConfirmacaoCaso.Comando comando = new EnviarEmailConfirmacaoCaso.Comando(
                "usuario.teste@cps.sp.gov.br"
        );

        ContaUsuario contaMock = mock(ContaUsuario.class);
        when(repositorio.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(contaMock));
        doNothing().when(contaMock).validarStatusParaEnviarEmail();

        when(geradorCodigo.gerar(6)).thenReturn("123456");
        doNothing().when(cache).salvarCodigo(any(Email.class), eq("123456"));
        doNothing().when(remetente).enviarEmail(any(String.class), anyString(), anyString());

        enviarEmailConfirmacaoCaso.executar(comando);

        verify(contaMock, times(1)).validarStatusParaEnviarEmail();
        verify(cache, times(1)).salvarCodigo(any(Email.class), eq("123456"));
        verify(remetente, times(1)).enviarEmail(any(String.class), anyString(), contains("123456"));
    }

    @Test
    void deveLancarExcecaoEInterromperFluxoQuandoContaNaoForEncontrada() {
        EnviarEmailConfirmacaoCaso.Comando comando = new EnviarEmailConfirmacaoCaso.Comando(
                "usuario.inexistente@cps.sp.gov.br"
        );

        when(repositorio.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());

        GenericaExcecao excecao = assertThrows(GenericaExcecao.class, () -> {
            enviarEmailConfirmacaoCaso.executar(comando);
        });

        assertEquals(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, excecao.getCodigoErro());

        verify(geradorCodigo, never()).gerar(anyInt());
        verify(cache, never()).salvarCodigo(any(), any());
        verify(remetente, never()).enviarEmail(any(), any(), any());
    }
}