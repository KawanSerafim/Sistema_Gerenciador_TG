package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.infraestrutura.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.aplicacao.casosdeuso.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/alunos")
public class AlunoControlador {
    private final BuscarAlunosPorTurmaIdCaso buscarAlunosPorTurmaIdCaso;
    private final BuscarAlunosSemGrupoPorTurmasIdsCaso buscarAlunosSemGrupoPorTurmasIdsCaso;
    private final ImportarAlunosCaso importarAlunosCaso;
    private final BuscarAlunosImportadosCaso buscarAlunosImportadosCaso;
    private final SolicitarAcessoAlunoCaso solicitarAcessoAlunoCaso;

    public AlunoControlador(
            BuscarAlunosPorTurmaIdCaso buscarAlunosPorTurmaIdCaso,
            BuscarAlunosSemGrupoPorTurmasIdsCaso buscarAlunosSemGrupoPorTurmasIdsCaso,
            ImportarAlunosCaso importarAlunosCaso,
            BuscarAlunosImportadosCaso buscarAlunosImportadosCaso,
            SolicitarAcessoAlunoCaso solicitarAcessoAlunoCaso
    ){
        this.buscarAlunosPorTurmaIdCaso = buscarAlunosPorTurmaIdCaso;
        this.buscarAlunosSemGrupoPorTurmasIdsCaso = buscarAlunosSemGrupoPorTurmasIdsCaso;
        this.importarAlunosCaso = importarAlunosCaso;
        this.buscarAlunosImportadosCaso = buscarAlunosImportadosCaso;
        this.solicitarAcessoAlunoCaso = solicitarAcessoAlunoCaso;
    }

    @PostMapping(value = "/importar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportarAlunosCaso.Resposta>
    importarAlunos(
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam("idTurma") String idTurma) {
        try {
            var comando = new ImportarAlunosCaso.Comando(
                    idTurma,
                    arquivo.getInputStream()
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

    @PostMapping
    public ResponseEntity<SolicitarAcessoAlunoCaso.Resposta>
        solicitarAcesso(@RequestBody SolicitarAcessoAlunoCaso.Comando comando) {

        var resposta = solicitarAcessoAlunoCaso.executar(comando);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resposta);
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
