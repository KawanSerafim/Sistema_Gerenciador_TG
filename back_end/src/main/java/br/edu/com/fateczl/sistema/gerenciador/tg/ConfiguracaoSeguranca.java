package br.edu.com.fateczl.sistema.gerenciador.tg;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.configuracao.seguranca.FiltroAutenticacaoJwt;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableCaching
@EnableMethodSecurity
@AllArgsConstructor
public class ConfiguracaoSeguranca {
    private static final String[] ROTAS_PUBLICAS_POST = {
            "/login/api",
            "/professores/api/cadastrar",
            "/alunos/api/finalizar-cadastro",
            "/enviar-email/api/enviar",
            "/validar-codigo/api"
    };

    private final FiltroAutenticacaoJwt filtroJwt;

    @Bean
    public SecurityFilterChain filtroChavesSeguranca(HttpSecurity http)
        throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                HttpMethod.POST,
                                ROTAS_PUBLICAS_POST
                        ).permitAll().anyRequest().authenticated()
                )
                .addFilterBefore(
                        filtroJwt,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    @Bean
    public AuthenticationManager gerenciadorAutenticacao(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}