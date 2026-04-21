package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.infraestrutura.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso.BuscarAlunosPorTurmaIdCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso.BuscarAlunosSemGrupoPorTurmaIdCaso;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("alunos/api")
public class AlunoControlador {
    private final BuscarAlunosPorTurmaIdCaso buscarAlunosPorTurmaIdCaso;
    private final BuscarAlunosSemGrupoPorTurmaIdCaso buscarAlunosSemGrupoPorTurmaIdCaso;

    public AlunoControlador(
            BuscarAlunosPorTurmaIdCaso buscarAlunosPorTurmaIdCaso,
            BuscarAlunosSemGrupoPorTurmaIdCaso buscarAlunosSemGrupoPorTurmaIdCaso
    ){
        this.buscarAlunosPorTurmaIdCaso = buscarAlunosPorTurmaIdCaso;
        this.buscarAlunosSemGrupoPorTurmaIdCaso = buscarAlunosSemGrupoPorTurmaIdCaso;
    }

    @GetMapping
    public ResponseEntity<BuscarAlunosPorTurmaIdCaso.Resposta>
        buscarAlunosPorTurmaId(@RequestParam("turmaId") String turmaId){
        BuscarAlunosPorTurmaIdCaso.Comando comando = new
                BuscarAlunosPorTurmaIdCaso.Comando(turmaId);
        //Se tudo deu certo retorna 200 com a lista de DTOs no corpo da requisição
        return ResponseEntity.ok(buscarAlunosPorTurmaIdCaso.executar(comando));

    }

    @GetMapping("sem-grupo")
    public ResponseEntity<BuscarAlunosSemGrupoPorTurmaIdCaso.Resposta>
        buscarAlunosSemGrupoPorTurmaId(@RequestParam("turmaId") String turmaId) {
        BuscarAlunosSemGrupoPorTurmaIdCaso.Comando comando = new
                BuscarAlunosSemGrupoPorTurmaIdCaso.Comando(turmaId);
        //Se tudo deu certo retorna 200 com a lista de DTOs no corpo da requisição
        return ResponseEntity.ok(buscarAlunosSemGrupoPorTurmaIdCaso.executar(comando));
    }

}
