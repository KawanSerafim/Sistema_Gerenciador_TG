package br.edu.com.fateczl.sistema.gerenciador.tcc.professor.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tcc.contausuario.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tcc.contausuario.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tcc.contausuario.objetosvalor.StatusContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tcc.professor.objetosvalor.CargoProfessor;
import br.edu.com.fateczl.sistema.gerenciador.tcc.professor.objetosvalor.ProfessorId;

public class Professor {
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
        return new Professor(null, nome, matricula, contaUsuario, cargo);
    }

    public static Professor carregar(ProfessorId id, Nome nome,
                                     Matricula matricula,
                                     ContaUsuario contaUsuario,
                                     CargoProfessor cargo) {
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

    // Métodos de Atualização --------------------------------------------------

    public void atualizarCargo(CargoProfessor novoCargo) {
        this.cargo = assegurarPresenca(novoCargo, "cargo");
    }

    // Métodos Getters de Delegação --------------------------------------------

    public Email email() {
        return contaUsuario.email();
    }

    public StatusContaUsuario statusContaUsuario() {
        return contaUsuario.status();
    }

    // Métodos Getters ---------------------------------------------------------

    public ProfessorId id() { return id; }
    public Nome nome() { return nome; }
    public Matricula matricula() { return matricula; }
    public ContaUsuario contaUsuario() { return contaUsuario; }
    public CargoProfessor cargo() { return cargo; }
}