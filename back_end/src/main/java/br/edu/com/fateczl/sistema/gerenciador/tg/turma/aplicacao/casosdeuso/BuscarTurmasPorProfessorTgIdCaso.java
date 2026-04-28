package br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;

import java.util.List;
import java.util.UUID;

import static br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO;

public class BuscarTurmasPorProfessorTgIdCaso {
    private final TurmaRepositorio repositorio;

    public BuscarTurmasPorProfessorTgIdCaso(TurmaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    //Id que vem da requisição
    public record Comando(String professorId){}

    //DTO para a resposta
    public record TurmaResumoDTO(
            String id,
            String turno,
            String disciplina,
            Integer ano,
            Integer semestre
    ){}

    public record Resposta(List<TurmaResumoDTO> turmas){}

    /**
     * Executar do caso de uso Buscar turmas do professorTgId informado
     * @param comando professorTgId em string
     * @return (Resposta) Lista de DTOs(turno,disciplina,ano,semestre)
     */
    public Resposta executar(Comando comando) {
        ProfessorId professorId;
        try{
            //Cria UUID da string vinda da requisição
            professorId = new ProfessorId(UUID.fromString(comando.professorId()));
        }catch (IllegalArgumentException e){
            throw new ValidacaoExcecao(GN_001_REGISTRO_NAO_ENCONTRADO,"professorId");
        }
            //Busca lista de turmas com id professor
            List<Turma> turmas = this.repositorio.buscarPorProfessorTgId(professorId);
            //Cria DTO para resposta
            List<TurmaResumoDTO> turmasDTO = turmas.stream()
                    .map(turma -> new TurmaResumoDTO(
                            turma.id().texto(),
                            turma.turno().toString(),
                            turma.disciplina().toString(),
                            turma.anoLetivoValor(),
                            turma.semestreLetivoValor()))
                    .toList();
            return new Resposta(turmasDTO);



    }

}
