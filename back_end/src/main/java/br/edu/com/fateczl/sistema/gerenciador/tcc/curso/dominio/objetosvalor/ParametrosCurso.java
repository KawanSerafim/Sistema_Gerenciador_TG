package br.edu.com.fateczl.sistema.gerenciador.tcc.curso.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.objetosvalor.Turno;

import java.util.List;

public record ParametrosCurso(List<Turno> turnos,
                              List<Disciplina> disciplinas,
                              List<AjusteTipoTcc> ajustesTipoTcc) {
    public ParametrosCurso {
        validarLista(turnos, "turnos");
        validarLista(disciplinas, "disciplinas");
        validarLista(ajustesTipoTcc, "ajustes de tipos de TCC");

        turnos = List.copyOf(turnos);
        disciplinas = List.copyOf(disciplinas);
        ajustesTipoTcc = List.copyOf(ajustesTipoTcc);
    }

    private void validarLista(List<?> lista, String campo) {
        if(lista == null || lista.isEmpty()) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    campo);
        }
    }

    public boolean validarDisciplina(Disciplina disciplina) {
        return disciplinas.contains(disciplina);
    }

    public boolean validarTurno(Turno turno) {
        return turnos.contains(turno);
    }

    public boolean validarTipoTcc(TipoTcc tipoTcc) {
        if(tipoTcc == null) return false;

        return ajustesTipoTcc.stream().anyMatch(ajuste -> ajuste.tipoTcc()
                .equals(tipoTcc));
    }

    public boolean validarQtdAlunosGrupo(TipoTcc tipoTcc, Integer quantidade) {
        if(quantidade == null || quantidade < 1 || tipoTcc == null) return false;

        return ajustesTipoTcc.stream()
                .filter(ajuste -> ajuste.tipoTcc() == tipoTcc)
                .findFirst()
                .map(ajuste -> ajuste.validarQtdAlunosGrupo(quantidade))
                .orElse(false);
    }
}