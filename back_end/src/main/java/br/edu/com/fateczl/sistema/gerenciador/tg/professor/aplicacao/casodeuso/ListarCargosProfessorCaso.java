package br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.CargoProfessor;

import java.util.List;

public class ListarCargosProfessorCaso {

    public record Resposta (
            List<CargoProfessor> cargos
    ){ }

    /**
     * Excecutar do caso de uso Listar Cargos possiveis de Professor
     * @return (Resposta) - Lista com as opções de cargos vindas do enum
     */
    public Resposta executar() {
        //Pega todos os cargos do enum e retorna em lista imutavel
        return new Resposta(List.of(CargoProfessor.values()));
    }
}
