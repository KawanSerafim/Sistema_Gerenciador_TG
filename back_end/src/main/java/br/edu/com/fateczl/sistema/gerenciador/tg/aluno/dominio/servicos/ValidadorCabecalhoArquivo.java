package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.servicos;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.portas.LeitorArquivoAlunos;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;

public class ValidadorCabecalhoArquivo {
    public void validar(
            LeitorArquivoAlunos.DadosArquivo arquivo,
            Turma turma
    ) {
        if(!turma.anoLetivoValor().equals(arquivo.ano())
                || !turma.semestreLetivoValor().equals(arquivo.semestre())
                || !turma.turno().equals(arquivo.turno())
        ) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_007_CAMPO_NAO_SUPORTADO,
                    "cabeçalho do arquivo",
                    "os dados do ano, semestre ou turno não correspondem à "
                    + "turma selecionada"
            );
        }
    }
}