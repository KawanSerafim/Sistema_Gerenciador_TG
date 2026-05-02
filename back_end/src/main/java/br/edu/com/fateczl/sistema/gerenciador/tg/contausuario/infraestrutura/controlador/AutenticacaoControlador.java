package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso.AutenticarUsuarioCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso.EnviarEmailConfirmacaoCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso.ValidarCodigoCaso;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador que lida com assuntos de autentificacao como login e codigo de confirmacao do email
 */
@RestController
@RequestMapping("/api/autenticacao")
public class AutenticacaoControlador {
    private final ValidarCodigoCaso validarCodigoCaso;
    private final AutenticarUsuarioCaso autenticarUsuarioCaso;
    private final EnviarEmailConfirmacaoCaso enviarEmailCaso;

    public AutenticacaoControlador(
            ValidarCodigoCaso validarCodigoCaso,
            AutenticarUsuarioCaso autenticarUsuarioCaso,
            EnviarEmailConfirmacaoCaso enviarEmailCaso) {
        this.validarCodigoCaso = validarCodigoCaso;
        this.autenticarUsuarioCaso = autenticarUsuarioCaso;
        this.enviarEmailCaso = enviarEmailCaso;
    }

    // ===================== VALIDAÇÃO E REENVIO DE CODIGO =========================//
    @PostMapping("/validar-codigo")
    public ResponseEntity<Void> validarCodigo(@RequestBody ValidarCodigoCaso.Comando comando) {
        validarCodigoCaso.executar(comando);
        return ResponseEntity.noContent().build();
    }

    // DTO simples para receber o JSON do front-end
    public record ReenviarCodigoRequisicao(String email) {}

    @PostMapping("/reenviar-codigo")
    public ResponseEntity<Void> reenviarCodigo(@RequestBody ReenviarCodigoRequisicao requisicao) {
        // Aproveita o comando já criado pelo seu par
        var comando = new EnviarEmailConfirmacaoCaso.Comando(requisicao.email());

        enviarEmailCaso.executar(comando);

        return ResponseEntity.noContent().build();
    }

    // ===================== LOGIN =========================//

    public record LoginRequisicaoDTO(String email, String senha) {}

    public record LoginRespostaDTO(String token) {}

    @PostMapping("/login")
    public ResponseEntity<LoginRespostaDTO> realizarLogin(@RequestBody LoginRequisicaoDTO requisicao) {

        var comando = new AutenticarUsuarioCaso.
                Comando(requisicao.email,
                requisicao.senha);
        String jwtGerado = autenticarUsuarioCaso.executar(comando);

        return ResponseEntity.ok().body(new LoginRespostaDTO(jwtGerado));
    }
}
