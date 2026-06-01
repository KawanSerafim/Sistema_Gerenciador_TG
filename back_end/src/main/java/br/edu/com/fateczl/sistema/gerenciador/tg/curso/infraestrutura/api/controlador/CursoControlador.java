package br.edu.com.fateczl.sistema.gerenciador.tg.curso.infraestrutura.api.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.curso.aplicacao.casosdeuso.GerarCursoCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.aplicacao.casosdeuso.ListarCursosCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.aplicacao.casosdeuso.ListarTiposTgCaso;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cursos")
public class CursoControlador {

    private final GerarCursoCaso gerarCursoCaso;
    private final ListarCursosCaso listarCursosCaso;
    private final ListarTiposTgCaso listarTiposTgCaso;

    public CursoControlador(
            GerarCursoCaso gerarCursoCaso,
            ListarCursosCaso listarCursosCaso,
            ListarTiposTgCaso listarTiposTgCaso
    ) {
        this.gerarCursoCaso = gerarCursoCaso;
        this.listarCursosCaso = listarCursosCaso;
        this.listarTiposTgCaso = listarTiposTgCaso;
    }

    @PostMapping
    public ResponseEntity<GerarCursoCaso.Resposta> cadastrarCurso
            (@RequestBody GerarCursoCaso.Comando comando){
        var resposta = gerarCursoCaso.executar(comando);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    /**
     * Get de todos os cursos com paginação, caso não seja informado a pagina sera a primeira e o tamanho dela será 10
     * @param pagina Integer da pagina desejada
     * @param tamanho Integer do tamanho da página
     * @return Resposta lista de cursos paginada
     */
    @GetMapping
    public ResponseEntity<ListarCursosCaso.Resposta> buscarTodos(
            @RequestParam(value = "pagina", defaultValue = "0") int pagina,
            @RequestParam(value = "tamanho", defaultValue = "10") int tamanho
    ) {
        var comando = new ListarCursosCaso.Comando(pagina, tamanho);
        var resposta = listarCursosCaso.executar(comando);

        return ResponseEntity.ok(resposta);
    }

    /**
     * Get para listar todas as opções de tiposTg
     * @return List lista string com tipos
     */
    @GetMapping("/tipos-tg")
    public ResponseEntity<ListarTiposTgCaso.Resposta> listarTiposTg() {
        return ResponseEntity.ok(listarTiposTgCaso.executar());
    }

}
