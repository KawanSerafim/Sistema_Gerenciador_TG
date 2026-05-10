package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.implementadores;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GerenciadorCacheCodigo;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GerenciadorCacheCodigoImpl implements GerenciadorCacheCodigo {
    private final Map<String, EntradaCache> cache = new ConcurrentHashMap<>();

        @Value("${app.otp.ttl-minutos:15}")
        private long ttlMinutos;

    private record EntradaCache(String codigo, Instant expiracao) {}

    @Override
    public void salvarCodigo(Email email, String codigo) {
        if(email == null) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "email"
            );
        }
        if(codigo == null || codigo.isBlank()) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "código"
            );
        }

        final Instant expiracao = Instant.now().plus(
                ttlMinutos,
                ChronoUnit.MINUTES
        );

        cache.put(email.valor(), new EntradaCache(codigo, expiracao));
    }

    @Override
    public String buscarCodigo(Email email) {
        if(email == null) return null;

        final EntradaCache entrada = cache.get(email.valor());

        if(entrada == null) return null;

        if(Instant.now().isAfter(entrada.expiracao)) {
            removerCodigo(email);
            return null;
        }

        return entrada.codigo();
    }

    @Override
    public void removerCodigo(Email email) {
        if(email != null) {
            cache.remove(email.valor());
        }
    }
}