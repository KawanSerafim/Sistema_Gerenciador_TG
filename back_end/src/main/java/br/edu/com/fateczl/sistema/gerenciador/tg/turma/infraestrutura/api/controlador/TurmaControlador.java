package br.edu.com.fateczl.sistema.gerenciador.tg.turma.infraestrutura.api.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GeradorToken;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso.BuscarGruposOrientadosCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso.BuscarTurmasPorProfessorTgIdCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso.BuscarTurmasProfessorLogadoCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso.GerarTurmaCaso;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turmas")
public class TurmaControlador {

    private final GerarTurmaCaso gerarTurmaCaso;
    private final BuscarTurmasPorProfessorTgIdCaso buscarTurmasPorProfessorTgIdCaso;
    private final BuscarTurmasProfessorLogadoCaso buscarTurmasProfessorLogadoCaso;
    private final GeradorToken geradorToken;
    private final BuscarGruposOrientadosCaso buscarGruposOrientadosCaso;

    public TurmaControlador(
            BuscarTurmasPorProfessorTgIdCaso buscarTurmasPorProfessorTgIdCaso,
            GerarTurmaCaso gerarTurmaCaso,
            BuscarTurmasProfessorLogadoCaso buscarTurmasProfessorLogadoCaso,
            GeradorToken geradorToken, BuscarGruposOrientadosCaso buscarGruposOrientadosCaso){
        this.buscarTurmasPorProfessorTgIdCaso = buscarTurmasPorProfessorTgIdCaso;
        this.gerarTurmaCaso = gerarTurmaCaso;
        this.buscarTurmasProfessorLogadoCaso = buscarTurmasProfessorLogadoCaso;
        this.geradorToken = geradorToken;
        this.buscarGruposOrientadosCaso = buscarGruposOrientadosCaso;
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

    @GetMapping("/grupos-orientados")
    public ResponseEntity<List<BuscarGruposOrientadosCaso.GrupoOrientadoDTO>> listarMeusGrupos(
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer semestre,
            @RequestHeader("Authorization") String headerAutorizacao
    ) {
        String token = headerAutorizacao.replace("Bearer ", "");
        String emailLogado = geradorToken.extrairTopico(token);

        // Monta o comando repassando os parâmetros de filtro
        var comando = new BuscarGruposOrientadosCaso.Comando(emailLogado, ano, semestre);
        var resposta = buscarGruposOrientadosCaso.executar(comando);

        return ResponseEntity.ok(resposta);
    }


    /**
     * Busca as turmas do professor logado pelo email do jwt, apenas professor tg
     * @return List lista de turmas {id, turno, disciplina, ano, semestre}
     */
    @GetMapping("/professortg")
    public ResponseEntity<BuscarTurmasPorProfessorTgIdCaso.Resposta> buscarMinhasTurmas() {
        // Não passamos nenhum ID aqui, o Caso de Uso vai se virar para descobrir quem é!
        var turmas = buscarTurmasProfessorLogadoCaso.executar();
        return ResponseEntity.ok(turmas);
    }


}
