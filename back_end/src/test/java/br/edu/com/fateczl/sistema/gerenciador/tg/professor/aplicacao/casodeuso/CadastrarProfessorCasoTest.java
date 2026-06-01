package br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.administrador.dominio.entidade.Administrador;
import br.edu.com.fateczl.sistema.gerenciador.tg.administrador.dominio.repositorio.AdministradorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.eventos.ContaPendenteCriadaEvento;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.portas.PublicadorEventos;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.CriptografoSenhas;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Autoridade;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Senha;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.servicos.VerificadorUnicidadeEmail;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.CargoProfessor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.servicos.IdentificadorAutoridadesProfessor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.servicos.VerificadorUnicidadeProfessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastrarProfessorCasoTest {

    @Mock private ProfessorRepositorio professorRepositorio;
    @Mock private ContaUsuarioRepositorio contaUsuarioRepositorio;
    @Mock private CriptografoSenhas criptografo;
    @Mock private PublicadorEventos publicador;
    @Mock private VerificadorUnicidadeEmail verificadorEmail;
    @Mock private VerificadorUnicidadeProfessor verificadorProfessor;
    @Mock private IdentificadorAutoridadesProfessor identificadorAutoridades;
    @Mock private AdministradorRepositorio administradorRepositorio;
    @Mock private Administrador administrador;

    @InjectMocks
    private CadastrarProfessorCaso cadastrarProfessorCaso;

    @Test
    void deveCadastrarProfessorEContaComSucesso() {
        // Arrange com dados válidos segundo as regras de negócio
        CadastrarProfessorCaso.Comando comando = new CadastrarProfessorCaso.Comando(
                "admin@cps.sp.gov.br",
                "Carlos Silva",
                "1110482323001", // Matrícula válida com 13 dígitos
                "carlos.silva@cps.sp.gov.br", // Domínio de e-mail institucional
                "SenhaSegura123!",
                CargoProfessor.ORIENTADOR
        );
       when(administradorRepositorio.buscarPorEmail(any(Email.class)))
               .thenReturn(Optional.of(administrador));
        when(identificadorAutoridades.identificar(CargoProfessor.ORIENTADOR))
                .thenReturn(Set.of(Autoridade.ROLE_ORIENTADOR));

        doNothing().when(verificadorEmail).verificar(any(Email.class));
        doNothing().when(verificadorProfessor).verificar(any(Matricula.class));

        Senha senhaMock = mock(Senha.class);
        when(criptografo.criptografar(anyString())).thenReturn(senhaMock);

        // Act
        CadastrarProfessorCaso.Resposta resposta = cadastrarProfessorCaso.executar(comando);

        // Assert da Resposta
        assertNotNull(resposta.id());
        assertEquals("Carlos Silva", resposta.nome());
        assertEquals("1110482323001", resposta.matricula());
        assertEquals(CargoProfessor.ORIENTADOR, resposta.cargo());

        // Verificações de efeitos colaterais
        verify(verificadorEmail, times(1)).verificar(any(Email.class));
        verify(verificadorProfessor, times(1)).verificar(any(Matricula.class));
        verify(criptografo, times(1)).criptografar("SenhaSegura123!");
        verify(contaUsuarioRepositorio, times(1)).salvar(any(ContaUsuario.class));
        verify(professorRepositorio, times(1)).salvar(any(Professor.class));
        verify(publicador, times(1)).publicar(any(ContaPendenteCriadaEvento.class));
    }

    @Test
    void deveLancarExcecaoEInterromperFluxoQuandoEmailJaExistir() {
        // Arrange
        CadastrarProfessorCaso.Comando comando = new CadastrarProfessorCaso.Comando(
                "admin@cps.sp.gov.br",
                "Carlos Silva",
                "1110482323001",
                "carlos.silva@cps.sp.gov.br",
                "SenhaSegura123!",
                CargoProfessor.ORIENTADOR
        );
        when(administradorRepositorio.buscarPorEmail(any(Email.class)))
                .thenReturn(Optional.of(administrador));
        when(identificadorAutoridades.identificar(CargoProfessor.ORIENTADOR))
                .thenReturn(Set.of(Autoridade.ROLE_ORIENTADOR));

        doThrow(new RegraNegocioExcecao(CodigoErro.RN_002_REGISTRO_DUPLICADO, "E-mail já cadastrado"))
                .when(verificadorEmail).verificar(any(Email.class));

        // Act & Assert
        RegraNegocioExcecao excecao = assertThrows(RegraNegocioExcecao.class, () -> {
            cadastrarProfessorCaso.executar(comando);
        });

        assertEquals(CodigoErro.RN_002_REGISTRO_DUPLICADO, excecao.getCodigoErro());

        // Verifica que processos posteriores não foram engatilhados após a falha
        verify(verificadorProfessor, never()).verificar(any());
        verify(criptografo, never()).criptografar(anyString());
        verify(contaUsuarioRepositorio, never()).salvar(any());
        verify(professorRepositorio, never()).salvar(any());
        verify(publicador, never()).publicar(any());
    }
}