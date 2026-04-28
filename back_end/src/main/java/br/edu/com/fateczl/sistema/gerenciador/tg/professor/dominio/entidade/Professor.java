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

    private Professor(
            ProfessorId id,
            Nome nome,
            Matricula matricula,
            ContaUsuarioId contaUsuarioId,
            CargoProfessor cargo
    ) {
        this.id = assegurarPresenca(id, "ID");
        this.nome = assegurarPresenca(nome, "nome");
        this.matricula = assegurarPresenca(matricula, "matrícula");
        this.contaUsuarioId = assegurarPresenca(
                contaUsuarioId,
                "ID da conta de usuário"
        );
        this.cargo = assegurarPresenca(cargo, "cargo");
    }

    // MÉTODOS FACTORY ---------------------------------------------------------

    public static Professor novo(
            ProfessorId id,
            Nome nome,
            Matricula matricula,
            ContaUsuarioId contaUsuarioId,
            CargoProfessor cargo
    ) {
        return new Professor(id, nome, matricula, contaUsuarioId, cargo);
    }

    public static Professor carregar(
            ProfessorId id,
            Nome nome,
            Matricula matricula,
            ContaUsuarioId contaUsuarioId,
            CargoProfessor cargo
    ) {
        return new Professor(id, nome, matricula, contaUsuarioId, cargo);
    }

    // MÉTODOS PARA GARANTIR PRESENÇA ------------------------------------------

    private <T> T assegurarPresenca(T objeto, String campo) {
        if(objeto == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    campo);
        }
        return objeto;
    }

    // MÉTODOS DE VERIFICAÇÃO --------------------------------------------------

    public boolean podeSerOrientador() { return cargo.podeSerOrientador(); }

    public boolean podeSerProfessorTg() { return cargo.podeSerProfessorTg(); }

    public boolean podeSerCoordenadorCurso() {
        return cargo.podeSerCoordenadorCurso();
    }

    // MÉTODOS DE ATUALIZAÇÃO --------------------------------------------------

    public void atualizarCargo(CargoProfessor novoCargo) {
        this.cargo = assegurarPresenca(novoCargo, "cargo");
    }

    // MÉTODOS DE CONTRATO -----------------------------------------------------

    @Override public Nome nome() { return nome; }
    @Override public String identificacao() { return this.matricula.valor(); }

    // MÉTODOS GETTERS DE DELEGAÇÃO --------------------------------------------

    public String idTexto() { return id.texto(); }
    public String nomeTexto() { return nome.valor(); }
    public String matriculaTexto() { return matricula.valor(); }
    public String contaUsuarioIdTexto() { return contaUsuarioId.texto(); }

    // MÉTODOS GETTERS ---------------------------------------------------------

    public ProfessorId id() { return id; }
    public Matricula matricula() { return matricula; }
    public ContaUsuarioId contaUsuarioId() { return contaUsuarioId; }
    public CargoProfessor cargo() { return cargo; }
}