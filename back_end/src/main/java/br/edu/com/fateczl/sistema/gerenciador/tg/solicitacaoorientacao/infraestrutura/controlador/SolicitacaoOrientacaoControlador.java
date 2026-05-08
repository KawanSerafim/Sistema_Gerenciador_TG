package br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.infraestrutura.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GeradorToken;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.aplicacao.casosdeuso.SolicitarOrientacaoCaso;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/solicitacoes-orientacao")
public class SolicitacaoOrientacaoControlador {
    private final SolicitarOrientacaoCaso solicitarOrientacaoCaso;
    private final GeradorToken geradorToken;

    public SolicitacaoOrientacaoControlador(
            SolicitarOrientacaoCaso solicitarOrientacaoCaso,
            GeradorToken geradorToken
    ) {
        this.solicitarOrientacaoCaso = solicitarOrientacaoCaso;
        this.geradorToken = geradorToken;
    }

    /**
     * Rota para o aluno (grupo) solicitar um orientador
     * @param requisicao DTO que recebe o ID do professor escolhido
     * @param headerAutorizacao string do header de auth que contem o token jwt
     * @return 201 Created
     */
    @PostMapping
    public ResponseEntity<Void> solicitarOrientacao(
            @RequestBody SolicitarOrientacaoRequisicao requisicao,
            @RequestHeader("Authorization") String headerAutorizacao
    ) {
        // Tira o prefixo "Bearer " do token
        String token = headerAutorizacao.replace("Bearer ", "");

        // Extrai o ID da conta do aluno logado usando a ferramenta de JWT
        String idContaUsuarioLogada = geradorToken.extrairId(token);

        // Monta o comando exigido pelo Caso de Uso
        var comando = new SolicitarOrientacaoCaso.Comando(
                idContaUsuarioLogada,
                requisicao.idProfessor()
        );

        // Executa a orquestração e validações de negócio
        solicitarOrientacaoCaso.executar(comando);

        // Retorna 201 Created com corpo vazio indicando sucesso na criação
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ========= DTOs =====
    public record SolicitarOrientacaoRequisicao(
            String idProfessor
    ) {}
}
