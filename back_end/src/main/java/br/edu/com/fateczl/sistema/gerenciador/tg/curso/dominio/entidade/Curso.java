package br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.objetosvalor.Turno;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.ParametrosCurso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;

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