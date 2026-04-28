package br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.eventos.ContaPendenteCriadaEvento;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.portas.PublicadorEventos;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.CriptografoSenhas;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Autoridade;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Senha;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.servicos.VerificadorUnicidadeEmail;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.CargoProfessor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.servicos.IdentificadorAutoridadesProfessor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.servicos.VerificadorUnicidadeProfessor;

import java.util.Set;
import java.util.UUID;

public class CadastrarProfessorCaso {
    private final ProfessorRepositorio professorRepositorio;
    private final ContaUsuarioRepositorio contaUsuarioRepositorio;
    private final CriptografoSenhas criptografo;
    private final PublicadorEventos publicador;
    private final VerificadorUnicidadeEmail verificadorEmail;
    private final VerificadorUnicidadeProfessor verificadorProfessor;
    private final IdentificadorAutoridadesProfessor identificadorAutoridades;

    public CadastrarProfessorCaso(
            ProfessorRepositorio professorRepositorio,
            ContaUsuarioRepositorio contaUsuarioRepositorio,
            CriptografoSenhas criptografo,
            PublicadorEventos publicador,
            VerificadorUnicidadeEmail verificadorEmail,
            VerificadorUnicidadeProfessor verificadorProfessor,
            IdentificadorAutoridadesProfessor identificadorAutoridades
    ) {
        this.professorRepositorio = professorRepositorio;
        this.contaUsuarioRepositorio = contaUsuarioRepositorio;
        this.criptografo = criptografo;
        this.publicador = publicador;
        this.verificadorEmail = verificadorEmail;
        this.verificadorProfessor = verificadorProfessor;
        this.identificadorAutoridades = identificadorAutoridades;
    }

    public record Comando(
            String nome,
            String matricula,
            String email,
            String senha,
            CargoProfessor cargo
    ) {}

    public record Resposta(
            String id,
            String nome,
            String matricula,
            CargoProfessor cargo
    ) {}

    public Resposta executar(Comando comando) {
        Email emailAlvo = new Email(comando.email());
        Matricula matriculaAlvo = new Matricula(comando.matricula());
        Nome nome = new Nome(comando.nome());
        Set<Autoridade> autoridades = identificadorAutoridades
                .identificar(comando.cargo());

        verificadorEmail.verificar(emailAlvo);
        verificadorProfessor.verificar(matriculaAlvo);

        ContaUsuario novaConta = gerarNovaConta(
                emailAlvo,
                comando.senha(),
                autoridades
        );
        Professor novoProfessor = gerarNovoProfessor(
                nome, matriculaAlvo,
                novaConta.id(),
                comando.cargo()
        );

        contaUsuarioRepositorio.salvar(novaConta);
        professorRepositorio.salvar(novoProfessor);

        publicador.publicar(
                new ContaPendenteCriadaEvento(novaConta.emailTexto())
        );

        return new Resposta(
                novoProfessor.idTexto(),
                nome.valor(),
                matriculaAlvo.valor(),
                comando.cargo()
        );
    }

    private ContaUsuario gerarNovaConta(
            Email email,
            String senhaLimpa,
            Set<Autoridade> autoridades
    ) {
        Senha senhaCriptografada = criptografo.criptografar(senhaLimpa);

        return ContaUsuario.novo(
                new ContaUsuarioId(UUID.randomUUID()),
                email,
                senhaCriptografada,
                autoridades
        );
    }

    private Professor gerarNovoProfessor(
            Nome nome,
            Matricula matricula,
            ContaUsuarioId contaId,
            CargoProfessor cargo
    ) {
        return Professor.novo(
                new ProfessorId(UUID.randomUUID()),
                nome,
                matricula,
                contaId,
                cargo
        );
    }
}