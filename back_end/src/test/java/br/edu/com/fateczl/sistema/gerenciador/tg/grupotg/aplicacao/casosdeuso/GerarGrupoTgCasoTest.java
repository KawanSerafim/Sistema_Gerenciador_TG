package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso;


import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.AlunoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.StatusAluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.servicos.ValidadorComposicaoGrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GerarGrupoTgCasoTest {

    // Dependências "Mockadas" (Simuladas)
    @Mock private GrupoTgRepositorio grupoTgRepositorio;
    @Mock private CursoRepositorio cursoRepositorio;
    @Mock private AlunoRepositorio alunoRepositorio;
    @Mock private TurmaRepositorio turmaRepositorio;
    @Mock private ValidadorComposicaoGrupoTg validadorComposicao;

    // A classe que estamos testando (O Mockito injeta os mocks nela automaticamente)
    @InjectMocks
    private GerarGrupoTgCaso casoDeUso;

    // Variáveis auxiliares para os testes
    private String idContaAutorStr;
    private GerarGrupoTgCaso.Comando comandoValido;
    private int anoAtual;
    private int semestreAtual;

    @BeforeEach
    void setUp() {
        idContaAutorStr = UUID.randomUUID().toString();

        // Simula os dados do comando que viriam do Front-end / Controller
        comandoValido = new GerarGrupoTgCaso.Comando(
                idContaAutorStr,
                "Sistema de TCC",
                "Plataforma de gerenciamento de trabalho de graduação para os alunos da Fatec Zona Leste.",
                TipoTg.DESENVOLVIMENTO_SOFTWARE,
                List.of("1112568555555", "1112568677777")
        );

        // Calcula a data atual igual o Caso de Uso faz para poder mockar corretamente
        anoAtual = LocalDate.now().getYear();
        semestreAtual = LocalDate.now().getMonthValue() <= 6 ? 1 : 2;
    }

    @Test
    void deveGerarGrupoComSucessoQuandoTodosOsDadosForemValidos() {
        // Arrange (Preparação)
        Aluno alunoAutor = mock(Aluno.class);
        AlunoId autorId = new AlunoId(UUID.randomUUID());
        when(alunoAutor.id()).thenReturn(autorId);

        List<TurmaId> turmasIds = List.of(new TurmaId(UUID.randomUUID()));
        when(alunoAutor.turmasIds()).thenReturn(turmasIds);

        // Simula que o repositório encontrou o autor
        when(alunoRepositorio.buscarPorContaId(any(ContaUsuarioId.class)))
                .thenReturn(Optional.of(alunoAutor));

        // Simula uma turma válida (do semestre atual)
        Turma turmaAtual = mock(Turma.class);
        when(turmaAtual.anoLetivoValor()).thenReturn(anoAtual);
        when(turmaAtual.semestreLetivoValor()).thenReturn(semestreAtual);
        when(turmaAtual.cursoId()).thenReturn(new CursoId(UUID.randomUUID()));
        when(turmaAtual.disciplina()).thenReturn(Disciplina.TG1);
        when(turmaRepositorio.buscarTodasPorIds(turmasIds.stream().collect(Collectors.toUnmodifiableSet()))).thenReturn(List.of(turmaAtual));

        // Simula o Curso
        Curso curso = mock(Curso.class);
        when(curso.id()).thenReturn(new CursoId(UUID.randomUUID()));
        when(cursoRepositorio.buscarPorId(any(CursoId.class))).thenReturn(Optional.of(curso));

        // Simula os integrantes sendo encontrados no banco e com Status CADASTRADO
        Aluno integrante1 = mock(Aluno.class);
        when(integrante1.id()).thenReturn(new AlunoId(UUID.randomUUID()));
        when(integrante1.status()).thenReturn(StatusAluno.CADASTRADO);

        Aluno integrante2 = mock(Aluno.class);
        when(integrante2.id()).thenReturn(new AlunoId(UUID.randomUUID()));
        when(integrante2.status()).thenReturn(StatusAluno.CADASTRADO);

        when(alunoRepositorio.buscarPorMatriculas(anyList()))
                .thenReturn(Optional.of(List.of(integrante1, integrante2)));

        // Act (Ação)
        casoDeUso.executar(comandoValido);

        // Assert (Verificação)
        // Verifica se a validação de domínio foi chamada
        verify(validadorComposicao, times(1)).validar(eq(curso), eq(TipoTg.DESENVOLVIMENTO_SOFTWARE), anySet(), anyList());

        // Verifica se o método salvar do repositório foi chamado e captura o Grupo criado para validar
        ArgumentCaptor<GrupoTg> captor = ArgumentCaptor.forClass(GrupoTg.class);
        verify(grupoTgRepositorio, times(1)).salvar(captor.capture());

        GrupoTg grupoSalvo = captor.getValue();
        assertNotNull(grupoSalvo);
        assertEquals("Sistema de TCC", grupoSalvo.temaTg().nome());

        //Verifica a quantidade de alunos do grupo, integrantes + autor
        assertEquals(3, grupoSalvo.alunosIds().size());
    }

    @Test
    void deveLancarExcecaoQuandoAlunoNaoEstiverMatriculadoEmTurmaAtual() {
        // Arrange
        Aluno alunoAutor = mock(Aluno.class);
        List<TurmaId> turmasIds = List.of(new TurmaId(UUID.randomUUID()));
        when(alunoAutor.turmasIds()).thenReturn(turmasIds);

        when(alunoRepositorio.buscarPorContaId(any(ContaUsuarioId.class)))
                .thenReturn(Optional.of(alunoAutor));

        // Simula uma turma de um ano/semestre ANTIGO
        Turma turmaAntiga = mock(Turma.class);
        when(turmaAntiga.anoLetivoValor()).thenReturn(anoAtual - 1); // Ano passado
        //Não usa semestreLetivo pois o ano anterior ja vai acionar a excessão
        lenient().when(turmaRepositorio.buscarTodasPorIds(any())).thenReturn(List.of(turmaAntiga));

        // Act & Assert
        RegraNegocioExcecao excecao = assertThrows(RegraNegocioExcecao.class,
                () -> casoDeUso.executar(comandoValido));

        // Verifica se a exceção correta foi lançada pelo motivo correto
        assertTrue(excecao.getMessage().contains("matriculado no semestre atual"));
        verify(grupoTgRepositorio, never()).salvar(any()); // Garante que NUNCA tentou salvar
    }

    @Test
    void deveLancarExcecaoQuandoMembroNaoEstiverCadastrado() {
        // Arrange
        Aluno alunoAutor = mock(Aluno.class);
        when(alunoRepositorio.buscarPorContaId(any())).thenReturn(Optional.of(alunoAutor));

        Turma turmaAtual = mock(Turma.class);
        when(turmaAtual.anoLetivoValor()).thenReturn(anoAtual);
        when(turmaAtual.semestreLetivoValor()).thenReturn(semestreAtual);
        when(turmaAtual.cursoId()).thenReturn(new CursoId(UUID.randomUUID()));
        when(turmaRepositorio.buscarTodasPorIds(any())).thenReturn(List.of(turmaAtual));

        Curso curso = mock(Curso.class);
        when(cursoRepositorio.buscarPorId(any())).thenReturn(Optional.of(curso));

        // Simula um integrante com status inválido
        Aluno integrante1 = mock(Aluno.class);
        when(integrante1.status()).thenReturn(StatusAluno.CADASTRADO);

        Aluno integrante2Invalido = mock(Aluno.class);
        when(integrante2Invalido.status()).thenReturn(StatusAluno.AGUARDANDO_CONFIRMACAO); // Status Inválido!

        when(alunoRepositorio.buscarPorMatriculas(anyList()))
                .thenReturn(Optional.of(List.of(integrante1, integrante2Invalido)));

        // Act & Assert
        RegraNegocioExcecao excecao = assertThrows(RegraNegocioExcecao.class,
                () -> casoDeUso.executar(comandoValido));

        // Garante que a exceção é referente ao status CADASTRADO
        assertTrue(excecao.getMessage().contains("CADASTRADO"));
        verify(validadorComposicao, never()).validar(any(), any(), any(), any()); // Nem chegou a validar
        verify(grupoTgRepositorio, never()).salvar(any());
    }

    @Test
    void deveLancarExcecaoQuandoQuantidadeDeAlunosForDiferenteDasMatriculas() {
        // Arrange
        Aluno alunoAutor = mock(Aluno.class);
        when(alunoRepositorio.buscarPorContaId(any())).thenReturn(Optional.of(alunoAutor));

        Turma turmaAtual = mock(Turma.class);
        when(turmaAtual.anoLetivoValor()).thenReturn(anoAtual);
        when(turmaAtual.semestreLetivoValor()).thenReturn(semestreAtual);
        when(turmaAtual.cursoId()).thenReturn(new CursoId(UUID.randomUUID()));
        when(turmaRepositorio.buscarTodasPorIds(any())).thenReturn(List.of(turmaAtual));

        when(cursoRepositorio.buscarPorId(any())).thenReturn(Optional.of(mock(Curso.class)));

        // O comando exige 2 matriculas, mas vamos simular que o banco só encontrou 1 aluno
        Aluno integrante1 = mock(Aluno.class);
        when(alunoRepositorio.buscarPorMatriculas(anyList()))
                .thenReturn(Optional.of(List.of(integrante1)));

        // Act & Assert
        GenericaExcecao excecao = assertThrows(GenericaExcecao.class,
                () -> casoDeUso.executar(comandoValido));

        assertTrue(excecao.getMessage().contains("um ou mais alunos listados"));
    }
}
