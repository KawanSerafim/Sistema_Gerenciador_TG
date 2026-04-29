package br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.CargoProfessor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;

import java.util.Arrays;
import java.util.List;

import static br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro.VD_005_PADRAO_INVALIDO;

public class ListarProfessoresPorCargoCaso {

    private final ProfessorRepositorio repositorio;

    public ListarProfessoresPorCargoCaso(ProfessorRepositorio repositorio){
        this.repositorio = repositorio;
    }
    //Recebe a string vinda da requisição
    public record Comando(String cargo){}

    // DTO para a saida
    public record ProfessorResumoDTO(String id, String nome, String matricula){}

    //Resposta empacotada
    public record Resposta(List<ProfessorResumoDTO> professoresDTO) {}


    /**
     * Executa o caso de uso BuscarProfessoresPorCargo
     * @param comando comando - contem a string cargo que vem da requisição
     * @return (Resposta) resposta - contem uma lista de ProfessorResumoDTO, com id e nome
     */
    public Resposta executar(Comando comando) {
        //Valida se o cargo da requisição é valido
        CargoProfessor cargoEnum;
        try{
            cargoEnum = CargoProfessor.valueOf(comando.cargo().toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new ValidacaoExcecao(VD_005_PADRAO_INVALIDO,
                    "cargo",
                    Arrays.toString(CargoProfessor.values()));
        }

        //Busca entidades usando interface
        List<Professor> professors = repositorio.listarPorCargoProfessor(cargoEnum);

        List<ProfessorResumoDTO> dtos = professors.stream()
                .map( professor -> new ProfessorResumoDTO(
                        professor.idTexto(),
                        professor.nomeTexto(),
                        professor.matriculaTexto()
                ))
                .toList();
        return new Resposta(dtos);
    }
}
