package br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.repositorio.BancaRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class MarcarBancaCasoTest {
    @Mock private BancaRepositorio bancaRepositorio;
    @Mock private GrupoTgRepositorio grupoTgRepositorio;
    @Mock private ProfessorRepositorio professorRepositorio;

    @InjectMocks
    private MarcarBancaCaso casoDeUso;

    private String emailContaLogado;
    private ProfessorId orientadorId;
    private GrupoTgId grupoTgId;
    private MarcarBancaCaso.Comando comandoValido;

    @BeforeEach
    void setUp(){
        emailContaLogado = "teste@fatec.sp.gov.br";
        orientadorId = new ProfessorId(UUID.randomUUID());
        grupoTgId = new GrupoTgId(UUID.randomUUID());

        // Setup base de comando válido
        comandoValido = new MarcarBancaCaso.Comando(
                emailContaLogado,
                grupoTgId.texto(),
                LocalDate.of(2026,6,15),
                LocalTime.of(19,30),
                "Sala 111",
                // 1 Avaliador Interno
                List.of(UUID.randomUUID().toString()),
                // 0 Avaliadores Internos
                List.of()
        );
    }

    // ========================================================================
    // FLUXO DE SUCESSO
    // ========================================================================
    @Test
    void deveMarcarBancaComSucessoQuandoDadosForemValidos() {
        // Arrange
        Professor orientadorLogado = Mockito.mock(Professor.class);
        Mockito.when(professorRepositorio.buscarPorEmail(any(Email.class)))
                .thenReturn(Optional.of(orientadorLogado));
        Mockito.when(orientadorLogado.id()).thenReturn(orientadorId);
        GrupoTg grupo = Mockito.mock(GrupoTg.class);
        Mockito.when(grupo.orientadorId()).thenReturn(orientadorId);
        Mockito.when(grupoTgRepositorio.buscarPorIdGrupo(any(GrupoTgId.class)))
                .thenReturn(Optional.of(grupo));

        Mockito.when(bancaRepositorio.existeBancaParaGrupo(any(GrupoTgId.class)))
                .thenReturn(false);

        // Act
        casoDeUso.executar(comandoValido);

        // Assert
        Mockito.verify(bancaRepositorio,
                Mockito.times(1)).salvar(any());
    }

    // ========================================================================
    // FLUXOS DE REGRA DE NEGÓCIO (FALHAS ESPERADAS)
    // ========================================================================

    @Test
    void deveLancarExcecaoQuandoNaoHouverNenhumAvaliadorNaBanca() {
        // Arrange: Comando com as duas listas VAZIAS
        var comandoSemAvaliadores = new MarcarBancaCaso.Comando(
                emailContaLogado,
                grupoTgId.texto(),
                LocalDate.now().plusDays(10),
                LocalTime.of(20, 0),
                "Teams",
                List.of(), // Vazio
                List.of()  // Vazio
        );

        Professor orientadorLogado = Mockito.mock(Professor.class);
        Mockito.when(orientadorLogado.id()).thenReturn(orientadorId);
        Mockito.when(professorRepositorio.buscarPorEmail(any(Email.class)))
                .thenReturn(Optional.of(orientadorLogado));

        GrupoTg grupo = Mockito.mock(GrupoTg.class);
        Mockito.when(grupo.orientadorId()).thenReturn(orientadorId);
        Mockito.when(grupoTgRepositorio.buscarPorIdGrupo(any(GrupoTgId.class)))
                .thenReturn(Optional.of(grupo));

        Mockito.when(bancaRepositorio.existeBancaParaGrupo(any(GrupoTgId.class)))
                .thenReturn(false);

        // Act & Assert
        // O erro vai estourar dentro do "Banca.nova()" pela falta de membros
        RegraNegocioExcecao excecao = assertThrows(RegraNegocioExcecao.class,
                () -> casoDeUso.executar(comandoSemAvaliadores));

        assertTrue(excecao.getMessage().contains("composição da banca"));
        Mockito.verify(bancaRepositorio, Mockito.never()).salvar(any());
    }

    // ========================================================================
    // FLUXOS DE SEGURANÇA E AUTORIZAÇÃO
    // ========================================================================

    @Test
    void deveLancarExcecaoQuandoAtorLogadoNaoForProfessor() {
        // Arrange
        // Simula que a conta do JWT pertence a um Aluno ou Admin. O Repositório de Professor não vai achar.
        Mockito.when(professorRepositorio.buscarPorEmail(any(Email.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        GenericaExcecao excecao = assertThrows(GenericaExcecao.class,
                () -> casoDeUso.executar(comandoValido));

        // Erro genérico do repositório (GN_001)
        assertTrue(excecao.getMessage().contains("orientador"));

        // Garante que a execução morreu no passo 1 e nem chamou as outras dependências
        Mockito.verify(grupoTgRepositorio, Mockito.never()).buscarPorIdGrupo(any());
        Mockito.verify(bancaRepositorio, Mockito.never()).salvar(any());
    }

    @Test
    void deveLancarExcecaoQuandoProfessorLogadoNaoForOrientadorDesteGrupo() {
        // Arrange
        Professor orientadorIntruso = Mockito.mock(Professor.class);
        // O ID do professor logado é DIFERENTE do ID do orientador oficial do grupo
        ProfessorId intrusoId = new ProfessorId(UUID.randomUUID());
        Mockito.when(orientadorIntruso.id()).thenReturn(intrusoId);

        Mockito.when(professorRepositorio.buscarPorEmail(any(Email.class)))
                .thenReturn(Optional.of(orientadorIntruso));

        GrupoTg grupo = Mockito.mock(GrupoTg.class);
        // orientadorId oficial (outro UUID)
        Mockito.when(grupo.orientadorId()).thenReturn(orientadorId);

        Mockito.when(grupoTgRepositorio.buscarPorIdGrupo(any(GrupoTgId.class)))
                .thenReturn(Optional.of(grupo));

        // Act & Assert
        RegraNegocioExcecao excecao = assertThrows(RegraNegocioExcecao.class,
                () -> casoDeUso.executar(comandoValido));

        assertTrue(excecao.getMessage().contains("marcar banca"));
        Mockito.verify(bancaRepositorio, Mockito.never()).salvar(any());
    }
}
