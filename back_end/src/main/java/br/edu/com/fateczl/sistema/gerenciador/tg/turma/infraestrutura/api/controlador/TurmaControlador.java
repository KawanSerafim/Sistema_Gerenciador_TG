package br.edu.com.fateczl.sistema.gerenciador.tg.turma.infraestrutura.api.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GeradorToken;

import br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso.BuscarTurmasPorProfessorTgIdCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso.BuscarTurmasProfessorLogadoCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso.FinalizarTurmasCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso.GerarTurmaCaso;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/turmas")
public class TurmaControlador {

    private final GerarTurmaCaso gerarTurmaCaso;
    private final BuscarTurmasPorProfessorTgIdCaso buscarTurmasPorProfessorTgIdCaso;
    private final BuscarTurmasProfessorLogadoCaso buscarTurmasProfessorLogadoCaso;
    private final FinalizarTurmasCaso finalizarTurmasCaso;
    private final GeradorToken geradorToken;


    public TurmaControlador(
            BuscarTurmasPorProfessorTgIdCaso buscarTurmasPorProfessorTgIdCaso,
            GerarTurmaCaso gerarTurmaCaso,
            GeradorToken geradorToken,
            BuscarTurmasProfessorLogadoCaso buscarTurmasProfessorLogadoCaso,
            FinalizarTurmasCaso finalizarTurmasCaso){
        this.buscarTurmasPorProfessorTgIdCaso = buscarTurmasPorProfessorTgIdCaso;
        this.gerarTurmaCaso = gerarTurmaCaso;
        this.buscarTurmasProfessorLogadoCaso = buscarTurmasProfessorLogadoCaso;
        this.finalizarTurmasCaso = finalizarTurmasCaso;
        this.geradorToken = geradorToken;
    }

    @PostMapping
    public ResponseEntity<GerarTurmaCaso.Resposta> cadastrarTurma(@RequestBody GerarTurmaCaso.Comando comando){
        var resposta = gerarTurmaCaso.executar(comando);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    /**
     * Busca as turmas do professor pelo professor id
     * @return List lista de turmas {id, turno, disciplina, ano, semestre}
     */
    @GetMapping
    public ResponseEntity<BuscarTurmasPorProfessorTgIdCaso.Resposta> buscarTurmasPorProfessorTGId(
            @RequestParam("professorTgId") String professorTgId
    ){
        var comando = new BuscarTurmasPorProfessorTgIdCaso.Comando(professorTgId);
        var turmas =buscarTurmasPorProfessorTgIdCaso.executar(comando);
        return ResponseEntity.ok(turmas);
    }

    /**
     * Busca as turmas do professor logado pelo email do jwt, apenas professor tg
     * @return List lista de turmas {id, turno, disciplina, ano, semestre}
     */
    @GetMapping("/professor-tg")
    public ResponseEntity<BuscarTurmasPorProfessorTgIdCaso.Resposta> buscarMinhasTurmas() {
        // Não passamos nenhum ID aqui, o Caso de Uso vai se virar para descobrir quem é!
        var turmas = buscarTurmasProfessorLogadoCaso.executar();
        return ResponseEntity.ok(turmas);
    }

    /**
     * Finalizar turmas, alterando campo de statusTurma para FINALIZADA
     * @param comando Email do professor, lista de ids de turmas a serem finalizadas
     * @param headerAutorizacao cabeçalho com jwt do usuario logado
     * @return HTTP 204
     */
    @PatchMapping("/finalizar")
    public ResponseEntity<Void> finalizarTurmas(
            @RequestBody FinalizarTurmasCaso.Comando comando,
            @RequestHeader("Authorization") String headerAutorizacao){

        String token = headerAutorizacao.replace("Bearer ", "");
        String emailProfessor = geradorToken.extrairTopico(token);

        var comandoSeguro = new FinalizarTurmasCaso.Comando(
                emailProfessor,
                comando.turmasIds()
        );

        finalizarTurmasCaso.executar(comandoSeguro);

        // Retorna 204 No Content (Sucesso, mas sem corpo de resposta)
        return ResponseEntity.noContent().build();

    }


}
