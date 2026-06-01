package br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.entidade.Banca;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.objetosvalor.BancaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.objetosvalor.StatusBanca;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.repositorio.BancaRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.portas.PublicadorEventos;
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

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AtribuirNotasBancaCasoTest {
    @Mock
    private BancaRepositorio bancaRepositorio;
    @Mock private GrupoTgRepositorio grupoTgRepositorio;
    @Mock private ProfessorRepositorio professorRepositorio;
    @Mock
    private PublicadorEventos publicador;

    @InjectMocks
    private AtribuirNotasBancaCaso casoDeUso;

    private ProfessorId orientadorOficialId;
    private String orientadorEmail;
    private GrupoTgId grupoId;
    private BancaId bancaId;
    private AtribuirNotasBancaCaso.Comando comandoValido;

    @BeforeEach
    void setUp() {
        orientadorOficialId = new ProfessorId(UUID.randomUUID());
        orientadorEmail = "teste@cps.sp.gov.br";
        grupoId = new GrupoTgId(UUID.randomUUID());
        bancaId = new BancaId(UUID.randomUUID());

        // Comando simulando o Front-End enviando 3 notas
        Map<String, Double> notas = Map.of(
                "avaliador-1", 8.0,
                "avaliador-2", 9.0,
                "avaliador-3", 10.0
        );

        comandoValido = new AtribuirNotasBancaCaso.Comando(
                orientadorEmail,
                bancaId.texto(),
                notas
        );
    }

    // ========================================================================
    // FLUXO DE SUCESSO
    // ========================================================================

    @Test
    void deveAtribuirNotasESalvarComStatusAvaliada() {
        // Arrange
        Professor orientador = Mockito.mock(Professor.class);
        Mockito.when(orientador.id()).thenReturn(orientadorOficialId);
        Mockito.when(professorRepositorio.buscarPorEmail(any(Email.class)))
                .thenReturn(Optional.of(orientador));

        // Cria uma Banca REAL para podermos testar a lógica interna dela
        Banca bancaReal = Banca.carregar(
                bancaId,
                grupoId,
                LocalDateTime.now().minusDays(1), // Data no passado para permitir avaliação!
                "Sala 1",
                List.of(new ProfessorId(UUID.randomUUID())),
                List.of(),
                StatusBanca.MARCADA,
                new HashMap<>(),
                null
        );

        Mockito.when(bancaRepositorio.buscarPorId(bancaId)).thenReturn(Optional.of(bancaReal));

        GrupoTg grupo = Mockito.mock(GrupoTg.class);
        Mockito.when(grupo.orientadorId()).thenReturn(orientadorOficialId);
        Mockito.when(grupoTgRepositorio.buscarPorIdGrupo(grupoId)).thenReturn(Optional.of(grupo));

        // Act
        casoDeUso.executar(comandoValido);

        // Assert
        // 1. O método salvar deve ter sido acionado com a bancaReal
        Mockito.verify(bancaRepositorio, Mockito.times(1)).salvar(bancaReal);

        // 2. A banca agora deve estar AVALIADA
        assertEquals(StatusBanca.AVALIADA, bancaReal.status());

        // 3. A nota final (média) deve ter sido calculada (8 + 9 + 10) / 3 = 9.0
        assertEquals(9.0, bancaReal.notaFinal());
    }

    // ========================================================================
    // FLUXOS DE EXCEÇÃO (REGRA DE NEGÓCIO E SEGURANÇA)
    // ========================================================================

    @Test
    void deveLancarExcecaoQuandoBancaNaoForEncontrada() {
        // Arrange
        Professor orientador = Mockito.mock(Professor.class);
        Mockito.when(professorRepositorio.buscarPorEmail(any(Email.class)))
                .thenReturn(Optional.of(orientador));

        // Simula o banco não achando a banca
        Mockito.when(bancaRepositorio.buscarPorId(any(BancaId.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        GenericaExcecao excecao = assertThrows(GenericaExcecao.class,
                () -> casoDeUso.executar(comandoValido));

        assertTrue(excecao.getMessage().contains("banca"));
        Mockito.verify(grupoTgRepositorio, Mockito.never()).buscarPorIdGrupo(any());
        Mockito.verify(bancaRepositorio, Mockito.never()).salvar(any());
    }

    @Test
    void deveLancarExcecaoQuandoGrupoDaBancaNaoForEncontrado() {
        // Arrange
        Professor orientador = Mockito.mock(Professor.class);
        Mockito.when(professorRepositorio.buscarPorEmail(any(Email.class)))
                .thenReturn(Optional.of(orientador));

        Banca bancaMock = Mockito.mock(Banca.class);
        Mockito.when(bancaMock.grupoId()).thenReturn(grupoId);
        Mockito.when(bancaRepositorio.buscarPorId(any(BancaId.class)))
                .thenReturn(Optional.of(bancaMock));

        // Simula o grupo não existindo no banco
        Mockito.when(grupoTgRepositorio.buscarPorIdGrupo(any(GrupoTgId.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        GenericaExcecao excecao = assertThrows(GenericaExcecao.class,
                () -> casoDeUso.executar(comandoValido));

        assertTrue(excecao.getMessage().contains("grupo"));
        Mockito.verify(bancaRepositorio, Mockito.never()).salvar(any());
    }

    @Test
    void deveBloquearAtribuicaoQuandoOrientadorLogadoNaoForDonoDoGrupo() {
        // Arrange
        Professor orientadorIntruso = Mockito.mock(Professor.class);
        // ID Diferente do Orientador Oficial
        Mockito.when(orientadorIntruso.id()).thenReturn(new ProfessorId(UUID.randomUUID()));

        Mockito.when(professorRepositorio.buscarPorEmail(any(Email.class)))
                .thenReturn(Optional.of(orientadorIntruso));

        Banca bancaMock = Mockito.mock(Banca.class);
        Mockito.when(bancaMock.grupoId()).thenReturn(grupoId);
        Mockito.when(bancaRepositorio.buscarPorId(any(BancaId.class)))
                .thenReturn(Optional.of(bancaMock));

        GrupoTg grupo = Mockito.mock(GrupoTg.class);
        // Grupo aponta para o ID oficial
        Mockito.when(grupo.orientadorId()).thenReturn(orientadorOficialId);
        Mockito.when(grupoTgRepositorio.buscarPorIdGrupo(grupoId))
                .thenReturn(Optional.of(grupo));

        // Act & Assert
        RegraNegocioExcecao excecao = assertThrows(RegraNegocioExcecao.class,
                () -> casoDeUso.executar(comandoValido));

        assertTrue(excecao.getMessage().contains("orientador"));

        // Garante que ninguém consegue fraudar a nota do grupo dos outros!
        Mockito.verify(bancaMock, Mockito.never()).atribuirNotas(any());
        Mockito.verify(bancaRepositorio, Mockito.never()).salvar(any());
    }
}
