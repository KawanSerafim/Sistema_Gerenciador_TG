package br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.identificadores.Coorientador;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.StatusContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.CargoProfessor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;

import java.util.UUID;

public class Professor implements Coorientador {
    private final ProfessorId id;
    private final Nome nome;
    private final Matricula matricula;
    private final ContaUsuario contaUsuario;
    private CargoProfessor cargo;

    private Professor(ProfessorId id, Nome nome, Matricula matricula,
                      ContaUsuario contaUsuario,
                      CargoProfessor cargo) {
        this.id = id;
        this.nome = assegurarPresenca(nome, "nome");
        this.matricula = assegurarPresenca(matricula, "matrícula");
        this.contaUsuario = assegurarPresenca(contaUsuario, "conta de usuário");
        this.cargo = assegurarPresenca(cargo, "cargo");
    }

    // Métodos Factory ---------------------------------------------------------

    public static Professor novo(Nome nome, Matricula matricula,
                                 ContaUsuario contaUsuario,
                                 CargoProfessor cargo) {
        return new Professor(new ProfessorId(UUID.randomUUID()), nome,
                matricula, contaUsuario, cargo);
    }

    public static Professor carregar(ProfessorId id, Nome nome,
                                     Matricula matricula,
                                     ContaUsuario contaUsuario,
                                     CargoProfessor cargo) {
        if(id == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "ID do professor");
        }

        return new Professor(id, nome, matricula, contaUsuario, cargo);
    }

    // Métodos especiais -------------------------------------------------------

    private <T> T assegurarPresenca(T objeto, String campo) {
        if(objeto == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    campo);
        }
        return objeto;
    }

    public boolean podeSerOrientador() {
        return cargo.podeSerOrientador();
    }

    public boolean podeSerProfessorTg() {
        return cargo.podeSerProfessorTg();
    }

    public boolean podeSerCoordenadorCurso() {
        return cargo.podeSerCoordenadorCurso();
    }

    // Métodos de Atualização --------------------------------------------------

    public void atualizarCargo(CargoProfessor novoCargo) {
        this.cargo = assegurarPresenca(novoCargo, "cargo");
    }

    // Métodos de contrato -----------------------------------------------------

    @Override public Nome nome() { return nome; }
    @Override public String identificacao() { return this.matricula.valor(); }

    // Métodos Getters de Delegação --------------------------------------------

    public Email email() {
        return contaUsuario.email();
    }

    public StatusContaUsuario statusContaUsuario() {
        return contaUsuario.status();
    }

    // Métodos Getters ---------------------------------------------------------

    public ProfessorId id() { return id; }
    public String idTexto() { return id.valor().toString(); }
    public String nomeTexto() { return nome.valor(); }
    public Matricula matricula() { return matricula; }
    public String matriculaTexto() { return matricula.valor(); }
    public ContaUsuario contaUsuario() { return contaUsuario; }
    public CargoProfessor cargo() { return cargo; }
}