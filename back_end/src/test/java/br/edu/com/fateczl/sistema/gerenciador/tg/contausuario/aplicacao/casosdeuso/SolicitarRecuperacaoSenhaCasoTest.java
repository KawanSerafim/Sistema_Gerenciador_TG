package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso;

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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class SolicitarRecuperacaoSenhaCasoTest {
    @Mock
    private ContaUsuarioRepositorio repositorio;
    @Mock private GerenciadorCacheCodigo cache;
    @Mock private RemetenteEmail remetente;
    @Mock private GeradorCodigoOTP geradorCodigo;

    @InjectMocks
    private SolicitarRecuperacaoSenhaCaso casoDeUso;

    @Test
    void deveEnviarEmailComCodigo(){
        // Arange - prepara email valido
        String emailValido = "teste.valido@cps.sp.gov.br";
        var comando = new SolicitarRecuperacaoSenhaCaso.Comando(emailValido);

        //Conta mock
        ContaUsuario contaMock = Mockito.mock(ContaUsuario.class);
        //Quando bsucar qualquer email retorne o mock da conta
        Mockito.when(repositorio.buscarPorEmail(any(Email.class)))
                .thenReturn(Optional.of(contaMock));
        //Mock do codigo
        String codigoMock = "12345J";
        Mockito.when(geradorCodigo.gerar(6))
                .thenReturn(codigoMock);
        //Act
        casoDeUso.executar(comando);

        //Assert
        //Verifica se o codigo mock foi para o cache
        Mockito.verify(cache, times(1))
                .salvarCodigo(any(Email.class),Mockito.eq(codigoMock));
        //Verifica se enviarEmailTexto foi chamada
        Mockito.verify(remetente, times(1))
                .enviarEmailTexto(any(),any(),any());
    }

    @Test
    void naoDeveEnviarEmailSeContaNaoExistir() {
        // ARRANJE (Preparação)
        String emailInvalido = "teste.email@cps.sp.gov.br";
        var comando = new SolicitarRecuperacaoSenhaCaso.Comando(emailInvalido);

        // Ensina o mock do repositório a retornar VAZIO quando buscarem esse e-mail
        Mockito.when(repositorio.buscarPorEmail(any(Email.class)))
                .thenReturn(java.util.Optional.empty());

        // Act (Execução) e Assert (Verificação)
        // Garante que a classe vai "estourar" a exceção correta
        assertThrows(GenericaExcecao.class, () -> casoDeUso.executar(comando));

        // Assert ADICIONAL: Garante que o e-mail NUNCA tentou ser enviado
        Mockito.verify(remetente, never()).enviarEmailTexto(any(), any(), any());

        // Garante que não sujou o cache à toa
        Mockito.verify(cache, never()).salvarCodigo(any(), any());

    }


}
