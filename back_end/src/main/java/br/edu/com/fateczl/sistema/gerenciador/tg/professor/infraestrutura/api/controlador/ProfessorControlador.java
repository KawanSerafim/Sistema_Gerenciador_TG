package br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.api.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso.CadastrarProfessorCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso.ListarProfessoresPorCargoCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso.ListarCargosProfessorCaso;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/professores")
//Cross origin super permissivo temporário
@CrossOrigin("*")
public class ProfessorControlador {

    private final ListarCargosProfessorCaso listarCargosProfessorCaso;
    private final ListarProfessoresPorCargoCaso listarProfessoresPorCargoCaso;
    private final CadastrarProfessorCaso cadastrarProfessorCaso;

    public ProfessorControlador(
            ListarCargosProfessorCaso listarCargosProfessorCaso,
            ListarProfessoresPorCargoCaso listarProfessoresPorCargoCaso,
            CadastrarProfessorCaso cadastrarProfessorCaso
    ){
            this.listarCargosProfessorCaso = listarCargosProfessorCaso;
            this.listarProfessoresPorCargoCaso = listarProfessoresPorCargoCaso;
            this.cadastrarProfessorCaso = cadastrarProfessorCaso;

    }

    @PostMapping
    public ResponseEntity<CadastrarProfessorCaso.Resposta> cadastrarProfessor(
            @RequestBody CadastrarProfessorCaso.Comando comando
    ) {
        // Aciona o ouvinte para enviar o email ao professor
        var resposta = cadastrarProfessorCaso.executar(comando);
        // Retorna 201
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
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
