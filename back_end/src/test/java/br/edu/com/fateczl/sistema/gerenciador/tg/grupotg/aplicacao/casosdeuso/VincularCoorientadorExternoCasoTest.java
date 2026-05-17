package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.AlunoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.entidade.CoorientadorExterno;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.objetosvalor.Origem;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.repositorio.CoorientadorExternoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.TemaTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.TipoCoorientador;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class VincularCoorientadorExternoCasoTest {

    @Mock
    private AlunoRepositorio alunoRepositorio;
    @Mock private GrupoTgRepositorio grupoTgRepositorio;
    @Mock private CoorientadorExternoRepositorio coorientadorRepositorio;

    @InjectMocks
    private VincularCoorientadorExternoCaso casoDeUso;

    private AlunoId alunoId;
    private GrupoTgId grupoId;
    private ProfessorId orientadorId;
    private VincularCoorientadorExternoCaso.Comando comandoBase;

    @BeforeEach
    void setUp() {
        alunoId = new AlunoId(UUID.randomUUID());
        grupoId = new GrupoTgId(UUID.randomUUID());
        orientadorId = new ProfessorId(UUID.randomUUID());

        comandoBase = new VincularCoorientadorExternoCaso.Comando(
                UUID.randomUUID().toString(),
                "Jill Valentina",
                "TestesSoftware"
        );
    }

    // Helper para criar um GrupoTg "Real"
    private GrupoTg criarGrupoReal(
            ProfessorId idOrientador,
            String idCoorientador,
            TipoCoorientador tipo) {
        return GrupoTg.carregar(
                grupoId,
                idOrientador,
                idCoorientador,
                tipo,
                new CursoId(UUID.randomUUID()),
                EnumSet.of(Disciplina.TG1),
                new TemaTg("Desenvolvimento de SO",
                        "Desenvolvimento de um sistema operacional utilizando a linguagem Java"),
                TipoTg.DESENVOLVIMENTO_SOFTWARE,
                List.of(alunoId)
        );
    }

    private void configurarMockAlunoLogado() {
        Aluno aluno = Mockito.mock(Aluno.class);
        Mockito.when(aluno.id()).thenReturn(alunoId);
        Mockito.when(alunoRepositorio.buscarPorContaId(any(ContaUsuarioId.class)))
                .thenReturn(Optional.of(aluno));
    }

    // ========================================================================
    // FLUXO DE SUCESSO
    // ========================================================================

    @Test
    void deveVincularCoorientadorESalvarGrupoComSucesso() {
        // Arrange
        configurarMockAlunoLogado();

        // Cria grupo COM orientador e SEM coorientador
        GrupoTg grupoReal = criarGrupoReal(orientadorId, null, null);
        Mockito.when(grupoTgRepositorio.buscarPorAlunoId(alunoId)).thenReturn(Optional.of(grupoReal));

        // Cria o CoorientadorExterno que será retornado (ou salvo)
        CoorientadorExterno coorientadorMock = Mockito.mock(CoorientadorExterno.class);
        Mockito.when(coorientadorMock.idTexto()).thenReturn("coorientador-123");

        // Simula que achou o coorientador no banco de dados
        Mockito.when(coorientadorRepositorio.buscarPorNomeEOrigem(any(Nome.class), any(Origem.class)))
                .thenReturn(Optional.of(coorientadorMock));

        // Act
        casoDeUso.executar(comandoBase);

        // Assert
        // 1. Valida se salvou no repositório
        Mockito.verify(grupoTgRepositorio, Mockito.times(1)).salvar(grupoReal);

        // 2. Valida se a entidade mudou de estado corretamente
        assertEquals(TipoCoorientador.EXTERNO, grupoReal.tipoCoorientador());
        assertEquals("coorientador-123", grupoReal.coorientadorIdTexto());
    }

    // ========================================================================
    // FLUXOS DE FALHA (REGRAS DE NEGÓCIO)
    // ========================================================================

    @Test
    void deveLancarExcecaoQuandoGrupoNaoTiverOrientadorPrincipal() {
        // Arrange
        configurarMockAlunoLogado();

        // Cria grupo SEM orientador (null)
        GrupoTg grupoSemOrientador = criarGrupoReal(null, null, null);
        Mockito.when(grupoTgRepositorio.buscarPorAlunoId(alunoId)).thenReturn(Optional.of(grupoSemOrientador));

        // Act & Assert
        RegraNegocioExcecao excecao = assertThrows(RegraNegocioExcecao.class,
                () -> casoDeUso.executar(comandoBase));

        // Verifica se a mensagem ou o contexto da exceção é o esperado
        assertTrue(excecao.getMessage().contains("orientador vinculado"));

        // Garante que não salvou o grupo
        Mockito.verify(grupoTgRepositorio, Mockito.never()).salvar(any());
        Mockito.verify(coorientadorRepositorio, Mockito.never()).salvar(any());
    }

    @Test
    void deveLancarExcecaoQuandoGrupoJaPossuirUmCoorientador() {
        // Arrange
        configurarMockAlunoLogado();

        // Cria grupo COM orientador e COM coorientador já preenchido
        GrupoTg grupoLotado = criarGrupoReal(orientadorId, "coorientador-antigo", TipoCoorientador.EXTERNO);
        Mockito.when(grupoTgRepositorio.buscarPorAlunoId(alunoId)).thenReturn(Optional.of(grupoLotado));

        // Act & Assert
        RegraNegocioExcecao excecao = assertThrows(RegraNegocioExcecao.class,
                () -> casoDeUso.executar(comandoBase));

        assertTrue(excecao.getMessage().contains("já possui um coorientador")
                || excecao.getMessage().contains("estado inválido"));

        Mockito.verify(grupoTgRepositorio, Mockito.never()).salvar(any());
    }


    @Test
    void deveLancarExcecaoQuandoNaoEncontrarGrupoDoAluno() {
        // Arrange
        Aluno alunoIntruso = Mockito.mock(Aluno.class);
        // ID Diferente do ID que está no GrupoTg
        Mockito.when(alunoIntruso.id()).thenReturn(new AlunoId(UUID.randomUUID()));
        Mockito.when(alunoRepositorio.buscarPorContaId(any())).thenReturn(Optional.of(alunoIntruso));
        //Quando buscar pelo grupo usando o id do aluno retorna um optinal vazio
        Mockito.when(grupoTgRepositorio.buscarPorAlunoId(alunoIntruso.id())).thenReturn(Optional.empty());

        // Act & Assert
        GenericaExcecao excecao = assertThrows(GenericaExcecao.class,
                () -> casoDeUso.executar(comandoBase));

        // Erro estourado pelo Caso de Uso
        assertTrue(excecao.getMessage().contains("grupo do aluno"));
        Mockito.verify(grupoTgRepositorio, Mockito.never()).salvar(any());
    }
}