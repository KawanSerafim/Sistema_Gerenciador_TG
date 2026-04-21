package br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.infraestrutura.rede.excecoes;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;

@ControllerAdvice
public class ManipuladorGlobalExcecao {
    private static final Logger log = LoggerFactory
            .getLogger(ManipuladorGlobalExcecao.class);

    public record ErroRespostaCliente(String codigo, String mensagem) {}
    public record ErroRespostaDominio(
            String codigo,
            String mensagem,
            OffsetDateTime timeStamp
    ) {}

    @ExceptionHandler(DominioExcecao.class)
    public ResponseEntity<ErroRespostaDominio> tratarDominioExcecao(
            DominioExcecao ex
    ) {
        HttpStatus status = determinarStatus(ex);

        var corpo = new ErroRespostaDominio(
                ex.getCodigoErro().name(),
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(status).body(corpo);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroRespostaCliente> tratarJsonMalformadoExcecao(
            HttpMessageNotReadableException ex
    ) {
        log.error(ex.getMessage());

        var erroResposta = new ErroRespostaCliente(
                CodigoErro.CLIENTE_001_JSON_MALFORMADO.name(),
                "JSON malformado!"
        );

        return ResponseEntity.badRequest().body(erroResposta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroRespostaCliente> tratarExcecaoGenerica(
            Exception ex
    ) {
        log.error(ex.getMessage());

        var erroResposta = new ErroRespostaCliente(
                CodigoErro.CLIENTE_002_ERRO_INESPERADO.name(),
                "Ocorreu um erro no servidor!"
        );

        return ResponseEntity.badRequest().body(erroResposta);
    }

    private HttpStatus determinarStatus(DominioExcecao ex) {
        return switch (ex) {
            case ValidacaoExcecao v -> HttpStatus.BAD_REQUEST;
            case AutorizacaoExcecao a -> HttpStatus.FORBIDDEN;
            case RegraNegocioExcecao r -> HttpStatus.UNPROCESSABLE_ENTITY;
            case GenericaExcecao g -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}