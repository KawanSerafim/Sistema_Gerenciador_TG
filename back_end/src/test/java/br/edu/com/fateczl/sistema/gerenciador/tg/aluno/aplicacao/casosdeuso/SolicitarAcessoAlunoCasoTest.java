package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.TipoRedeSocial;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.eventos.ContaPendenteCriadaEvento;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.portas.PublicadorEventos;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.CriptografoSenhas;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Senha;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.servicos.VerificadorUnicidadeEmail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitarAcessoAlunoCasoTest {

    @Mock private AlunoRepositorio alunoRepositorio;
    @Mock private ContaUsuarioRepositorio contaUsuarioRepositorio;
    @Mock private CriptografoSenhas criptografo;
    @Mock private PublicadorEventos publicador;
    @Mock private VerificadorUnicidadeEmail verificadorEmail;

    @InjectMocks
    private SolicitarAcessoAlunoCaso solicitarAcessoAlunoCaso;

    @Test
    void deveCriarContaEVincularAoAlunoComSucesso() {
        SolicitarAcessoAlunoCaso.Comando comando = new SolicitarAcessoAlunoCaso.Comando(
                "1110482323001",
                "aluno.teste@cps.sp.gov.br",
                "SenhaSegura123!",
                Map.of(TipoRedeSocial.LINKEDIN, "linkedin.com/in/aluno")
        );

        doNothing().when(verificadorEmail).verificar(any(Email.class));

        Aluno alunoMock = mock(Aluno.class);
        when(alunoMock.idTexto()).thenReturn(UUID.randomUUID().toString());
        when(alunoMock.nomeTexto()).thenReturn("THIAGO SILVA ANTENOR");

        when(alunoRepositorio.buscarPorMatricula(any(Matricula.class))).thenReturn(Optional.of(alunoMock));

        doNothing().when(alunoMock).validarSolicitacaoAcesso();
        doNothing().when(alunoMock).atualizarRedesSociais(anyMap());
        doNothing().when(alunoMock).vincularConta(any());

        Senha senhaMock = mock(Senha.class);
        when(criptografo.criptografar(anyString())).thenReturn(senhaMock);

        SolicitarAcessoAlunoCaso.Resposta resposta = solicitarAcessoAlunoCaso.executar(comando);

        assertNotNull(resposta.idAluno());
        assertEquals("THIAGO SILVA ANTENOR", resposta.nome());
        assertEquals("aluno.teste@cps.sp.gov.br", resposta.email());

        verify(contaUsuarioRepositorio, times(1)).salvar(any(ContaUsuario.class));
        verify(alunoRepositorio, times(1)).salvar(alunoMock);
        verify(publicador, times(1)).publicar(any(ContaPendenteCriadaEvento.class));
    }

    @Test
    void deveLancarExcecaoEInterromperFluxoQuandoMatriculaNaoForEncontrada() {
        SolicitarAcessoAlunoCaso.Comando comando = new SolicitarAcessoAlunoCaso.Comando(
                "1110482323099",
                "aluno.inexistente@cps.sp.gov.br",
                "SenhaSegura123!",
                Map.of()
        );

        doNothing().when(verificadorEmail).verificar(any(Email.class));
        when(alunoRepositorio.buscarPorMatricula(any(Matricula.class))).thenReturn(Optional.empty());

        GenericaExcecao excecao = assertThrows(GenericaExcecao.class, () -> {
            solicitarAcessoAlunoCaso.executar(comando);
        });

        assertEquals(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, excecao.getCodigoErro());

        // Garante que o agregado de Conta não foi gerado devido à falha de domínio
        verify(criptografo, never()).criptografar(anyString());
        verify(contaUsuarioRepositorio, never()).salvar(any());
        verify(publicador, never()).publicar(any());
    }
}