package br.edu.com.fateczl.sistema.gerenciador.tg.curso.aplicacao.casosdeuso;


import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Pagina;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ListarCursosCaso {
    private final CursoRepositorio repositorio;
    private final ProfessorRepositorio professorRepositorio;

    public ListarCursosCaso(
            CursoRepositorio repositorio,
            ProfessorRepositorio professorRepositorio) {
        this.repositorio = repositorio;
        this.professorRepositorio = professorRepositorio;
    }

    //A requisição
    public record Comando(Integer pagina, Integer tamanho){}

    //Record usado para manter a relação entre tipoTG e qnt maxima de alunos
    public record AjusteDTO(String tipoTg, Integer maxAlunosGrupo){}

    //Representação do curso na resposta
    public record CursosDTO(
            String id,
            String nome,
            String nomeCoordenador,
            List<String> turnos,
            List<AjusteDTO> ajustes,
            List<String> disciplinas){}

    public record Resposta(
            List<CursosDTO> cursos,
            Integer paginaAtual,
            Integer totalPaginas,
            Long totalElementos
        )
    {}

    public Resposta executar(Comando comando){
        Pagina<Curso> paginas = repositorio.buscarTodos(comando.pagina(), comando.tamanho());
        List<CursosDTO> dtos = paginas.conteudo().stream()
                .map(curso -> {
                    // Transformamos a lista do Domínio na lista de DTOs
                    List<AjusteDTO> ajustesDto = curso.parametros().ajustesTipoTg().stream()
                            .map(ajuste -> new AjusteDTO(
                                    ajuste.tipoTg().name(),
                                    ajuste.maxAlunosGrupo()
                            ))
                            .toList();

                    String nomeCoordenador = buscaNomeCoordenador(curso.coordenadorIdTexto());

                   return new CursosDTO(
                            curso.idTexto(),
                            curso.nomeTexto(),
                            nomeCoordenador,
                            curso.parametros().turnos().stream().map(Enum::name).toList(),
                            ajustesDto,
                            curso.parametros().disciplinas().stream().map(Enum::name).toList()
                            );
                }).toList();
        return new Resposta(
                dtos,
                paginas.paginaAtual(),
                paginas.totalPaginas(),
                paginas.totalElementos()
        );

    }

    // ----------- Funções auxiliares -----------//
    public String buscaNomeCoordenador(String coordenadorId){
        Optional<Professor> coordenador = professorRepositorio.buscarPorId(
                new ProfessorId(UUID.fromString(coordenadorId))
        );
        return coordenador.map(Professor::nomeTexto)
                    .orElse("Sem coordenador");
    }

}
