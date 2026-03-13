package br.edu.com.fateczl.sistema.gerenciador.tg.turma.objetosvalor;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.excecoes.ValidacaoExcecao;

import java.time.LocalDate;

public record PeriodoLetivo(Ano ano, Semestre semestre) {
    public PeriodoLetivo {
        if(ano == null || semestre == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "período letivo");
        }

        LocalDate agora = LocalDate.now();
        int anoAtual = agora.getYear();
        int mesAtual = agora.getMonthValue();
        int semestreAtual = (mesAtual <= 6) ? 1 : 2;

        if(ano.valor() == anoAtual && semestre.valor() < semestreAtual) {
            throw new ValidacaoExcecao(CodigoErro.VD_004_DATA_INVALIDA,
                    "período letivo", "não pode ser um semestre passado");
        }
    }
}