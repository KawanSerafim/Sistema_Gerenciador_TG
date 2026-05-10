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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableCaching
@EnableMethodSecurity
@AllArgsConstructor
public class ConfiguracaoSeguranca {

    private static final String[] ROTAS_PUBLICAS_POST = {
            "/api/professores",
            "/api/alunos",
            "/api/alunos/finalizar-cadastro",
            "/api/autenticacao/validar-codigo",
            "/api/autenticacao/login",
            "/api/autenticacao/reenviar-codigo",
            "/api/conta-usuario/senha/solicitar-recuperacao",
            "/api/conta-usuario/senha/redefinir"
    };

    private static final String[] ROTAS_PUBLICAS_GET = {
            //Evita que o spring security mascare erros
            "/error",
            "/api/cursos/tipos-tg",
            "/api/compartilhado/turnos",
            "/api/compartilhado/disciplinas"
    };

    private final FiltroAutenticacaoJwt filtroJwt;

    @Bean
    public SecurityFilterChain filtroChavesSeguranca(HttpSecurity http)
        throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))
                .authorizeHttpRequests(auth -> auth
                        //POST
                        .requestMatchers(
                                HttpMethod.POST,
                                ROTAS_PUBLICAS_POST
                        ).permitAll()
                        //GET
                        .requestMatchers(
                                HttpMethod.GET,
                                ROTAS_PUBLICAS_GET
                        ).permitAll()
                        //Bloqueia o resto
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        filtroJwt,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuracao = new CorsConfiguration();
        //Portas local do React
        configuracao.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
        configuracao.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuracao.setAllowedHeaders(List.of("*"));
        configuracao.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource fonte = new UrlBasedCorsConfigurationSource();
        fonte.registerCorsConfiguration("/**", configuracao);
        return fonte;
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