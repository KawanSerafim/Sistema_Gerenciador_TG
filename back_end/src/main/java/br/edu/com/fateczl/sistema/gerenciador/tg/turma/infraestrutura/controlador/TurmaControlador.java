package br.edu.com.fateczl.sistema.gerenciador.tg.turma.infraestrutura.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso.BuscarTurmasPorProfessorTgIdCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso.GerarTurmaCaso;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/turmas")
//Cross origin temporário
@CrossOrigin("*")
public class TurmaControlador {

    private final BuscarTurmasPorProfessorTgIdCaso buscarTurmasPorProfessorTgIdCaso;

    private final GerarTurmaCaso gerarTurmaCaso;

    public TurmaControlador(
            BuscarTurmasPorProfessorTgIdCaso buscarTurmasPorProfessorTgIdCaso,
            GerarTurmaCaso gerarTurmaCaso){
        this.buscarTurmasPorProfessorTgIdCaso = buscarTurmasPorProfessorTgIdCaso;
        this.gerarTurmaCaso = gerarTurmaCaso;
    }

    @PostMapping
    public ResponseEntity<GerarTurmaCaso.Resposta> cadastrarTurma(@RequestBody GerarTurmaCaso.Comando comando){
        var resposta = gerarTurmaCaso.executar(comando);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping
    public ResponseEntity<BuscarTurmasPorProfessorTgIdCaso.Resposta> buscarTurmasPorProfessorTGId(
            @RequestParam("professorTgId") String professorTgId
    ){
        var comando = new BuscarTurmasPorProfessorTgIdCaso.Comando(professorTgId);
        var turmas =buscarTurmasPorProfessorTgIdCaso.executar(comando);
        return ResponseEntity.ok(turmas);
    }

}
