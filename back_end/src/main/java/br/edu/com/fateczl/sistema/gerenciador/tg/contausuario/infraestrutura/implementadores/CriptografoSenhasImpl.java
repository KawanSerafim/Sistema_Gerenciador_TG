package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.implementadores;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.CriptografoSenhas;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Senha;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CriptografoSenhasImpl implements CriptografoSenhas {
    private final PasswordEncoder codificador = new BCryptPasswordEncoder(12);

    @Override
    public Senha criptografar(String senhaLimpa) {
        if(senhaLimpa == null || senhaLimpa.isBlank()) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "senha"
            );
        }

        final String hash = codificador.encode(senhaLimpa);
        return new Senha(hash);
    }

    @Override
    public boolean comparar(String senhaLimpa, Senha senhaCriptografada) {
        if(senhaLimpa == null
                || senhaLimpa.isBlank()
                || senhaCriptografada == null
        ) {
            return false;
        }

        return codificador.matches(senhaLimpa, senhaCriptografada.valor());
    }
}