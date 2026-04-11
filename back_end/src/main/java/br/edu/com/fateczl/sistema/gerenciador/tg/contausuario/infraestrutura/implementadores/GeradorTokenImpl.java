package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.implementadores;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GeradorToken;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class GeradorTokenImpl implements GeradorToken {

    @Value("${app.jwt.secret}")
    private String segredoJwt;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long expiracaoMs;

    private Key pegarChaveAssinatura() {
        return Keys.hmacShaKeyFor(segredoJwt.getBytes());
    }

    @Override
    public String gerarToken(ContaUsuario usuario) {
        if(usuario == null) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "conta de usuário"
            );
        }

        final Date agora = new Date();
        final Date dataExpiracao = new Date(agora.getTime() + expiracaoMs);

        return Jwts.builder()
                .setSubject(usuario.emailTexto())
                .claim("id", usuario.idTexto())
                .claim("status", usuario.status().name())
                .setIssuedAt(agora)
                .setExpiration(dataExpiracao)
                .signWith(pegarChaveAssinatura(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String extrairTopico(String token) {
        if(token == null || token.isBlank()) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "token"
            );
        }

        try {
            final Claims claims = Jwts.parserBuilder()
                    .setSigningKey(pegarChaveAssinatura())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_006_TOKEN_INVALIDO_EXPIRADO
            );
        }
    }
}