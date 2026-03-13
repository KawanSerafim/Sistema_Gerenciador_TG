package br.edu.com.fateczl.sistema.gerenciador.tcc.curso.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.objetosvalor.Turno;
import br.edu.com.fateczl.sistema.gerenciador.tcc.curso.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tcc.curso.objetosvalor.ParametrosCurso;
import br.edu.com.fateczl.sistema.gerenciador.tcc.curso.objetosvalor.TipoTcc;
import br.edu.com.fateczl.sistema.gerenciador.tcc.professor.entidade.Professor;

public class Curso {
    private final CursoId id;
    private Nome nome;
    private ParametrosCurso parametros;
    private Professor coordenador;

    private Curso(CursoId id, Nome nome, ParametrosCurso parametros,
                  Professor coordenador) {
        this.id = id;
        this.nome = assegurarPresenca(nome, "nome");
        this.parametros = assegurarPresenca(parametros, "parâmetros");
        this.coordenador = assegurarPresenca(coordenador, "coordenador");
    }

    // Métodos Factory ---------------------------------------------------------

    public static Curso novo(Nome nome, ParametrosCurso parametros,
                             Professor coordenador) {
        return new Curso(null, nome, parametros, coordenador);
    }

    public static Curso carregar(CursoId id, Nome nome,
                                 ParametrosCurso parametros,
                                 Professor coordenador) {
        if(id == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "ID do curso");
        }

        return new Curso(id, nome, parametros, coordenador);
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

    public boolean validarTipoTcc(TipoTcc tipoTcc) {
        return parametros.validarTipoTcc(tipoTcc);
    }

    public boolean validarQtdAlunosGrupo(TipoTcc tipoTcc, Integer quantidade) {
        return parametros.validarQtdAlunosGrupo(tipoTcc, quantidade);
    }

    // Métodos de Atualização --------------------------------------------------

    public void atualizarNome(Nome novoNome) {
        this.nome = assegurarPresenca(novoNome, "nome");
    }

    public void atualizarParametros(ParametrosCurso novosParametros) {
        this.parametros = assegurarPresenca(novosParametros, "parâmetros");
    }

    public void atualizarCoordenador(Professor novoCoordenador) {
        this.coordenador = assegurarPresenca(novoCoordenador, "coordenador");
    }

    // Métodos Getters ---------------------------------------------------------

    public CursoId id() { return id; }
    public Nome nome() { return nome; }
    public String nomeTexto() { return nome.valor(); }
    public ParametrosCurso parametros() { return parametros; }
    public Professor coordenador() { return coordenador; }
}