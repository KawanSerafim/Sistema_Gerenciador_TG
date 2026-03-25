package br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.ParametrosCurso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;

public class Curso {
    private final CursoId id;
    private Nome nome;
    private ParametrosCurso parametros;
    private ProfessorId coordenadorId;

    private Curso(CursoId id, Nome nome, ParametrosCurso parametros,
                  ProfessorId coordenadorId) {
        this.id = assegurarPresenca(id, "ID");
        this.nome = assegurarPresenca(nome, "nome");
        this.parametros = assegurarPresenca(parametros, "parâmetros");
        this.coordenadorId = assegurarPresenca(coordenadorId, "ID " +
                "do coordenador");
    }

    // Métodos Factory ---------------------------------------------------------

    public static Curso novo(CursoId id, Nome nome, ParametrosCurso parametros,
                             ProfessorId coordenadorId) {
        return new Curso(id, nome, parametros, coordenadorId);
    }

    public static Curso carregar(CursoId id, Nome nome,
                                 ParametrosCurso parametros,
                                 ProfessorId coordenadorId) {
        return new Curso(id, nome, parametros, coordenadorId);
    }

    // Métodos especiais -------------------------------------------------------

    private <T> T assegurarPresenca(T objeto, String campo) {
        if(objeto == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    campo);
        }
        return objeto;
    }

    public boolean validarDisciplina(Disciplina disciplina) {
        return parametros.validarDisciplina(disciplina);
    }

    public boolean validarTurno(Turno turno) {
        return parametros.validarTurno(turno);
    }

    public boolean validarTipoTg(TipoTg tipoTg) {
        return parametros.validarTipoTg(tipoTg);
    }

    public boolean validarQtdAlunosGrupo(TipoTg tipoTg, Integer quantidade) {
        return parametros.validarQtdAlunosGrupo(tipoTg, quantidade);
    }

    // Métodos de Atualização --------------------------------------------------

    public void atualizarNome(Nome novoNome) {
        this.nome = assegurarPresenca(novoNome, "nome");
    }

    public void atualizarParametros(ParametrosCurso novosParametros) {
        this.parametros = assegurarPresenca(novosParametros, "parâmetros");
    }

    public void atualizarCoordenador(ProfessorId novoCoordenadorId) {
        this.coordenadorId = assegurarPresenca(novoCoordenadorId,
                "coordenador");
    }

    // Métodos Getters ---------------------------------------------------------

    public CursoId id() { return id; }
    public String idTexto() { return id.valor().toString(); }
    public Nome nome() { return nome; }
    public String nomeTexto() { return nome.valor(); }
    public ParametrosCurso parametros() { return parametros; }
    public ProfessorId coordenadorId() { return coordenadorId; }
}