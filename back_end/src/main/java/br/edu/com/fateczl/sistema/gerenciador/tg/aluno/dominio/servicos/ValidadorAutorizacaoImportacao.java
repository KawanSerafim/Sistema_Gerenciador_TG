package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.servicos;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.AutorizacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;

public class ValidadorAutorizacaoImportacao {
    private final CursoRepositorio cursoRepositorio;

    public ValidadorAutorizacaoImportacao(CursoRepositorio cursoRepositorio) {
        this.cursoRepositorio = cursoRepositorio;
    }

    public void validar(Professor autor, Turma turma) {
        if(turma.professorTgId().equals(autor.id())) {
            return;
        }

        Curso curso = cursoRepositorio.buscarPorId(turma.cursoId())
                .orElseThrow(() -> new GenericaExcecao(
                        CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                        "curso associado a turma"
                ));

        if(curso.coordenadorId().equals(autor.id())) {
            return;
        }

        throw new AutorizacaoExcecao(
                CodigoErro.AU_002_ACAO_NAO_PERMITIDA,
                "importar alunos para esta turma"
        );
    }
}