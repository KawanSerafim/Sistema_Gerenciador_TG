package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.infraestrutura.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso.BuscarAlunosImportadosCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso.BuscarAlunosPorTurmaIdCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso.BuscarAlunosSemGrupoPorTurmasIdsCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso.ImportarAlunosCaso;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("alunos/api")
public class AlunoControlador {
    private final BuscarAlunosPorTurmaIdCaso buscarAlunosPorTurmaIdCaso;
    private final BuscarAlunosSemGrupoPorTurmasIdsCaso buscarAlunosSemGrupoPorTurmasIdsCaso;
    private final ImportarAlunosCaso importarAlunosCaso;
    private final BuscarAlunosImportadosCaso buscarAlunosImportadosCaso;
    public AlunoControlador(
            BuscarAlunosPorTurmaIdCaso buscarAlunosPorTurmaIdCaso,
            BuscarAlunosSemGrupoPorTurmasIdsCaso buscarAlunosSemGrupoPorTurmasIdsCaso,
            ImportarAlunosCaso importarAlunosCaso,
            BuscarAlunosImportadosCaso buscarAlunosImportadosCaso
    ){
        this.buscarAlunosPorTurmaIdCaso = buscarAlunosPorTurmaIdCaso;
        this.buscarAlunosSemGrupoPorTurmasIdsCaso = buscarAlunosSemGrupoPorTurmasIdsCaso;
        this.importarAlunosCaso = importarAlunosCaso;
        this.buscarAlunosImportadosCaso = buscarAlunosImportadosCaso;
    }

    @PostMapping(value = "/importar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportarAlunosCaso.Resposta>
    importarAlunos(
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam("idTurma") String idTurma,
            @RequestParam("emailAutor") String emailAutor) {
        try {
            var comando = new ImportarAlunosCaso.Comando(
                    idTurma,
                    arquivo.getInputStream(),
                    emailAutor
            );
            var resposta = importarAlunosCaso.executar(comando);
            return ResponseEntity.ok().body(resposta);
        } catch (IOException e) {
            //Se arquivo corrompido
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("/importar")
    public ResponseEntity<BuscarAlunosImportadosCaso.Resposta>
        buscarAlunosImportados(@RequestParam("turmaId") String turmaId) {

        var comando = new BuscarAlunosImportadosCaso.Comando(turmaId);
        return ResponseEntity.ok()
                .body(buscarAlunosImportadosCaso.executar(comando));
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
