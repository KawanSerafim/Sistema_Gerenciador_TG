package br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.servicos;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;

public class ValidadorCoordenadorCurso {
    public void validar(Professor professor) {
        if(!professor.podeSerCoordenadorCurso()) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "professor", "coordenador de curso"
            );
        }
    }
}