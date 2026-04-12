package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.eventos.ContaPendenteCriadaEvento;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.portas.PublicadorEventos;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.CriptografoSenhas;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Autoridade;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Senha;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.servicos.VerificadorUnicidadeEmail;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SolicitarAcessoAlunoCaso {
    private final AlunoRepositorio alunoRepositorio;
    private final ContaUsuarioRepositorio contaUsuarioRepositorio;
    private final CriptografoSenhas criptografo;
    private final PublicadorEventos publicador;
    private final VerificadorUnicidadeEmail verificadorEmail;

    public SolicitarAcessoAlunoCaso(
            AlunoRepositorio alunoRepositorio,
            ContaUsuarioRepositorio contaUsuarioRepositorio,
            CriptografoSenhas criptografo,
            PublicadorEventos publicador,
            VerificadorUnicidadeEmail verificadorEmail
    ) {
        this.alunoRepositorio = alunoRepositorio;
        this.contaUsuarioRepositorio = contaUsuarioRepositorio;
        this.criptografo = criptografo;
        this.publicador = publicador;
        this.verificadorEmail = verificadorEmail;
    }

    public record Comando(String matricula, String email, String senhaLimpa) {}
    public record Resposta(String idAluno, String nome, String email) {}

    // FLUXO PRINCIPAL ---------------------------------------------------------

    public Resposta executar(Comando comando) {
        Matricula matricula = new Matricula(comando.matricula());
        Email email = new Email(comando.email());
        Set<Autoridade> autoridades = Set.of(Autoridade.ROLE_ALUNO);

        verificadorEmail.verificar(email);

        Aluno aluno = buscarAluno(matricula);
        aluno.validarSolicitacaoAcesso();

        ContaUsuarioId novaContaId = new ContaUsuarioId(UUID.randomUUID());
        Senha senhaCriptografada = criptografo.criptografar(
                comando.senhaLimpa()
        );
        ContaUsuario novaConta = ContaUsuario.novo(
                novaContaId,
                email,
                senhaCriptografada,
                autoridades
        );

        aluno.vincularConta(novaContaId);

        contaUsuarioRepositorio.salvar(novaConta);
        alunoRepositorio.salvar(aluno);

        publicador.publicar(
                new ContaPendenteCriadaEvento(novaConta.emailTexto())
        );

        return new Resposta(
                aluno.idTexto(),
                aluno.nomeTexto(),
                email.valor()
        );
    }

    // FLUXO ESPECIALIZADO ---------------------------------------------------

    private Aluno buscarAluno(Matricula matricula) {
        return alunoRepositorio.buscarPorMatricula(matricula)
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                        "aluno"
                ));
    }
}