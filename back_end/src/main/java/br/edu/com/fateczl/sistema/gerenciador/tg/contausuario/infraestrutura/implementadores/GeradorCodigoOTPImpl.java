package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.implementadores;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GeradorCodigoOTP;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class GeradorCodigoOTPImpl implements GeradorCodigoOTP {
    private final SecureRandom random = new SecureRandom();
    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefg"
            + "hijklmnopqrstuvwxyz0123456789";

    @Override
    public String gerar(int tamanho) {
        StringBuilder codigo = new StringBuilder(tamanho);

        for(int i = 0; i < tamanho; i++) {
            codigo.append(CARACTERES.charAt(random.nextInt(
                    CARACTERES.length()
            )));
        }
        return codigo.toString();
    }
}