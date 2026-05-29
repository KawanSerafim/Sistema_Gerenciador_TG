package br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.cassosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso.FinalizarTurmasCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinalizarTurmasCasoTest {

    @Mock
    private TurmaRepositorio turmaRepositorio;

    @Mock
    private ProfessorRepositorio professorRepositorio;

    @InjectMocks
    private FinalizarTurmasCaso finalizarTurmasCaso;

    @Test
    void deveFinalizarTurmasComSucessoQuandoValidacoesDeSegurancaPassarem() {
        FinalizarTurmasCaso.Comando comando = new FinalizarTurmasCaso.Comando(
                "professor.tg@cps.sp.gov.br",
                List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString())
        );

        Professor professorMock = mock(Professor.class);
        String idProfessor = UUID.randomUUID().toString();

        when(professorMock.podeSerProfessorTg()).thenReturn(true);
        when(professorMock.idTexto()).thenReturn(idProfessor);
        when(professorRepositorio.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(professorMock));

        Turma turmaMock1 = mock(Turma.class);
        Turma turmaMock2 = mock(Turma.class);

        // Garante que as turmas pertencem ao professor logado
        when(turmaMock1.professorTgIdTexto()).thenReturn(idProfessor);
        when(turmaMock2.professorTgIdTexto()).thenReturn(idProfessor);

        when(turmaRepositorio.buscarTodasPorIds(anySet())).thenReturn(List.of(turmaMock1, turmaMock2));

        finalizarTurmasCaso.executar(comando);

        verify(turmaMock1, times(1)).finalizar();
        verify(turmaMock2, times(1)).finalizar();
        verify(turmaRepositorio, times(1)).salvarTodas(anyList());
    }

    @Test
    void deveLancarExcecaoQuandoProfessorLogadoNaoTiverPermissaoDeProfessorTg() {
        FinalizarTurmasCaso.Comando comando = new FinalizarTurmasCaso.Comando(
                "professor.orientador@cps.sp.gov.br",
                List.of(UUID.randomUUID().toString())
        );

        Professor professorMock = mock(Professor.class);

        // Simula um professor que não possui o cargo de Professor de TG
        when(professorMock.podeSerProfessorTg()).thenReturn(false);
        when(professorRepositorio.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(professorMock));

        RegraNegocioExcecao excecao = assertThrows(RegraNegocioExcecao.class, () -> {
            finalizarTurmasCaso.executar(comando);
        });

        assertEquals(CodigoErro.AU_003_ACAO_NAO_PERMITIDA_MOTIVO, excecao.getCodigoErro());
        verify(turmaRepositorio, never()).buscarTodasPorIds(anySet());
        verify(turmaRepositorio, never()).salvarTodas(anyList());
    }

    @Test
    void deveLancarExcecaoQuandoNenhumaTurmaForEncontradaOuListaForVazia() {
        FinalizarTurmasCaso.Comando comando = new FinalizarTurmasCaso.Comando(
                "professor.tg@cps.sp.gov.br",
                List.of(UUID.randomUUID().toString())
        );

        Professor professorMock = mock(Professor.class);
        when(professorMock.podeSerProfessorTg()).thenReturn(true);
        when(professorRepositorio.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(professorMock));

        // Retorna lista vazia simulando que as turmas solicitadas não existem no banco
        when(turmaRepositorio.buscarTodasPorIds(anySet())).thenReturn(List.of());

        RegraNegocioExcecao excecao = assertThrows(RegraNegocioExcecao.class, () -> {
            finalizarTurmasCaso.executar(comando);
        });

        assertEquals(CodigoErro.AU_003_ACAO_NAO_PERMITIDA_MOTIVO, excecao.getCodigoErro());
        verify(turmaRepositorio, never()).salvarTodas(anyList());
    }

    @Test
    void deveLancarExcecaoQuandoTentaremFinalizarTurmaDeOutroProfessor() {
        FinalizarTurmasCaso.Comando comando = new FinalizarTurmasCaso.Comando(
                "professor.tg@cps.sp.gov.br",
                List.of(UUID.randomUUID().toString())
        );

        Professor professorMock = mock(Professor.class);
        String idProfessorLogado = UUID.randomUUID().toString();

        when(professorMock.podeSerProfessorTg()).thenReturn(true);
        when(professorMock.idTexto()).thenReturn(idProfessorLogado);
        when(professorRepositorio.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(professorMock));

        Turma turmaMock = mock(Turma.class);

        // Simula que a turma pertence a um professor diferente do logado
        when(turmaMock.professorTgIdTexto()).thenReturn(UUID.randomUUID().toString());

        when(turmaRepositorio.buscarTodasPorIds(anySet())).thenReturn(List.of(turmaMock));

        ValidacaoExcecao excecao = assertThrows(ValidacaoExcecao.class, () -> {
            finalizarTurmasCaso.executar(comando);
        });

        assertEquals(CodigoErro.AU_003_ACAO_NAO_PERMITIDA_MOTIVO, excecao.getCodigoErro());
        verify(turmaMock, never()).finalizar();
        verify(turmaRepositorio, never()).salvarTodas(anyList());
    }

    @Test
    void deveLancarExcecaoQuandoProfessorLogadoNaoForEncontrado() {
        FinalizarTurmasCaso.Comando comando = new FinalizarTurmasCaso.Comando(
                "professor.inexistente@cps.sp.gov.br",
                List.of(UUID.randomUUID().toString())
        );

        when(professorRepositorio.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());

        GenericaExcecao excecao = assertThrows(GenericaExcecao.class, () -> {
            finalizarTurmasCaso.executar(comando);
        });

        assertEquals(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, excecao.getCodigoErro());
        verify(turmaRepositorio, never()).buscarTodasPorIds(anySet());
    }
}