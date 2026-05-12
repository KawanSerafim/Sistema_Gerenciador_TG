package br.edu.com.fateczl.sistema.gerenciador.tg.banca.infraestrutura.api.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.casosdeuso.MarcarBancaCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GeradorToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/bancas")
public class BancaControlador {

    private final MarcarBancaCaso marcarBancaCaso;
    private final GeradorToken geradorToken;

    public BancaControlador(MarcarBancaCaso marcarBancaCaso, GeradorToken geradorToken) {
        this.marcarBancaCaso = marcarBancaCaso;
        this.geradorToken = geradorToken;
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
}
