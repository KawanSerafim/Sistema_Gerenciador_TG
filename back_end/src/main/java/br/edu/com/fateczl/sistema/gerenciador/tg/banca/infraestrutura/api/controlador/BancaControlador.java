package br.edu.com.fateczl.sistema.gerenciador.tg.banca.infraestrutura.api.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.casosdeuso.AtribuirNotasBancaCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.casosdeuso.CancelarAvaliacaoCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.casosdeuso.ListarBancasOrientadorCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.casosdeuso.MarcarBancaCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GeradorToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bancas")
public class BancaControlador {

    private final MarcarBancaCaso marcarBancaCaso;
    private final GeradorToken geradorToken;
    private final ListarBancasOrientadorCaso listarBancasOrientadorCaso;
    private final AtribuirNotasBancaCaso atribuirNotasBancaCaso;
    private final CancelarAvaliacaoCaso cancelarAvaliacaoCaso;

    public BancaControlador(
            MarcarBancaCaso marcarBancaCaso,
            GeradorToken geradorToken,
            ListarBancasOrientadorCaso listarBancasOrientadorCaso,
            AtribuirNotasBancaCaso atribuirNotasBancaCaso,
            CancelarAvaliacaoCaso cancelarAvaliacaoCaso) {
        this.marcarBancaCaso = marcarBancaCaso;
        this.geradorToken = geradorToken;
        this.listarBancasOrientadorCaso = listarBancasOrientadorCaso;
        this.atribuirNotasBancaCaso = atribuirNotasBancaCaso;
        this.cancelarAvaliacaoCaso = cancelarAvaliacaoCaso;
    }

    /**
     * Realiza a marcação da banca do grupoTg
     * @param requisicao
     * @param headerAutorizacao
     * @return HTTP201
     */
    @PostMapping
    public ResponseEntity<Void> marcarBanca(
            @RequestBody MarcarBancaRequisicao requisicao,
            @RequestHeader("Authorization") String headerAutorizacao
    ) {
        // Extrai quem é o professor logado através do token
        String token = headerAutorizacao.replace("Bearer ", "");
        String emailContaOrientador = geradorToken.extrairTopico(token);

        // Monta o Comando para o Caso de Uso
        var comando = new MarcarBancaCaso.Comando(
                emailContaOrientador,
                requisicao.idGrupo(),
                requisicao.data(),
                requisicao.hora(),
                requisicao.local(),
                requisicao.idsProfessoresConvidados(),
                requisicao.convidadosExternos()
        );

        // Executa a regra de negócio
        marcarBancaCaso.executar(comando);

        // Retorna 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ========================================================================
    // ROTA 2: LISTAR BANCAS DO ORIENTADOR (Para a Tabela)
    // ========================================================================

    /**
     * Lista as bancas do orientador logado
     * @param headerAutorizacao cabeçalho de autorização com o token
     * @return HTTP 200 Lista de bancas
     */
    @GetMapping
    public ResponseEntity<List<ListarBancasOrientadorCaso.BancaVisaoDto>> listarBancasDoOrientador(
            @RequestHeader("Authorization") String headerAutorizacao
    ) {
        String emailOrientador = extrairEmailDoToken(headerAutorizacao);

        var comando = new ListarBancasOrientadorCaso.Comando(emailOrientador);

        List<ListarBancasOrientadorCaso.BancaVisaoDto> resposta = listarBancasOrientadorCaso.executar(comando);

        // Retorna 200 OK com o JSON da lista (o Spring Boot converte a lista automaticamente)
        return ResponseEntity.ok(resposta);
    }

    // ========================================================================
    // ROTA 3: ATRIBUIR NOTAS (Modal do Frontend)
    // ========================================================================

    /**
     * Atribui as notas do grupoTg naquela banca
     * @param idBanca Id da banca
     * @param requisicao DTO com notas dos membros
     * @param headerAutorizacao cabeçalho de autorização com o token
     * @return HTTP 204
     */
    @PutMapping("/{idBanca}/notas")
    public ResponseEntity<Void> atribuirNotas(
            @PathVariable String idBanca,
            @RequestBody AtribuirNotasRequisicao requisicao,
            @RequestHeader("Authorization") String headerAutorizacao
    ) {
        String emailOrientador = extrairEmailDoToken(headerAutorizacao);

        var comando = new AtribuirNotasBancaCaso.Comando(
                emailOrientador,
                idBanca,
                requisicao.notasMembros()
        );

        atribuirNotasBancaCaso.executar(comando);

        // Retorna 204 No Content (padrão REST para atualizações bem-sucedidas sem retorno de corpo)
        return ResponseEntity.noContent().build();
    }

    /**
     * Cancela a avaliação da banca, apenas se tiver status 'MARCADA'
     * @param idBanca Id da banca
     * @param headerAutorizacao cabeçalho com o jwt
     * @return
     */
    @PutMapping("/{idBanca}/cancelar")
    public ResponseEntity<Void> cancelarAvaliação(
            @PathVariable String idBanca,
            @RequestHeader("Authorization") String headerAutorizacao
    ) {
        String emailOrientador = extrairEmailDoToken(headerAutorizacao);

        var comando = new CancelarAvaliacaoCaso.Comando(
                emailOrientador,
                idBanca
        );

        cancelarAvaliacaoCaso.executar(comando);

        // Retorna 204 No Content
        return ResponseEntity.noContent().build();
    }

    // ========================================================================
    // MÉTODO AUXILIAR
    // ========================================================================
    private String extrairEmailDoToken(String headerAutorizacao) {
        String token = headerAutorizacao.replace("Bearer ", "");
        return geradorToken.extrairTopico(token);
    }

    // ========================================================================
    // DTO DE REQUISIÇÃO (Mapeia exatamente o JSON que o frontend vai enviar)
    // ========================================================================
    public record MarcarBancaRequisicao(
            String idGrupo,
            LocalDate data,      // O Spring Boot formata automaticamente YYYY-MM-DD
            LocalTime hora,      // O Spring Boot formata automaticamente HH:mm
            String local,
            List<String> idsProfessoresConvidados,
            List<MarcarBancaCaso.MembroExternoDto> convidadosExternos
    ) {}
    public record AtribuirNotasRequisicao(
            Map<String, Double> notasMembros
    ) {}
}
