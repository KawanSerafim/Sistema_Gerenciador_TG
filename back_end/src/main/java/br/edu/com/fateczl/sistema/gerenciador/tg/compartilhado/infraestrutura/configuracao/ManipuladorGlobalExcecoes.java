package br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.infraestrutura.configuracao;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManipuladorGlobalExcecoes {

    //Record para exibir em JSON a mensagem de erro
    public record RespostaErro(String erro) {}

    /**
     * Lida com a exceção de validação na resposta da requisição
     * @param excecao
     * @return (ResponseEntity<RespostaErro>) JSON com a mensagem de erro da validação
     */
    @ExceptionHandler(ValidacaoExcecao.class)
    public ResponseEntity<RespostaErro> lidarComValidacaoException(ValidacaoExcecao excecao) {
        // Empacota a mensagem ("Cargo fornecido não é válido...") em um JSON
        RespostaErro corpoDaResposta = new RespostaErro(excecao.getMessage());

        // Retorna 400 Bad Request
        return ResponseEntity.badRequest().body(corpoDaResposta);
    }
}
