package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso.RedefinirSenhaCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso.SolicitarRecuperacaoSenhaCaso;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conta-usuario")
public class ContaUsuarioControlador {

    private final SolicitarRecuperacaoSenhaCaso solicitarRecuperacaoSenhaCaso;
    private final RedefinirSenhaCaso redefinirSenhaCaso;

    public ContaUsuarioControlador(
            SolicitarRecuperacaoSenhaCaso solicitarRecuperacaoSenhaCaso,
            RedefinirSenhaCaso redefinirSenhaCaso
    ) {
        this.solicitarRecuperacaoSenhaCaso = solicitarRecuperacaoSenhaCaso;
        this.redefinirSenhaCaso = redefinirSenhaCaso;
    }

    // ========================================================================
    // SOLICITAR O CÓDIGO (ENVIA O E-MAIL)
    // ========================================================================
    @PostMapping("/senha/solicitar-recuperacao")
    public ResponseEntity<Void> solicitarRecuperacao(
            @RequestBody SolicitarRecuperacaoRequisicao requisicao
    ) {
        var comando = new SolicitarRecuperacaoSenhaCaso.Comando(requisicao.email());
        solicitarRecuperacaoSenhaCaso.executar(comando);

        return ResponseEntity.noContent().build();
    }

    // ========================================================================
    // REDEFINIR A NOVA SENHA
    // ========================================================================
    @PostMapping("/senha/redefinir")
    public ResponseEntity<Void> redefinirSenha(
            @RequestBody RedefinirSenhaRequisicao requisicao
    ) {
        var comando = new RedefinirSenhaCaso.Comando(
                requisicao.email(),
                requisicao.codigo(),
                requisicao.novaSenha()
        );
        redefinirSenhaCaso.executar(comando);

        return ResponseEntity.noContent().build();
    }

    // ========================================================================
    // DTOs DE REQUISIÇÃO
    // ========================================================================
    public record SolicitarRecuperacaoRequisicao(String email) {}

    public record RedefinirSenhaRequisicao(String email, String codigo, String novaSenha) {}
}
