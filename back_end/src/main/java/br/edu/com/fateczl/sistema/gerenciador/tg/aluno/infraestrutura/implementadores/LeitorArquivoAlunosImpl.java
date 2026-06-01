package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.infraestrutura.implementadores;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.portas.LeitorArquivoAlunos;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Component
public class LeitorArquivoAlunosImpl implements LeitorArquivoAlunos {
    private record CabecalhoExtraido(int ano, int semestre, Turno turno) {}

    @Override
    public DadosArquivo ler(InputStream arquivoBruto) {
        try (Workbook workbook = WorkbookFactory.create(arquivoBruto)) {
            final Sheet planilha = workbook.getSheetAt(0);

            if(planilha.getPhysicalNumberOfRows() < 3) {
                throw new ValidacaoExcecao(
                        CodigoErro.VD_008_ARQUIVO_INVALIDO,
                        "a folha de cálculo não tem dados suficientes para "
                        + "leitura"
                );
            }

            final CabecalhoExtraido cabecalho = extrairCabecalho(
                    planilha.getRow(0)
            );

            final List<DadosAluno> alunos = StreamSupport.stream(
                    planilha.spliterator(),
                    false
            )
                    .skip(2)
                    .map(this::extrairAlunoDaLinha)
                    .flatMap(Optional::stream)
                    .toList();

            if(alunos.isEmpty()) {
                throw new ValidacaoExcecao(
                        CodigoErro.VD_008_ARQUIVO_INVALIDO,
                        "Nenhum registro de aluno válido foi encontrado"
                );
            }

            return new DadosArquivo(
                    cabecalho.ano(),
                    cabecalho.semestre(),
                    cabecalho.turno(),
                    alunos
            );
        } catch(ValidacaoExcecao vd) {
            throw vd;
        } catch(Exception e) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_008_ARQUIVO_INVALIDO,
                    "deu falha crítica ao processar o Excel"
            );
        }
    }

    private CabecalhoExtraido extrairCabecalho(final Row linhaCabecalho) {
        if(linhaCabecalho == null) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_008_ARQUIVO_INVALIDO,
                    "o cabeçalho está inexistente"
            );
        }

        try {
            final String celulaAnoSemestre = pegarValorStringDaCelula(
                    linhaCabecalho.getCell(0)
            );
            final int anoSemestreInt = Integer.parseInt(
                    celulaAnoSemestre
            );

            final int ano = anoSemestreInt / 10;
            final int semestre = anoSemestreInt % 10;

            final Turno turno = pegarTurno(linhaCabecalho.getCell(2));

            return new CabecalhoExtraido(ano, semestre, turno);
        } catch(NumberFormatException e) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_008_ARQUIVO_INVALIDO,
                    "o formato de ano/semestre no cabeçalho é inválido. É "
                    + "esperado formato 'YYYYS'"
            );
        }
    }

    private Optional<DadosAluno> extrairAlunoDaLinha(final Row linha) {
        if(linha == null) {
            return Optional.empty();
        }

        final String matricula = pegarValorStringDaCelula(linha.getCell(0));
        final String nome = pegarValorStringDaCelula(linha.getCell(1));

        if(nome.isBlank() || matricula.isBlank()) {
            return Optional.empty();
        }

        return Optional.of(new DadosAluno(nome, matricula));
    }

    private String pegarValorStringDaCelula(final Cell celula) {
        if(celula == null) return "";

        return switch(celula.getCellType()) {
            case NUMERIC -> new DataFormatter().formatCellValue(celula);
            case STRING -> celula.getStringCellValue().trim();
            case BLANK -> "";
            default -> celula.toString().trim();
        };
    }

    private Turno pegarTurno(final Cell celulaTurno) {
        if(celulaTurno == null) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_008_ARQUIVO_INVALIDO,
                    "o campo de turno está em falta"
            );
        }

        final String celulaTurnoString = pegarValorStringDaCelula(celulaTurno)
                .toUpperCase();

        if(celulaTurnoString.contains("MANHÃ")) {
            return Turno.MANHA;
        }
        if(celulaTurnoString.contains("TARDE")) {
            return Turno.TARDE;
        }
        if(celulaTurnoString.contains("NOITE")) {
            return Turno.NOITE;
        }

        throw new ValidacaoExcecao(
                CodigoErro.VD_007_CAMPO_NAO_SUPORTADO,
                "turno", "texto não reconhecido"
        );
    }
}