package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.infraestrutura.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso.BuscarGrupoTgPorTurmasIdsCaso;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/gruposTg/api")
public class GrupoTgControlador {

    private final BuscarGrupoTgPorTurmasIdsCaso buscarGruposCaso;

    public GrupoTgControlador(BuscarGrupoTgPorTurmasIdsCaso buscarGruposCaso) {
        this.buscarGruposCaso = buscarGruposCaso;
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
}
