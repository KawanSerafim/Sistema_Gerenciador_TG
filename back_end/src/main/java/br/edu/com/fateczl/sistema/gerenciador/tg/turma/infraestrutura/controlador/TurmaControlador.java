package br.edu.com.fateczl.sistema.gerenciador.tg.turma.infraestrutura.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso.BuscarTurmasPorProfessorTgIdCaso;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/turmas/api")
//Cross origin temporário
@CrossOrigin("*")
public class TurmaControlador {

    private final BuscarTurmasPorProfessorTgIdCaso buscarTurmasPorProfessorTgIdCaso;

    public TurmaControlador(BuscarTurmasPorProfessorTgIdCaso buscarTurmasPorProfessorTgIdCaso){
        this.buscarTurmasPorProfessorTgIdCaso = buscarTurmasPorProfessorTgIdCaso;
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
