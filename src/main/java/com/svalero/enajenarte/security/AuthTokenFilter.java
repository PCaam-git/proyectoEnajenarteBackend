package com.svalero.enajenarte.security;

import com.svalero.enajenarte.domain.User;
import com.svalero.enajenarte.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component // El filtro se ejecuta una vez por petición
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String headerAuth = request.getHeader("Authorization");
        // comprueba que el header tiene el formato correcto
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            // extrae el token eliminando el prefijo 'bearer'
            String token = headerAuth.substring(7);

            // obtiene el usuario
            // extrae el username del token y lo busca en la base de datos
            if (jwtUtils.validateJwtToken(token)) {
                String username = jwtUtils.getUserNameFromJwtToken(token);
                User user = userRepository.findByUsername(username);

                // aquí crea un objeto de autenticación con el username y el rol y lo registra
                // a partir de aquí, spring ya sabe quien es el usuario y qué permisos tiene
                if (user != null) {
                    SimpleGrantedAuthority authority =
                            new SimpleGrantedAuthority("ROLE_" + user.getRole());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    List.of(authority)
                            );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}