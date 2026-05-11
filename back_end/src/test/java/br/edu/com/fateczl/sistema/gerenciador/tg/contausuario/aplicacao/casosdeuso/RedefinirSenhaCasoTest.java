package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.CriptografoSenhas;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GerenciadorCacheCodigo;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Senha;
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

@ExtendWith(MockitoExtension.class)
class RedefinirSenhaCasoTest {
    @Mock private ContaUsuarioRepositorio contaRepositorio;
    @Mock private GerenciadorCacheCodigo cacheCodigo;
    @Mock private CriptografoSenhas criptografoSenhas;

    @InjectMocks
    private RedefinirSenhaCaso casoDeUso;

    /**
     * Testa o "caminho feliz" validando se a senha foi alterada,
     * se o salvar foi chamada e o codigo removido do cache
     */
    @Test
    void deveRedefinirSenhaComSucessoQuandoCodigoForValido(){
        //Arange
        String emailValido = "teste.sucesso@fatec.sp.gov.br";
        String codigoValido = "12345X";
        String novaSenha = "14723";
        String senhaCriptografada = "$2a$10$hashfake...";

        ContaUsuario contaMock = Mockito.mock(ContaUsuario.class);

        var comando = new RedefinirSenhaCaso.Comando(emailValido,codigoValido,novaSenha);
        //Quando buscarcodigo retorne o codigo mockado
        Mockito.when(cacheCodigo.buscarCodigo(any()))
                .thenReturn(codigoValido);
        //Quando buscar por email retorne a conta mockada
        Mockito.when(contaRepositorio.buscarPorEmail(any()))
                .thenReturn(Optional.of(contaMock));

        Mockito.when(criptografoSenhas.criptografar(novaSenha))
                .thenReturn(new Senha(senhaCriptografada));

        //Act
        casoDeUso.executar(comando);

        //Assert
        //Se atualizar senha foi chamado
        Mockito.verify(contaMock,Mockito.times(1)).atualizarSenha(any());
        //Se o salvar do repositorio foi chamado
        Mockito.verify(contaRepositorio,Mockito.times(1)).salvar(any());
        //Se o cache removeu o codigo
        Mockito.verify(cacheCodigo,Mockito.times(1)).removerCodigo(any());
    }

    /**
     * Teste de codigo incorreto, lançará RegraNegocioExcecao e valida que a senha não foi alterada
     */
    @Test
    void deveLancarExcecaoQuandoCodigoForIncorreto(){
        //Arange
        String emailValido = "teste.sucesso@fatec.sp.gov.br";
        String codigoIncorreto = "111111";
        String codigoCache = "999999";
        String novaSenha = "14723";

        ContaUsuario contaMock = Mockito.mock(ContaUsuario.class);

        var comando = new RedefinirSenhaCaso.Comando(emailValido, codigoIncorreto,novaSenha);
        //Quando buscarcodigo retorne o codigo mockado
        Mockito.when(cacheCodigo.buscarCodigo(any()))
                .thenReturn(codigoCache);

        // Act e Assert
        // Garante que a classe vai "estourar" a exceção correta
        assertThrows(RegraNegocioExcecao.class, () -> casoDeUso.executar(comando));
        //Garante que a senha não foi atualizada
        Mockito.verify(contaMock, Mockito.never()).atualizarSenha(any());

    }

    @Test
    void deveLancarExcecaoQuandoCodigoEstiverExpiradoOuInexistente() {
        // Arrange
        var comando = new RedefinirSenhaCaso
                .Comando("teste@fatec.sp.gov.br", "123456", "14723");

        // Simula que o tempo expirou e o cache devolveu NULL
        Mockito.when(cacheCodigo.buscarCodigo(any())).thenReturn(null);

        // Act & Assert
        assertThrows(RegraNegocioExcecao.class, () -> casoDeUso.executar(comando));
        Mockito.verify(contaRepositorio, Mockito.never()).salvar(any());
    }
}
