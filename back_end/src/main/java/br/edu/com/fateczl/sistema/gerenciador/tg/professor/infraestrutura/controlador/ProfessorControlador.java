package br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso.ListarProfessoresPorCargoCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso.ListarCargosProfessorCaso;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("professores/api")
//Cross origin super permissivo temporário
@CrossOrigin("*")
public class ProfessorControlador {

    private final ListarCargosProfessorCaso listarCargosProfessorCaso;
    private final ListarProfessoresPorCargoCaso listarProfessoresPorCargoCaso;

    public ProfessorControlador(
            ListarCargosProfessorCaso listarCargosProfessorCaso,
            ListarProfessoresPorCargoCaso listarProfessoresPorCargoCaso){
            this.listarCargosProfessorCaso = listarCargosProfessorCaso;
            this.listarProfessoresPorCargoCaso = listarProfessoresPorCargoCaso;

    }

    @GetMapping("/cargos")
    public ResponseEntity<ListarCargosProfessorCaso.Resposta> listarCargos(){
        ListarCargosProfessorCaso.Resposta resposta = listarCargosProfessorCaso.executar();

        return ResponseEntity.ok(resposta);
    }

    //Get (professores/api/?cargo=nome_do_cargo
    @GetMapping
    public ResponseEntity<ListarProfessoresPorCargoCaso.Resposta> listarProfessoresPorCargos
            (@RequestParam("cargo") String cargo){

        ListarProfessoresPorCargoCaso.Comando comando =
                    new ListarProfessoresPorCargoCaso.Comando(cargo);
            ListarProfessoresPorCargoCaso.Resposta resposta =
                    listarProfessoresPorCargoCaso.executar(comando);

            return ResponseEntity.ok(resposta);
    }
}
