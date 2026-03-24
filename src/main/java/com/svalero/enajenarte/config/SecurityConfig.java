package com.svalero.enajenarte.config;

import com.svalero.enajenarte.security.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

// Dice a spring que esta clase contiene configuración. La leerá al arrancar
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private AuthTokenFilter authTokenFilter;

    @Bean
    // Security filter chain: define las reflas de seguridad
    // Lo que se configura aquí, afecta a cómo se protegen lso endpoints
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // csfr -> desactiva la defensa contra ese tipo de ataque. Una web maliciosa hace peticiones en mi nombre
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Define las peticiones que están permitidas y las que no
                // Get event y workshops para todos, registrarse para todos, resto de endopoints protegidos
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/users").permitAll()

                        // públicos
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/events/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/workshops/**").permitAll()

                        // USER autenticado / ADMIN - users
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/users/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/users/**").hasRole("ADMIN")

                        // ADMIN - events
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/events").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/events/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/events/**").hasRole("ADMIN")

                        // ADMIN - workshops
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/workshops").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/workshops/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/workshops/**").hasRole("ADMIN")

                        // ADMIN - speakers
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/speakers").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/speakers/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/speakers/**").hasRole("ADMIN")

                        // USER autenticado / ADMIN - registrations
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/registrations").authenticated()

                        // ADMIN - registrations
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/registrations").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/registrations/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/registrations/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/registrations/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        http.cors(cors -> {});

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}