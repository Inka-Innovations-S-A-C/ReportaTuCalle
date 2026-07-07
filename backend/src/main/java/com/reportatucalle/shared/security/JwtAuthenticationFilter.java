package com.reportatucalle.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de seguridad transversal.
 * Se ejecuta una única vez por cada petición HTTP (OncePerRequestFilter).
 * Su responsabilidad es extraer el token JWT, validarlo y establecer el 
 * contexto de autenticación en Spring Security.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // Lo implementaremos en el módulo Auth

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Obtener el token del header o del parámetro de query (para SSE)
        String authHeader = request.getHeader("Authorization");
        String jwt = null;
        final String userEmail;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        } else if (request.getRequestURI().contains("/stream") && request.getParameter("token") != null) {
            jwt = request.getParameter("token");
        }

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }
        userEmail = jwtService.extractUsername(jwt);

        // 4. Si tenemos email y el usuario aún no está autenticado en este contexto
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Cargamos los datos del usuario desde la base de datos
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Validamos matemáticamente el token contra los datos del usuario y la fecha de expiración
            if (jwtService.isTokenValid(jwt, userDetails)) {
                
                // Creamos el token de autenticación de Spring
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Credenciales nulas porque usamos JWT, no enviamos la contraseña en cada request
                        userDetails.getAuthorities()
                );
                
                // Añadimos detalles adicionales de la petición web (IP, session id - aunque estemos stateless)
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                // Registramos al usuario en el contexto global de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 5. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}