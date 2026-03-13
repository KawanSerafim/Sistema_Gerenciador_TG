package br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.objetosvalor.Turno;

import java.util.List;

public record ParametrosCurso(List<Turno> turnos,
                              List<Disciplina> disciplinas,
                              List<AjusteTipoTg> ajustesTipoTg) {
    public ParametrosCurso {
        validarLista(turnos, "turnos");
        validarLista(disciplinas, "disciplinas");
        validarLista(ajustesTipoTg, "ajustes de tipos de TG");

        turnos = List.copyOf(turnos);
        disciplinas = List.copyOf(disciplinas);
        ajustesTipoTg = List.copyOf(ajustesTipoTg);
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

    public boolean validarTipoTg(TipoTg tipoTg) {
        if(tipoTg == null) return false;

        return ajustesTipoTg.stream().anyMatch(ajuste -> ajuste.tipoTg()
                .equals(tipoTg));
    }

    public boolean validarQtdAlunosGrupo(TipoTg tipoTg, Integer quantidade) {
        if(quantidade == null || quantidade < 1 || tipoTg == null) return false;

        return ajustesTipoTg.stream()
                .filter(ajuste -> ajuste.tipoTg() == tipoTg)
                .findFirst()
                .map(ajuste -> ajuste.validarQtdAlunosGrupo(quantidade))
                .orElse(false);
    }
}