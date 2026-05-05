package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.infraestrutura.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GeradorToken;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg;
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
    private final GeradorToken geradorToken;

    public GrupoTgControlador(
            BuscarGrupoTgPorTurmasIdsCaso buscarGruposCaso,
            GerarGrupoTgCaso gerarGrupoTgCaso, GeradorToken geradorToken
    ) {
        this.buscarGruposCaso = buscarGruposCaso;
        this.gerarGrupoTgCaso = gerarGrupoTgCaso;
        this.geradorToken = geradorToken;
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
     * @param requisicao DTO recebe: disciplinas, tema, descricaoTema, tipoTg e lista de alunos integrantes
     * @param headerAutorizacao string do header de auth que contem o token jwt
     * @return 201
     */
    @PostMapping
    public ResponseEntity<Void> gerarGrupo(
            @RequestBody GerarGrupoRequisicao requisicao,
            // Pega o token direto do cabeçalho HTTP
            @RequestHeader("Authorization") String headerAutorizacao
    ) {
        // Tira o prefixo "Bearer " do token
        String token = headerAutorizacao.replace("Bearer ", "");

        // Extrai o ID usando a sua ferramenta
        String idContaUsuarioLogada = geradorToken.extrairId(token);

        var comando = new GerarGrupoTgCaso.Comando(
                idContaUsuarioLogada,
                requisicao.tema(),
                requisicao.descricaoTema(),
                requisicao.tipoTg(),
                requisicao.matriculasAlunos()
        );

        gerarGrupoTgCaso.executar(comando);

        // Retorna 201 Created com corpo vazio
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ========= DTOs =====
    public record GerarGrupoRequisicao(
            String tema,
            String descricaoTema,
            TipoTg tipoTg,
            List<String> matriculasAlunos
    ){}

}
