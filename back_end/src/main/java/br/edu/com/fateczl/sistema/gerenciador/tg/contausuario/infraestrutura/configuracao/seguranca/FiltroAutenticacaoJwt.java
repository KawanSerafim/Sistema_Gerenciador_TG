package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.configuracao.seguranca;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.implementadores.GeradorTokenImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class FiltroAutenticacaoJwt extends OncePerRequestFilter {
    private final GeradorTokenImpl geradorToken;
    private final ServicoDetalhesUsuarioImpl servicoDetalhesUsuario;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest requisicao,
            @NonNull HttpServletResponse resposta,
            @NonNull FilterChain cadeiaDeFiltros
    ) throws ServletException, IOException {
        final String cabecalhoAut = requisicao.getHeader("Authorization");

        if(cabecalhoAut == null || !cabecalhoAut.startsWith("Bearer ")) {
            cadeiaDeFiltros.doFilter(requisicao, resposta);
            return;
        }

        final String jwt = cabecalhoAut.substring(7);
        processarContextoAutenticacao(jwt, requisicao);

        cadeiaDeFiltros.doFilter(requisicao, resposta);
    }

    private void processarContextoAutenticacao(
            String jwt,
            HttpServletRequest requisicao
    ) {
        try {
            final String email = geradorToken.extrairTopico(jwt);

            if(deveAutenticarRequisicao(email)) {
                autenticarNaSpringSecurity(email, requisicao);
            }
        } catch(Exception e) {
            logger.warn("Token JWT inválido ou expirado: " + e.getMessage());
        }
    }

    private boolean deveAutenticarRequisicao(String email) {
        return email != null
                && SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void autenticarNaSpringSecurity(
            String email,
            HttpServletRequest requisicao
    ) {
        UserDetails detalhesUsuario = this.servicoDetalhesUsuario
                .loadUserByUsername(email);

        if(detalhesUsuario.isEnabled()
                && detalhesUsuario.isAccountNonLocked()
        ) {
            var tokenAutenticacao = new UsernamePasswordAuthenticationToken(
                    detalhesUsuario, null, detalhesUsuario.getAuthorities()
            );

            tokenAutenticacao.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(
                            requisicao
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(
                    tokenAutenticacao
            );
        }
    }
}