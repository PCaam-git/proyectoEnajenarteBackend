package com.svalero.enajenarte.config;

import com.svalero.enajenarte.security.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    // Genera el hash BCrypt al guardar una contraseña
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/contact-messages").permitAll()

                        // públicos
                        .requestMatchers(HttpMethod.GET, "/events/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/workshops/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/programs/**").permitAll()

                        // USER autenticado / ADMIN - users
                        .requestMatchers(HttpMethod.PUT, "/users/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")

                        // ADMIN - events
                        .requestMatchers(HttpMethod.POST, "/events").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/events/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/events/**").hasRole("ADMIN")

                        // ADMIN - workshops
                        .requestMatchers(HttpMethod.POST, "/workshops").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/workshops/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/workshops/**").hasRole("ADMIN")

                        // ADMIN - programs
                        .requestMatchers(HttpMethod.POST, "/programs").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/programs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/programs/**").hasRole("ADMIN")

                        // ADMIN - speakers
                        .requestMatchers(HttpMethod.POST, "/speakers").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/speakers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/speakers/**").hasRole("ADMIN")

                        // USER autenticado / ADMIN - registrations
                        .requestMatchers(HttpMethod.POST, "/registrations").authenticated()

                        // USER autenticado / ADMIN - program registrations
                        .requestMatchers(HttpMethod.POST, "/program-registrations").authenticated()

                        // ADMIN - registrations
                        .requestMatchers(HttpMethod.GET, "/registrations").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/registrations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/registrations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/registrations/**").hasRole("ADMIN")


                        // ADMIN - program registrations
                        .requestMatchers(HttpMethod.GET, "/program-registrations").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/program-registrations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/program-registrations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/program-registrations/**").hasRole("ADMIN")

                        // ADMIN - contact message
                        .requestMatchers(HttpMethod.GET, "/contact-messages").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/contact-messages/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/contact-messages/**").hasRole("ADMIN")

                        // ADMIN - calendar
                        // ADMIN - admin calendar
                        .requestMatchers(HttpMethod.GET, "/admin-calendar").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/admin-calendar/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/admin-calendar").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/admin-calendar/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/admin-calendar/**").hasRole("ADMIN")




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