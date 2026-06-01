package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.AutorizacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.CriptografoSenhas;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GeradorToken;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Senha;
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
class AutenticarUsuarioCasoTest {

    @Mock private ContaUsuarioRepositorio repositorio;
    @Mock private CriptografoSenhas criptografo;
    @Mock private GeradorToken geradorToken;

    @InjectMocks
    private AutenticarUsuarioCaso autenticarUsuarioCaso;

    @Test
    void deveAutenticarEGerarTokenComSucesso() {
        AutenticarUsuarioCaso.Comando comando = new AutenticarUsuarioCaso.Comando(
                "usuario.teste@cps.sp.gov.br",
                "SenhaSegura123!"
        );

        ContaUsuario usuarioMock = mock(ContaUsuario.class);
        Senha senhaMock = mock(Senha.class);

        when(repositorio.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(usuarioMock));
        doNothing().when(usuarioMock).validarSePodeAutenticar();
        when(usuarioMock.senha()).thenReturn(senhaMock);

        when(criptografo.comparar("SenhaSegura123!", senhaMock)).thenReturn(true);
        when(geradorToken.gerarToken(usuarioMock)).thenReturn("jwt.token.valido");

        String tokenGerado = autenticarUsuarioCaso.executar(comando);

        assertEquals("jwt.token.valido", tokenGerado);
        verify(geradorToken, times(1)).gerarToken(usuarioMock);
    }

    @Test
    void deveLancarExcecaoEInterromperFluxoQuandoSenhaForInvalida() {
        AutenticarUsuarioCaso.Comando comando = new AutenticarUsuarioCaso.Comando(
                "usuario.teste@cps.sp.gov.br",
                "SenhaIncorreta!"
        );

        ContaUsuario usuarioMock = mock(ContaUsuario.class);
        Senha senhaMock = mock(Senha.class);

        when(repositorio.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(usuarioMock));
        doNothing().when(usuarioMock).validarSePodeAutenticar();
        when(usuarioMock.senha()).thenReturn(senhaMock);

        // Simulando a falha de validação de credenciais
        when(criptografo.comparar("SenhaIncorreta!", senhaMock)).thenReturn(false);

        AutorizacaoExcecao excecao = assertThrows(AutorizacaoExcecao.class, () -> {
            autenticarUsuarioCaso.executar(comando);
        });

        assertEquals(CodigoErro.AU_001_CREDENCIAIS_INVALIDAS, excecao.getCodigoErro());
        verify(geradorToken, never()).gerarToken(any());
    }
}