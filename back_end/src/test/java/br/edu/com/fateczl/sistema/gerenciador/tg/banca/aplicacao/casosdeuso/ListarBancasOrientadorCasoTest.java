package br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.entidade.Banca;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.objetosvalor.StatusBanca;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.repositorio.BancaRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.TemaTg;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ListarBancasOrientadorCasoTest {

    @Mock private GrupoTgRepositorio grupoTgRepositorio;
    @Mock private BancaRepositorio bancaRepositorio;
    @Mock
    private ProfessorRepositorio professorRepositorio;

    @InjectMocks
    private ListarBancasOrientadorCaso casoDeUso;

    private ProfessorId professorId;
    private String emailContaLogada;

    @BeforeEach
    void setUp() {
        professorId = new ProfessorId(UUID.randomUUID());
        emailContaLogada = "teste@cps.sp.gov.br";
    }

    /**
     * Teste do fluxo de sucesso
     */
    @Test
    void deveListarBancasComSucessoEPermitirAtribuicaoQuandoBancaJaOcorreu() {
        // Arrange
        Professor orientador = Mockito.mock(Professor.class);
        Mockito.when(orientador.id()).thenReturn(professorId);
        Mockito.when(professorRepositorio.buscarPorEmail(any(Email.class)))
                .thenReturn(Optional.of(orientador));

        GrupoTg grupo = Mockito.mock(GrupoTg.class);
        TemaTg tema = new TemaTg("Sistema de Gestão",
                "Descrição de uma monografia de trabalho graduacao para testes unitarios");
        Mockito.when(grupo.id()).thenReturn(new GrupoTgId(UUID.randomUUID()));
        Mockito.when(grupo.temaTg()).thenReturn(tema);
        Mockito.when(grupo.tipoTg())
                .thenReturn(br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg.MONOGRAFIA);

        Mockito.when(grupoTgRepositorio.buscarPorOrientadorId(professorId))
                .thenReturn(List.of(grupo));

        // Banca no passado para validar o "podeAtribuir"
        Banca banca = Mockito.mock(Banca.class);
        Mockito.when(banca.idTexto()).thenReturn("banca-123");
        Mockito.when(banca.status()).thenReturn(StatusBanca.MARCADA);
        Mockito.when(banca.dataHora()).thenReturn(LocalDateTime.now().minusDays(1));

        Mockito.when(bancaRepositorio.buscarPorGrupoId(any(GrupoTgId.class)))
                .thenReturn(Optional.of(banca));

        // Act
        var resultado = casoDeUso.executar(new ListarBancasOrientadorCaso.Comando(emailContaLogada));

        // Assert
        assertFalse(resultado.isEmpty());
        var dto = resultado.getFirst();
        assertEquals("banca-123", dto.idBanca());
        assertEquals("Sistema de Gestão", dto.tema());
        assertTrue(dto.podeAtribuirNota());
        assertEquals("Pré Banca realizada", dto.situacao());
    }

    @Test
    void deveRetornarListaVaziaQuandoOrientadorNaoPossuirBancasMarcadas() {
        // Arrange
        Professor orientador = Mockito.mock(Professor.class);
        Mockito.when(orientador.id()).thenReturn(professorId);
        Mockito.when(professorRepositorio.buscarPorEmail(any(Email.class)))
                .thenReturn(Optional.of(orientador));

        // Possui grupo, mas sem banca vinculada
        GrupoTg grupoSemBanca = Mockito.mock(GrupoTg.class);
        Mockito.when(grupoTgRepositorio.buscarPorOrientadorId(professorId))
                .thenReturn(List.of(grupoSemBanca));

        Mockito.when(bancaRepositorio.buscarPorGrupoId(any())).thenReturn(Optional.empty());

        // Act
        var resultado = casoDeUso.executar(new ListarBancasOrientadorCaso.Comando(emailContaLogada));

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveBloquearAtribuicaoDeNotaQuandoBancaForFutura() {
        // Arrange
        Professor orientador = Mockito.mock(Professor.class);
        Mockito.when(orientador.id()).thenReturn(professorId);
        Mockito.when(professorRepositorio.buscarPorEmail(any(Email.class)))
                .thenReturn(Optional.of(orientador));

        GrupoTg grupo = Mockito.mock(GrupoTg.class);
        Mockito.when(grupo.temaTg()).thenReturn(
                new TemaTg("IA em Saúde",
                        "Descrição de um trabalho de graduação sobre IA na área de saúde"));
        Mockito.when(grupo.tipoTg())
                .thenReturn(br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg.ARTIGO);
        Mockito.when(grupoTgRepositorio.buscarPorOrientadorId(professorId))
                .thenReturn(List.of(grupo));

        // Banca no futuro
        Banca bancaFutura = Mockito.mock(Banca.class);
        Mockito.when(bancaFutura.status()).thenReturn(StatusBanca.MARCADA);
        Mockito.when(bancaFutura.dataHora()).thenReturn(LocalDateTime.now().plusWeeks(2));

        Mockito.when(bancaRepositorio.buscarPorGrupoId(any())).thenReturn(Optional.of(bancaFutura));

        // Act
        var resultado = casoDeUso.executar(new ListarBancasOrientadorCaso.Comando(emailContaLogada));

        // Assert
        assertFalse(resultado.getFirst().podeAtribuirNota());
        assertEquals("MARCADA", resultado.getFirst().situacao());
    }
}
