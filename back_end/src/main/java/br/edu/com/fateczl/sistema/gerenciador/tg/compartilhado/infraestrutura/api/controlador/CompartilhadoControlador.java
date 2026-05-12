package br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.infraestrutura.api.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.casosdeuso.ListarDisciplinasCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.casosdeuso.ListarTurnosCaso;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/compartilhado")
public class CompartilhadoControlador {
    private final ListarTurnosCaso listarTurnosCaso;
    private final ListarDisciplinasCaso listarDisciplinasCaso;

    public CompartilhadoControlador
            (ListarTurnosCaso listarTurnosCaso,
             ListarDisciplinasCaso listarDisciplinasCaso) {
        this.listarTurnosCaso = listarTurnosCaso;
        this.listarDisciplinasCaso = listarDisciplinasCaso;
    }

    @GetMapping("/turnos")
    public ResponseEntity<ListarTurnosCaso.Resposta> listarTurnos() {
        return ResponseEntity.ok(listarTurnosCaso.executar());
    }

    @GetMapping("/disciplinas")
    public ResponseEntity<ListarDisciplinasCaso.Resposta> listarDisciplinas() {
        return ResponseEntity.ok(listarDisciplinasCaso.executar());
    }
}
