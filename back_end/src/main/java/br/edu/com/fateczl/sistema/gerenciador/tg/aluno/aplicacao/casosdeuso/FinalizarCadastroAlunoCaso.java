package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.StatusAluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.eventos.ContaPendenteCriadaEvento;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.portas.PublicadorEventos;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.CriptografoSenhas;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Senha;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;

public class FinalizarCadastroAlunoCaso {
    private final AlunoRepositorio alunoRepositorio;
    private final ContaUsuarioRepositorio contaUsuarioRepositorio;
    private final CriptografoSenhas criptografo;
    private final PublicadorEventos publicador;

    public FinalizarCadastroAlunoCaso(AlunoRepositorio alunoRepositorio,
                                      ContaUsuarioRepositorio contaUsuarioRepositorio,
                                      CriptografoSenhas criptografo,
                                      PublicadorEventos publicador) {
        this.alunoRepositorio = alunoRepositorio;
        this.contaUsuarioRepositorio = contaUsuarioRepositorio;
        this.criptografo = criptografo;
        this.publicador = publicador;
    }

    public record Comando(String matricula, String email, String senhaLimpa) {}
    public record Resposta(String idAluno, String nome, String email) {}

    // FLUXO PRINCIPAL ---------------------------------------------------------

    public Resposta executar(Comando comando) {
        Matricula matricula = new Matricula(comando.matricula());
        Email email = new Email(comando.email());

        validarUnicidadeEmail(email);
        Aluno aluno = buscarEValidarAluno(matricula);

        Senha senhaCriptografada = criptografo.criptografar(
                comando.senhaLimpa());
        ContaUsuario novaConta = ContaUsuario.novo(email, senhaCriptografada);

        aluno.atualizarContaUsuario(novaConta);

        contaUsuarioRepositorio.salvar(novaConta);
        alunoRepositorio.salvar(aluno);

        publicador.publicar(new ContaPendenteCriadaEvento(
                novaConta.emailTexto()));

        return new Resposta(aluno.idTexto(), aluno.nomeTexto(), email.valor());
    }

    // FLUXOS ESPECIALIZADOS ---------------------------------------------------

    private void validarUnicidadeEmail(Email email) {
        contaUsuarioRepositorio.buscarPorEmail(email).ifPresent(conta -> {
            throw new RegraNegocioExcecao(CodigoErro.RN_002_REGISTRO_DUPLICADO,
                    "e-mail");
        });
    }

    private Aluno buscarEValidarAluno(Matricula matricula) {
        Aluno aluno = alunoRepositorio.buscarPorMatricula(matricula)
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "aluno"));

        if(aluno.status() != StatusAluno.PRE_CADASTRO) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO, "aluno",
                    "PRE_CADASTRO");
        }
        return aluno;
    }
}