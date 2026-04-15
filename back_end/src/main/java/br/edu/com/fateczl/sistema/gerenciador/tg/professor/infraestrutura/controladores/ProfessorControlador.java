package br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.controladores;

import br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso.ListarCargosProfessorCaso;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("professores/api")
//Cross origin super permissivo temporário
@CrossOrigin("*")
public class ProfessorControlador {

    private final ListarCargosProfessorCaso listarCargosProfessorCaso;

    public ProfessorControlador(ListarCargosProfessorCaso listarCargosProfessorCaso){
            this.listarCargosProfessorCaso = listarCargosProfessorCaso;
    }

    @GetMapping("/cargos")
    public ResponseEntity<ListarCargosProfessorCaso.Resposta> listarCargos(){
        ListarCargosProfessorCaso.Resposta resposta = listarCargosProfessorCaso.executar();

        return ResponseEntity.ok(resposta);
    }
}
