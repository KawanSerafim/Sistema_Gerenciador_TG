package br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.AlunoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.entidade.SolicitacaoOrientacao;
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
class SolicitarOrientacaoCasoTest {

    @Mock
    private AlunoRepositorio alunoRepositorio;

    @Mock
    private GrupoTgRepositorio grupoTgRepositorio;

    @Mock
    private ProfessorRepositorio professorRepositorio;

    @Mock
    private SolicitacaoOrientacaoRepositorio solicitacaoRepositorio;

    @InjectMocks
    private SolicitarOrientacaoCaso solicitarOrientacaoCaso;

    @Test
    void deveCriarSolicitacaoOrientacaoComSucesso() {
        String idContaUsuario = UUID.randomUUID().toString();
        String idProfessor = UUID.randomUUID().toString();

        SolicitarOrientacaoCaso.Comando comando = new SolicitarOrientacaoCaso.Comando(
                idContaUsuario,
                idProfessor
        );

        Aluno alunoMock = mock(Aluno.class);
        AlunoId alunoId = new AlunoId(UUID.randomUUID());
        when(alunoMock.id()).thenReturn(alunoId);

        GrupoTg grupoMock = mock(GrupoTg.class);
        GrupoTgId grupoId = new GrupoTgId(UUID.randomUUID());
        when(grupoMock.id()).thenReturn(grupoId);
        // Garante que o grupo não tem orientador para passar na validação validarGrupoSemOrientador()
        when(grupoMock.orientadorId()).thenReturn(null);

        Professor professorMock = mock(Professor.class);
        ProfessorId professorId = new ProfessorId(UUID.fromString(idProfessor));
        when(professorMock.id()).thenReturn(professorId);

        when(alunoRepositorio.buscarPorContaId(any(ContaUsuarioId.class))).thenReturn(Optional.of(alunoMock));
        when(grupoTgRepositorio.buscarPorAlunoId(any(AlunoId.class))).thenReturn(Optional.of(grupoMock));
        // Garante que não há solicitações pendentes para passar na validação validarSolicitacaoPendente()
        when(solicitacaoRepositorio.existeSolicitacaoPendenteParaGrupo(anyString())).thenReturn(false);
        when(professorRepositorio.buscarPorId(any(ProfessorId.class))).thenReturn(Optional.of(professorMock));

        solicitarOrientacaoCaso.executar(comando);

        verify(solicitacaoRepositorio, times(1)).salvar(any(SolicitacaoOrientacao.class));
    }

    @Test
    void deveLancarExcecaoQuandoAlunoNaoForEncontrado() {
        String idContaUsuario = UUID.randomUUID().toString();
        String idProfessor = UUID.randomUUID().toString();

        SolicitarOrientacaoCaso.Comando comando = new SolicitarOrientacaoCaso.Comando(
                idContaUsuario,
                idProfessor
        );

        when(alunoRepositorio.buscarPorContaId(any(ContaUsuarioId.class))).thenReturn(Optional.empty());

        GenericaExcecao excecao = assertThrows(GenericaExcecao.class, () -> {
            solicitarOrientacaoCaso.executar(comando);
        });

        assertEquals(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, excecao.getCodigoErro());
        verify(grupoTgRepositorio, never()).buscarPorAlunoId(any());
        verify(solicitacaoRepositorio, never()).salvar(any());
    }
}