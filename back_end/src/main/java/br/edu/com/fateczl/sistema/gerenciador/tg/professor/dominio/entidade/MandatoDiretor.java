package br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.MandatoDiretorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;

import java.time.LocalDate;

public class MandatoDiretor {
    private final MandatoDiretorId id;
    private final ProfessorId professorId;
    private final LocalDate dataInicio;
    //Data de fim pode ser nula ou mesmo atualizada
    private LocalDate dataFim;
    //salvando os bytes reais da imagem com a assinatura do diretor convertidos em texto puro
    private final String assinaturaBase64;

    private MandatoDiretor(
            MandatoDiretorId mandatoDiretorId,
            ProfessorId professorId,
            LocalDate dataInicio,
            LocalDate dataFim,
            String assinaturaBase64) {
        this.id = assegurarPresenca(mandatoDiretorId, "ID");
        this.professorId = assegurarPresenca(professorId, "professor ID");
        this.dataInicio = assegurarPresenca(dataInicio, "data de inicio do mandato");
        //Data fim pode ser nula, caso não tenha previsão exata
        this.dataFim = dataFim;
        this.assinaturaBase64 = assegurarPresenca(assinaturaBase64,
                "imagem da assinatura do diretor em Base64");
    }

    // MÉTODOS FACTORY ---------------------------------------------------------

    public static MandatoDiretor novo(
            MandatoDiretorId id,
            ProfessorId professorId,
            LocalDate dataInicio,
            LocalDate dataFim,
            String assinaturaBase64
    ){
        return new MandatoDiretor(
                id,
                professorId,
                dataInicio,
                dataFim,
                assinaturaBase64);
    }

    public static MandatoDiretor carregar(
            MandatoDiretorId id,
            ProfessorId professorId,
            LocalDate dataInicio,
            LocalDate dataFim,
            String assinaturaBase64
    ){
        return new MandatoDiretor(
                id,
                professorId,
                dataInicio,
                dataFim,
                assinaturaBase64);
    }

    // MÉTODOS PARA GARANTIR PRESENÇA ------------------------------------------

    private <T> T assegurarPresenca(T objeto, String campo) {
        if(objeto == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    campo);
        }
        return objeto;
    }

    // MÉTODOS DE VERIFICAÇÃO --------------------------------------------------
    public boolean estaVigente() {
        LocalDate hoje = LocalDate.now();
        return (hoje.isEqual(dataInicio) || hoje.isAfter(dataInicio)) &&
                (dataFim == null || hoje.isBefore(dataFim) || hoje.isEqual(dataFim));
    }
    // MÉTODOS DE ATUALIZAÇÃO --------------------------------------------------

    //O comportamento do domínio para antecipar o fim do mandato
    public void encerrar() {
        // Define o fim do mandato para ontem.
        // Assim, a validação 'dataFim >= hoje' dará FALSO instantaneamente.
        this.dataFim = LocalDate.now().minusDays(1);
    }

    // MÉTODOS GETTERS DE DELEGAÇÃO --------------------------------------------

    public String idTexto() { return id.texto(); }
    public String professorIdTexto(){return professorId.texto();}
    public String dataInicioTexto() {return dataInicio.toString();}
    public String dataFimTexto() {return dataFim != null ? dataFim.toString() : null;}


    // MÉTODOS GETTERS ---------------------------------------------------------

    public MandatoDiretorId id() { return id; }
    public ProfessorId professorId() { return professorId; }
    public String assinaturaBase64() { return assinaturaBase64; }
    public LocalDate dataInicio(){ return dataInicio;}
    public LocalDate dataFim(){ return dataFim;}
}
