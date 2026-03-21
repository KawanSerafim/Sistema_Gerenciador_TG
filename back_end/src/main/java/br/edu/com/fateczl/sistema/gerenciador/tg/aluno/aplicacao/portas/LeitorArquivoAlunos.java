package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.portas;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;

import java.io.InputStream;
import java.util.List;

public interface LeitorArquivoAlunos {
    record DadosAluno(String nome, String matricula) {}

    record DadosArquivo(
            Integer ano,
            Integer semestre,
            Turno turno,
            List<DadosAluno> alunos
    ) {}

    DadosArquivo ler(InputStream arquivoBruto);
}