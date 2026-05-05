package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.infraestrutura.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso.BuscarGrupoTgPorTurmasIdsCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso.GerarGrupoTgCaso;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gruposTg")
public class GrupoTgControlador {

    private final BuscarGrupoTgPorTurmasIdsCaso buscarGruposCaso;
    private final GerarGrupoTgCaso gerarGrupoTgCaso;

    public GrupoTgControlador(
            BuscarGrupoTgPorTurmasIdsCaso buscarGruposCaso,
            GerarGrupoTgCaso gerarGrupoTgCaso
    ) {
        this.buscarGruposCaso = buscarGruposCaso;
        this.gerarGrupoTgCaso = gerarGrupoTgCaso;
    }

    /**
     * Rota para buscar grupos tg por lista de turmasId
     * @param turmasIds - Lista de string com os ids de turmas (1 ou mais)
     * @return (ResponseEntity) - 200 com DTO de gruposTG
     */
    @GetMapping()
    public ResponseEntity<BuscarGrupoTgPorTurmasIdsCaso.Resposta> buscarGruposPorTurmas(
                @RequestParam("turmasIds") List<String> turmasIds
    ) {
        var comando = new BuscarGrupoTgPorTurmasIdsCaso.Comando(turmasIds);
        var resposta = buscarGruposCaso.executar(comando);

        return ResponseEntity.ok(resposta);
    }

    /**
     * Rota para criar o grupoTG
     * @param comando recebe idCurso, disciplinas, tema, descricaoTema, tipoTg e lista de alunos integrantes
     * @return 201
     */
    @PostMapping
    public ResponseEntity<Void> gerarGrupo(
            @RequestBody GerarGrupoTgCaso.Comando comando
    ) {

        gerarGrupoTgCaso.executar(comando);

        // Retorna 201 Created com corpo vazio
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
