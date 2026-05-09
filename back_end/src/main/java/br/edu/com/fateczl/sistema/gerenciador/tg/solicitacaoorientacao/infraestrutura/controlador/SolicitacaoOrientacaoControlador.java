package br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.infraestrutura.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GeradorToken;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.aplicacao.casosdeuso.BuscarSolicitacoesPendentesProfessorCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.aplicacao.casosdeuso.ResponderSolicitacaoOrientacaoCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.aplicacao.casosdeuso.SolicitarOrientacaoCaso;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitacoes-orientacao")
public class SolicitacaoOrientacaoControlador {
    private final SolicitarOrientacaoCaso solicitarOrientacaoCaso;
    private final BuscarSolicitacoesPendentesProfessorCaso buscarSolicitacoesCaso;
    private final ResponderSolicitacaoOrientacaoCaso responderSolicitacaoCaso;
    private final GeradorToken geradorToken;

    public SolicitacaoOrientacaoControlador(
            SolicitarOrientacaoCaso solicitarOrientacaoCaso,
            BuscarSolicitacoesPendentesProfessorCaso buscarSolicitacoesCaso,
            ResponderSolicitacaoOrientacaoCaso responderSolicitacaoCaso,
            GeradorToken geradorToken
    ) {
        this.solicitarOrientacaoCaso = solicitarOrientacaoCaso;
        this.buscarSolicitacoesCaso = buscarSolicitacoesCaso;
        this.responderSolicitacaoCaso = responderSolicitacaoCaso;
        this.geradorToken = geradorToken;
    }

    // ========================================================================
    // ROTA DO ALUNO
    // ========================================================================
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

    // ========================================================================
    // ROTAS DO PROFESSOR (ORIENTADOR)
    // ========================================================================

    /**
     * Rota para buscar as solicitações pendentes para a tela do professor
     * @param headerAutorizacao cabeçalho de autorização para pegar o token jwt
     * @return HTTP 200, lista de solicitacoes pendentes do professor logado e infos do grupo
     */
    @GetMapping("/pendentes")
    public ResponseEntity<List<BuscarSolicitacoesPendentesProfessorCaso.Resposta>> listarPendentes(
            @RequestHeader("Authorization") String headerAutorizacao
    ) {
        String token = headerAutorizacao.replace("Bearer ", "");

        // Extrai o email (tópico) do token
        String emailUsuarioLogado = geradorToken.extrairTopico(token);

        var comando = new BuscarSolicitacoesPendentesProfessorCaso.Comando(emailUsuarioLogado);
        List<BuscarSolicitacoesPendentesProfessorCaso.Resposta> resposta = buscarSolicitacoesCaso.executar(comando);

        return ResponseEntity.ok(resposta);
    }

    /**
     * Rota para o professor Aceitar ou Recusar a solicitação de um grupo
     * @param idSolicitacao ID da solicitação enviado na URL
     * @param requisicao decisão (aceita: true/false) no corpo
     * @param headerAutorizacao cabeçalho de autorização para pegar o token jwt
     * @return 204 sem conteúdo
     */
    @PostMapping("/{idSolicitacao}/responder")
    public ResponseEntity<Void> responderSolicitacao(
            @PathVariable String idSolicitacao,
            @RequestBody ResponderSolicitacaoRequisicao requisicao,
            @RequestHeader("Authorization") String headerAutorizacao
    ) {
        String token = headerAutorizacao.replace("Bearer ", "");

        //Pega o email do token jwt
        String emailUsuarioLogado = geradorToken.extrairTopico(token);

        var comando = new ResponderSolicitacaoOrientacaoCaso.Comando(
                emailUsuarioLogado,
                idSolicitacao,
                requisicao.aceita()
        );

        responderSolicitacaoCaso.executar(comando);

        return ResponseEntity.noContent().build();
    }


    // ========= DTOs =====
    public record SolicitarOrientacaoRequisicao(
            String idProfessor
    ) {}
    public record ResponderSolicitacaoRequisicao(boolean aceita) {}
}
