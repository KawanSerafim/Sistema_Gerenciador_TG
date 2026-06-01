package br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso;
import br.edu.com.fateczl.sistema.gerenciador.tg.administrador.dominio.entidade.Administrador;
import br.edu.com.fateczl.sistema.gerenciador.tg.administrador.dominio.repositorio.AdministradorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.MandatoDiretor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.MandatoDiretorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtribuirMandatoDiretorCasoTest {

    @Mock
    private ProfessorRepositorio professorRepositorio;

    @Mock
    private MandatoDiretorRepositorio mandatoDiretorRepositorio;

    @Mock
    private AdministradorRepositorio administradorRepositorio;

    @InjectMocks
    private AtribuirMandatoDiretorCaso casoDeUso;

    private final String EMAIL_ADMIN = "admin@cps.sp.gov.br";
    private final String MATRICULA = "1234567891012";

    private AtribuirMandatoDiretorCaso.Comando comandoValido;
    private Professor professorMock;
    private Administrador adminMock;

    @BeforeEach
    void setUp() {
        // Objeto de comando base para os testes
        comandoValido = new AtribuirMandatoDiretorCaso.Comando(
                EMAIL_ADMIN, MATRICULA, LocalDate.now(), LocalDate.now().plusYears(1),
                "base64String"
        );

        professorMock = mock(Professor.class);
        // O lenient() avisa ao Mockito que este mock pode ou não ser usado dependendo do teste
        lenient().when(professorMock.id()).thenReturn(new ProfessorId(UUID.randomUUID()));

        adminMock = mock(Administrador.class);
    }

    @Test
    @DisplayName("Deve atribuir um novo mandato ativo com sucesso quando os dados forem válidos")
    void deveAtribuirMandatoComSucesso() {
        // Arrange
        when(administradorRepositorio.buscarPorEmail(any(Email.class)))
                .thenReturn(Optional.of(adminMock));
        when(professorRepositorio.buscarPorMatricula(any(Matricula.class)))
                .thenReturn(Optional.of(professorMock));
        // Nenhum diretor ativo
        when(mandatoDiretorRepositorio.buscarMandatoVigente())
                .thenReturn(Optional.empty());

        // Act
        assertDoesNotThrow(() -> casoDeUso.executar(comandoValido));

        // Assert
        verify(mandatoDiretorRepositorio, times(1)).salvar(any(MandatoDiretor.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se o usuário que executou a ação não for um administrador")
    void deveLancarExcecaoSemPermissao() {
        // Arrange
        when(administradorRepositorio.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());

        // Act & Assert
        RegraNegocioExcecao ex = assertThrows(RegraNegocioExcecao.class, () -> casoDeUso.executar(comandoValido));
        assertEquals(CodigoErro.AU_003_ACAO_NAO_PERMITIDA_MOTIVO, ex.getCodigoErro());
        verify(mandatoDiretorRepositorio, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se a matrícula informada não pertencer a nenhum professor")
    void deveLancarExcecaoProfessorInexistente() {
        // Arrange
        when(administradorRepositorio.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(adminMock));
        when(professorRepositorio.buscarPorMatricula(any(Matricula.class))).thenReturn(Optional.empty());

        // Act & Assert
        GenericaExcecao ex = assertThrows(GenericaExcecao.class, () -> casoDeUso.executar(comandoValido));
        assertEquals(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, ex.getCodigoErro());
        verify(mandatoDiretorRepositorio, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atribuir mandato se a data final for menor que a data de início")
    void deveLancarExcecaoDatasIncoerentes() {
        // Arrange
        var comandoInvalido = new AtribuirMandatoDiretorCaso.Comando(
                EMAIL_ADMIN, MATRICULA, LocalDate.now(), LocalDate.now().minusDays(1), "base64String"
        );

        when(administradorRepositorio.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(adminMock));
        when(professorRepositorio.buscarPorMatricula(any(Matricula.class))).thenReturn(Optional.of(professorMock));

        // Act & Assert
        RegraNegocioExcecao ex = assertThrows(RegraNegocioExcecao.class, () -> casoDeUso.executar(comandoInvalido));
        assertEquals(CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO, ex.getCodigoErro());
        verify(mandatoDiretorRepositorio, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se já existir outro diretor com mandato ativo no sistema")
    void deveLancarExcecaoDiretorJaVigente() {
        // Arrange
        MandatoDiretor mandatoExistenteMock = mock(MandatoDiretor.class);

        when(administradorRepositorio.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(adminMock));
        when(professorRepositorio.buscarPorMatricula(any(Matricula.class))).thenReturn(Optional.of(professorMock));
        when(mandatoDiretorRepositorio.buscarMandatoVigente()).thenReturn(Optional.of(mandatoExistenteMock));

        // Act & Assert
        RegraNegocioExcecao ex = assertThrows(RegraNegocioExcecao.class, () -> casoDeUso.executar(comandoValido));
        assertEquals(CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO, ex.getCodigoErro());
        verify(mandatoDiretorRepositorio, never()).salvar(any());
    }
}
