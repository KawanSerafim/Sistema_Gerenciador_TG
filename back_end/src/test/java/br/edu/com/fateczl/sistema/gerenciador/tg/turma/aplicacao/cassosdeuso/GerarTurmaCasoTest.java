package br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.cassosdeuso;


import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso.GerarTurmaCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.servicos.ValidadorComposicaoTurma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.servicos.VerificadorUnicidadeTurma;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.servicos.ValidadorCoordenadorCurso;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GerarTurmaCasoTest {

    @Mock private TurmaRepositorio turmaRepositorio;
    @Mock private CursoRepositorio cursoRepositorio;
    @Mock private ProfessorRepositorio professorRepositorio;
    @Mock private ValidadorComposicaoTurma validadorComposicao;
    @Mock private VerificadorUnicidadeTurma verificadorUnicidade;
    @Mock private ValidadorCoordenadorCurso validadorCoordenador;

    @InjectMocks
    private GerarTurmaCaso gerarTurmaCaso;

    private final String EMAIL_COORDENADOR = "coordenador@cps.sp.gov.br";

    @BeforeEach
    void configurarSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn(EMAIL_COORDENADOR);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void limparSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveGerarTurmaComSucesso() {
        GerarTurmaCaso.Comando comando = new GerarTurmaCaso.Comando(
                "1110482323001",
                Disciplina.TG1,
                Turno.NOITE,
                2026,
                2
        );

        Professor coordenadorMock = mock(Professor.class);
        ProfessorId coordenadorId = new ProfessorId(UUID.randomUUID());
        when(coordenadorMock.id()).thenReturn(coordenadorId);

        Curso cursoMock = mock(Curso.class);
        when(cursoMock.id()).thenReturn(new CursoId(UUID.randomUUID()));
        when(cursoMock.nomeTexto()).thenReturn("Análise de Sistemas");

        Professor professorTgMock = mock(Professor.class);
        when(professorTgMock.id()).thenReturn(new ProfessorId(UUID.randomUUID()));
        when(professorTgMock.nomeTexto()).thenReturn("Prof. Orientador TG");

        when(professorRepositorio.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(coordenadorMock));
        when(cursoRepositorio.buscarPorCoordenadorId(coordenadorId)).thenReturn(Optional.of(cursoMock));
        when(professorRepositorio.buscarPorMatricula(any(Matricula.class))).thenReturn(Optional.of(professorTgMock));

        doNothing().when(validadorCoordenador).validar(any(Professor.class));
        doNothing().when(validadorComposicao).validar(any(), any(), any(), any());
        doNothing().when(verificadorUnicidade).verificar(any(), any(), any(), any());

        GerarTurmaCaso.Resposta resposta = gerarTurmaCaso.executar(comando);

        assertNotNull(resposta.id());
        assertEquals("Análise de Sistemas", resposta.nomeCurso());
        assertEquals(Disciplina.TG1, resposta.disciplina());
        verify(turmaRepositorio, times(1)).salvar(any(Turma.class));
    }

    @Test
    void deveLancarExcecaoQuandoCursoDoCoordenadorNaoForEncontrado() {
        GerarTurmaCaso.Comando comando = new GerarTurmaCaso.Comando(
                "1110482323001",
                Disciplina.TG1,
                Turno.NOITE,
                2026,
                2
        );

        Professor coordenadorMock = mock(Professor.class);
        ProfessorId coordenadorId = new ProfessorId(UUID.randomUUID());
        when(coordenadorMock.id()).thenReturn(coordenadorId);

        when(professorRepositorio.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(coordenadorMock));
        doNothing().when(validadorCoordenador).validar(any(Professor.class));

        // Simula que o coordenador não está atrelado a nenhum curso
        when(cursoRepositorio.buscarPorCoordenadorId(coordenadorId)).thenReturn(Optional.empty());

        GenericaExcecao excecao = assertThrows(GenericaExcecao.class, () -> {
            gerarTurmaCaso.executar(comando);
        });

        assertEquals(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, excecao.getCodigoErro());
        verify(validadorComposicao, never()).validar(any(), any(), any(), any());
        verify(turmaRepositorio, never()).salvar(any(Turma.class));
    }
}