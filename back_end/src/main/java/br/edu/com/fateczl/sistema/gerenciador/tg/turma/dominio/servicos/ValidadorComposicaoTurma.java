package br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.servicos;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;

public class ValidadorComposicaoTurma {
    public void validar(
            Curso curso,
            Professor professorTg,
            Disciplina disciplina,
            Turno turno
    ) {
        if(!professorTg.podeSerProfessorTg()) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "professor", "professor de TG"
            );
        }

        if(!curso.validarDisciplina(disciplina)) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_007_CAMPO_NAO_SUPORTADO,
                    "disciplina", "o curso associado não a possui"
            );
        }

        if(!curso.validarTurno(turno)) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_007_CAMPO_NAO_SUPORTADO,
                    "turno", "o curso associado não a possui"
            );
        }
    }
}