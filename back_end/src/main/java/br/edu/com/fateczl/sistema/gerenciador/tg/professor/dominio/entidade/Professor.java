package br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.identificadores.Coorientador;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.CargoProfessor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;

public class Professor implements Coorientador {
    private final ProfessorId id;
    private final Nome nome;
    private final Matricula matricula;
    private final ContaUsuarioId contaUsuarioId;
    private CargoProfessor cargo;

    private Professor(ProfessorId id, Nome nome, Matricula matricula,
                      ContaUsuarioId contaUsuarioId, CargoProfessor cargo) {
        this.id = assegurarPresenca(id, "ID");
        this.nome = assegurarPresenca(nome, "nome");
        this.matricula = assegurarPresenca(matricula, "matrícula");
        this.contaUsuarioId = assegurarPresenca(contaUsuarioId, "ID da conta " +
                "de usuário");
        this.cargo = assegurarPresenca(cargo, "cargo");
    }

    // Métodos Factory ---------------------------------------------------------

    public static Professor novo(ProfessorId id, Nome nome, Matricula matricula,
                                 ContaUsuarioId contaUsuarioId,
                                 CargoProfessor cargo) {
        return new Professor(id, nome, matricula, contaUsuarioId, cargo);
    }

    public static Professor carregar(ProfessorId id, Nome nome,
                                     Matricula matricula,
                                     ContaUsuarioId contaUsuarioId,
                                     CargoProfessor cargo) {
        return new Professor(id, nome, matricula, contaUsuarioId, cargo);
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

    // Métodos Getters ---------------------------------------------------------

    public ProfessorId id() { return id; }
    public String idTexto() { return id.valor().toString(); }
    public String nomeTexto() { return nome.valor(); }
    public Matricula matricula() { return matricula; }
    public String matriculaTexto() { return matricula.valor(); }
    public ContaUsuarioId contaUsuarioId() { return contaUsuarioId; }
    public CargoProfessor cargo() { return cargo; }
}