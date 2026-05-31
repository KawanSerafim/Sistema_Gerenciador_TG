package br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.api.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GeradorToken;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso.AtribuirMandatoDiretorCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso.BuscarMandatoDiretorVigenteCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso.RetirarMandatoDiretorCaso;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/diretores")
public class MandatoDiretorControlador {

    private final AtribuirMandatoDiretorCaso atribuirMandatoDiretorCaso;
    private final BuscarMandatoDiretorVigenteCaso buscarMandatoDiretorVigenteCaso;
    private final RetirarMandatoDiretorCaso retirarMandatoDiretorCaso;
    private final GeradorToken geradorToken;


    public MandatoDiretorControlador(
            AtribuirMandatoDiretorCaso atribuirMandatoDiretorCaso,
            BuscarMandatoDiretorVigenteCaso buscarMandatoDiretorVigenteCaso,
            RetirarMandatoDiretorCaso retirarMandatoDiretorCaso,
            GeradorToken geradorToken) {
        this.atribuirMandatoDiretorCaso = atribuirMandatoDiretorCaso;
        this.buscarMandatoDiretorVigenteCaso = buscarMandatoDiretorVigenteCaso;
        this.retirarMandatoDiretorCaso = retirarMandatoDiretorCaso;
        this.geradorToken = geradorToken;
    }

    /**
     * Administrador logado atribuir o cargo temporário de Diretor ao profesosr enviado
     * @param requisicao
     * @param headerAutorizacao
     * @return HTTP 201
     */
    @PostMapping("/atribuir")
    public ResponseEntity<Void> atribuirDiretor(
            @RequestBody AtribuirMandatoRequisicao requisicao,
            @RequestHeader("Authorization") String headerAutorizacao) {

        String emailUsuarioLogado = extrairEmailJwt(headerAutorizacao);

        AtribuirMandatoDiretorCaso.Comando comando = new AtribuirMandatoDiretorCaso.Comando(
                emailUsuarioLogado,
                requisicao.matriculaProfessor(),
                requisicao.dataInicio(),
                requisicao.dataFim(),
                requisicao.assinaturaBase64()
        );

        atribuirMandatoDiretorCaso.executar(comando);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Busca o mandato vigente atual, caso tenha retorna 200 com os dados, caso não retorna 204
     * @param headerAutorizacao
     * @return HTTP 200 ou 204
     */
    @GetMapping("/atual")
    public ResponseEntity<BuscarMandatoDiretorVigenteCaso.Resposta> buscarDiretorAtual(
            @RequestHeader("Authorization") String headerAutorizacao
    ) {
        //Verifica se o usuario é administrador
        String emailUsuarioLogado = extrairEmailJwt(headerAutorizacao);

        BuscarMandatoDiretorVigenteCaso.Comando comando =
                new BuscarMandatoDiretorVigenteCaso.Comando(emailUsuarioLogado);

        // Caso tenha mandato vigente retorna HTTP 200 e os dados dele
        return buscarMandatoDiretorVigenteCaso.executar(comando)
                .map(ResponseEntity::ok)
                //Caso não tenha, retorna 204 sem conteudo
                .orElse(ResponseEntity.noContent().build());
    }


    /**
     * Administrador logado retira o cargo temporário de diretor ao professor selecionado
     * @param headerAutorizacao
     * @return HTTP 200
     */
    @PostMapping("/retirar")
    public ResponseEntity<Void> retirarDiretor(
            @RequestHeader("Authorization") String headerAutorizacao
    ) {
        // Extrai a identidade de quem está fazendo a requisição
        String emailUsuarioLogado = extrairEmailJwt(headerAutorizacao);

        RetirarMandatoDiretorCaso.Comando comando = new RetirarMandatoDiretorCaso.Comando(emailUsuarioLogado);

        retirarMandatoDiretorCaso.executar(comando);
        return ResponseEntity.ok().build();
    }


    // Extrair do header de autorização o jwt e pega o email do usuário logado
    private String extrairEmailJwt(String headerAutorizacao){
        // Extrai a identidade de quem está fazendo a requisição de forma segura
        String token = headerAutorizacao.replace("Bearer ", "");
        //Pega o email do token jwt
        return geradorToken.extrairTopico(token);
    }


    // ------------ DTOS ------------

    // DTO que vem do front-end
    public record AtribuirMandatoRequisicao(
            String matriculaProfessor,
            LocalDate dataInicio,
            LocalDate dataFim,
            String assinaturaBase64
            //Email do admin é retirado do jwt
    ) {}
}
