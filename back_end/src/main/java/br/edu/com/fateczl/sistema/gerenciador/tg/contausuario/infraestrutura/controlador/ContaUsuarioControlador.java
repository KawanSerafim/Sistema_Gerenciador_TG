package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso.ValidarCodigoCaso;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conta-usuario/api")
public class ContaUsuarioControlador {

    private final ValidarCodigoCaso validarCodigoCaso;

    public ContaUsuarioControlador(ValidarCodigoCaso validarCodigoCaso) {
        this.validarCodigoCaso = validarCodigoCaso;
    }
    @PostMapping("/validar-codigo")
    public ResponseEntity<Void> validarCodigo(@RequestBody ValidarCodigoCaso.Comando comando) {
        validarCodigoCaso.executar(
                new ValidarCodigoCaso.Comando(
                        comando.email(), comando.codigoInformado())
        );
        return ResponseEntity.noContent().build();
    }
}
