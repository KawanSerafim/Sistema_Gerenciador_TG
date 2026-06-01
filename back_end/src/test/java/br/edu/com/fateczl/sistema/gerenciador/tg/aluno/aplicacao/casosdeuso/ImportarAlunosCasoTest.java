package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.portas.LeitorArquivoAlunos;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.servicos.ValidadorAutorizacaoImportacao;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.servicos.ValidadorCabecalhoArquivo;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportarAlunosCasoTest {

    @Mock private AlunoRepositorio alunoRepositorio;
    @Mock private ProfessorRepositorio professorRepositorio;
    @Mock private TurmaRepositorio turmaRepositorio;
    @Mock private LeitorArquivoAlunos leitorArquivo;
    @Mock private ValidadorAutorizacaoImportacao validadorAutorizacao;
    @Mock private ValidadorCabecalhoArquivo validadorCabecalho;

    @InjectMocks
    private ImportarAlunosCaso importarAlunosCaso;

    private final String EMAIL_LOGADO = "professor.coordenador@cps.sp.gov.br";

    @BeforeEach
    void configurarSecurityContext() {
        // A forma correta e segura para injetar o contexto de segurança em testes
        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn(EMAIL_LOGADO);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void limparSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveImportarAlunosComSucesso() {
        String idTurma = UUID.randomUUID().toString();
        InputStream arquivoMock = new ByteArrayInputStream("dados".getBytes());
        ImportarAlunosCaso.Comando comando = new ImportarAlunosCaso.Comando(
                idTurma,
                arquivoMock,
                1024, // Tamanho dentro do limite (1KB)
                "alunos.xlsx"
        );

        Professor autorMock = mock(Professor.class);
        Turma turmaMock = mock(Turma.class);
        when(turmaMock.id()).thenReturn(new TurmaId(UUID.fromString(idTurma)));

        when(professorRepositorio.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(autorMock));
        when(turmaRepositorio.buscarPorId(any(TurmaId.class))).thenReturn(Optional.of(turmaMock));

        doNothing().when(validadorAutorizacao).validar(any(), any());

        LeitorArquivoAlunos.DadosArquivo arquivoLidoMock = mock(LeitorArquivoAlunos.DadosArquivo.class);
        when(leitorArquivo.ler(any(InputStream.class))).thenReturn(arquivoLidoMock);
        doNothing().when(validadorCabecalho).validar(any(), any());

        LeitorArquivoAlunos.DadosAluno dadosAluno = mock(LeitorArquivoAlunos.DadosAluno.class);
        when(dadosAluno.matricula()).thenReturn("1110482323001");
        when(dadosAluno.nome()).thenReturn("THIAGO SILVA ANTENOR");
        when(arquivoLidoMock.alunos()).thenReturn(List.of(dadosAluno));

        // Simula que o aluno ainda não existe na base para engatilhar a criação de uma nova entidade
        when(alunoRepositorio.buscarPorMatricula(any(Matricula.class))).thenReturn(Optional.empty());

        ImportarAlunosCaso.Resposta resposta = importarAlunosCaso.executar(comando);

        assertEquals(1, resposta.alunos().size());
        assertEquals("1110482323001", resposta.alunos().getFirst().matricula());

        verify(alunoRepositorio, times(1)).salvar(any(Aluno.class));
    }

    @Test
    void deveLancarExcecaoQuandoArquivoForMaiorQueOLimite() {
        ImportarAlunosCaso.Comando comando = new ImportarAlunosCaso.Comando(
                UUID.randomUUID().toString(),
                new ByteArrayInputStream("dados".getBytes()),
                3 * 1024 * 1024, // 3MB (Acima do limite de 2MB)
                "alunos.xlsx"
        );

        ValidacaoExcecao excecao = assertThrows(ValidacaoExcecao.class, () -> {
            importarAlunosCaso.executar(comando);
        });

        assertEquals(CodigoErro.VD_008_ARQUIVO_INVALIDO, excecao.getCodigoErro());

        // Garante que o processamento para imediatamente sem invocar repositórios
        verify(professorRepositorio, never()).buscarPorEmail(any());
        verify(leitorArquivo, never()).ler(any());
        verify(alunoRepositorio, never()).salvar(any());
    }
}