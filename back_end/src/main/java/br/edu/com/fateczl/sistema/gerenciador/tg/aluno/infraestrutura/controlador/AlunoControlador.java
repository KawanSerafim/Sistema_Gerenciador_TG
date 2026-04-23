package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.infraestrutura.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso.BuscarAlunosPorTurmaIdCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso.BuscarAlunosSemGrupoPorTurmasIdsCaso;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("alunos/api")
public class AlunoControlador {
    private final BuscarAlunosPorTurmaIdCaso buscarAlunosPorTurmaIdCaso;
    private final BuscarAlunosSemGrupoPorTurmasIdsCaso buscarAlunosSemGrupoPorTurmasIdsCaso;

    public AlunoControlador(
            BuscarAlunosPorTurmaIdCaso buscarAlunosPorTurmaIdCaso,
            BuscarAlunosSemGrupoPorTurmasIdsCaso buscarAlunosSemGrupoPorTurmasIdsCaso
    ){
        this.buscarAlunosPorTurmaIdCaso = buscarAlunosPorTurmaIdCaso;
        this.buscarAlunosSemGrupoPorTurmasIdsCaso = buscarAlunosSemGrupoPorTurmasIdsCaso;
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
    public ResponseEntity<BuscarAlunosSemGrupoPorTurmasIdsCaso.Resposta>
        buscarAlunosSemGrupoPorTurmaId(@RequestParam("turmasIds") List<String> turmasIds) {
        var comando = new
                BuscarAlunosSemGrupoPorTurmasIdsCaso.Comando(turmasIds);
        //Se tudo deu certo retorna 200 com a lista de DTOs no corpo da requisição
        return ResponseEntity.ok(buscarAlunosSemGrupoPorTurmasIdsCaso.executar(comando));
    }

}
