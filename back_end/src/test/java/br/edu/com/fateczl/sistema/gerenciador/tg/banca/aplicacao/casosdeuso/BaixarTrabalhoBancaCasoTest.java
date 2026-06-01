package br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.entidade.Banca;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.objetosvalor.BancaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.repositorio.BancaRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso.BaixarTrabalhoBancaCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.portas.ArmazenamentoArquivoPorta;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaixarTrabalhoBancaCasoTest {

    @Mock private BancaRepositorio bancaRepositorio;
    @Mock private GrupoTgRepositorio grupoTgRepositorio;
    @Mock private ProfessorRepositorio professorRepositorio;
    @Mock private ArmazenamentoArquivoPorta armazenamentoPorta;

    @InjectMocks
    private BaixarTrabalhoBancaCaso baixarTrabalhoBancaCaso;

    @Test
    void deveBaixarTrabalhoComSucessoQuandoProfessorForOrientador() {
        BaixarTrabalhoBancaCaso.Comando comando = new BaixarTrabalhoBancaCaso.Comando(
                "professor@cps.sp.gov.br",
                UUID.randomUUID().toString()
        );

        Professor professorMock = mock(Professor.class);
        ProfessorId professorId = new ProfessorId(UUID.randomUUID());
        when(professorMock.id()).thenReturn(professorId);

        Banca bancaMock = mock(Banca.class);
        GrupoTgId grupoId = new GrupoTgId(UUID.randomUUID());
        when(bancaMock.grupoId()).thenReturn(grupoId);

        GrupoTg grupoMock = mock(GrupoTg.class);
        when(grupoMock.orientadorId()).thenReturn(professorId); // Autoriza o download via Orientador
        when(grupoMock.caminhoArquivoTrabalho()).thenReturn("/arquivos/trabalho-final.pdf");
        when(grupoMock.nomeTemaTg()).thenReturn("Inteligencia Artificial");

        when(professorRepositorio.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(professorMock));
        when(bancaRepositorio.buscarPorId(any(BancaId.class))).thenReturn(Optional.of(bancaMock));
        when(grupoTgRepositorio.buscarPorIdGrupo(grupoId)).thenReturn(Optional.of(grupoMock));

        byte[] conteudoEsperado = new byte[]{1, 2, 3};
        when(armazenamentoPorta.recuperarArquivo(anyString())).thenReturn(conteudoEsperado);

        BaixarTrabalhoBancaCaso.Saida saida = baixarTrabalhoBancaCaso.executar(comando);

        assertArrayEquals(conteudoEsperado, saida.conteudo());
        assertEquals("Inteligencia Artificial.pdf", saida.nomeArquivo());
        verify(armazenamentoPorta, times(1)).recuperarArquivo("/arquivos/trabalho-final.pdf");
    }

    @Test
    void deveLancarExcecaoEInterromperFluxoQuandoProfessorNaoForMembroDaBanca() {
        BaixarTrabalhoBancaCaso.Comando comando = new BaixarTrabalhoBancaCaso.Comando(
                "professor@cps.sp.gov.br",
                UUID.randomUUID().toString()
        );

        Professor professorMock = mock(Professor.class);
        ProfessorId professorId = new ProfessorId(UUID.randomUUID());
        when(professorMock.id()).thenReturn(professorId);

        Banca bancaMock = mock(Banca.class);
        GrupoTgId grupoId = new GrupoTgId(UUID.randomUUID());
        when(bancaMock.grupoId()).thenReturn(grupoId);
        when(bancaMock.avaliadoresInternos()).thenReturn(List.of()); // Não é avaliador

        GrupoTg grupoMock = mock(GrupoTg.class);
        ProfessorId outroOrientadorId = new ProfessorId(UUID.randomUUID());
        when(grupoMock.orientadorId()).thenReturn(outroOrientadorId); // Não é o orientador

        when(professorRepositorio.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(professorMock));
        when(bancaRepositorio.buscarPorId(any(BancaId.class))).thenReturn(Optional.of(bancaMock));
        when(grupoTgRepositorio.buscarPorIdGrupo(grupoId)).thenReturn(Optional.of(grupoMock));

        RegraNegocioExcecao excecao = assertThrows(RegraNegocioExcecao.class, () -> {
            baixarTrabalhoBancaCaso.executar(comando);
        });

        assertEquals(CodigoErro.AU_003_ACAO_NAO_PERMITIDA_MOTIVO, excecao.getCodigoErro());
        verify(armazenamentoPorta, never()).recuperarArquivo(anyString());
    }
}