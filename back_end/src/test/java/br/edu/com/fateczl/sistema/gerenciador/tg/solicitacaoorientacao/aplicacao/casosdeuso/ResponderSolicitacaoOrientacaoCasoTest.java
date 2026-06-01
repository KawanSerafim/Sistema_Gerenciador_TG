package br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.entidade.SolicitacaoOrientacao;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.objetosvalor.SolicitacaoOrientacaoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.repositorio.SolicitacaoOrientacaoRepositorio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResponderSolicitacaoOrientacaoCasoTest {

    @Mock private ProfessorRepositorio professorRepositorio;
    @Mock private SolicitacaoOrientacaoRepositorio solicitacaoRepositorio;
    @Mock private GrupoTgRepositorio grupoTgRepositorio;

    @InjectMocks
    private ResponderSolicitacaoOrientacaoCaso responderSolicitacaoCaso;

    @Test
    void deveAceitarSolicitacaoEVincularOrientadorComSucesso() {
        ResponderSolicitacaoOrientacaoCaso.Comando comando = new ResponderSolicitacaoOrientacaoCaso.Comando(
                "professor@cps.sp.gov.br",
                UUID.randomUUID().toString(),
                true // Aceita
        );

        Professor professorMock = mock(Professor.class);
        ProfessorId professorId = new ProfessorId(UUID.randomUUID());
        when(professorMock.id()).thenReturn(professorId);

        SolicitacaoOrientacao solicitacaoMock = mock(SolicitacaoOrientacao.class);
        GrupoTgId grupoId = new GrupoTgId(UUID.randomUUID());
        when(solicitacaoMock.professorId()).thenReturn(professorId);
        when(solicitacaoMock.grupoId()).thenReturn(grupoId);

        GrupoTg grupoMock = mock(GrupoTg.class);

        when(professorRepositorio.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(professorMock));
        when(solicitacaoRepositorio.buscarPorId(any(SolicitacaoOrientacaoId.class))).thenReturn(Optional.of(solicitacaoMock));
        when(grupoTgRepositorio.buscarPorIdGrupo(grupoId)).thenReturn(Optional.of(grupoMock));

        responderSolicitacaoCaso.executar(comando);

        verify(solicitacaoMock, times(1)).aceitar();
        verify(grupoMock, times(1)).vincularOrientador(professorId);
        verify(grupoTgRepositorio, times(1)).salvar(grupoMock);
        verify(solicitacaoRepositorio, times(1)).salvar(solicitacaoMock);
    }

    @Test
    void deveLancarExcecaoQuandoProfessorTentarResponderSolicitacaoDeOutroAvaliador() {
        ResponderSolicitacaoOrientacaoCaso.Comando comando = new ResponderSolicitacaoOrientacaoCaso.Comando(
                "professor@cps.sp.gov.br",
                UUID.randomUUID().toString(),
                false
        );

        Professor professorMock = mock(Professor.class);
        ProfessorId professorId = new ProfessorId(UUID.randomUUID());
        when(professorMock.id()).thenReturn(professorId);

        SolicitacaoOrientacao solicitacaoMock = mock(SolicitacaoOrientacao.class);
        ProfessorId outroProfessorId = new ProfessorId(UUID.randomUUID());
        when(solicitacaoMock.professorId()).thenReturn(outroProfessorId); // IDs incompatíveis

        when(professorRepositorio.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(professorMock));
        when(solicitacaoRepositorio.buscarPorId(any(SolicitacaoOrientacaoId.class))).thenReturn(Optional.of(solicitacaoMock));

        RegraNegocioExcecao excecao = assertThrows(RegraNegocioExcecao.class, () -> {
            responderSolicitacaoCaso.executar(comando);
        });

        assertEquals(CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO, excecao.getCodigoErro());
        verify(solicitacaoMock, never()).aceitar();
        verify(solicitacaoMock, never()).recusar();
        verify(solicitacaoRepositorio, never()).salvar(any());
        verify(grupoTgRepositorio, never()).salvar(any());
    }
}