package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso.AutenticarUsuarioCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso.ValidarCodigoCaso;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conta-usuario")
public class ContaUsuarioControlador {

    private final ValidarCodigoCaso validarCodigoCaso;
    private final AutenticarUsuarioCaso autenticarUsuarioCaso;

    public ContaUsuarioControlador(
            ValidarCodigoCaso validarCodigoCaso,
            AutenticarUsuarioCaso autenticarUsuarioCaso) {
        this.validarCodigoCaso = validarCodigoCaso;
        this.autenticarUsuarioCaso = autenticarUsuarioCaso;
    }
    @PostMapping("/validar-codigo")
    public ResponseEntity<Void> validarCodigo(@RequestBody ValidarCodigoCaso.Comando comando) {
        validarCodigoCaso.executar(
                new ValidarCodigoCaso.Comando(
                        comando.email(), comando.codigoInformado())
        );
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
