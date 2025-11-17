package com.laespiga.laespigabackend.config;

import com.laespiga.laespigabackend.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // Lista de rutas públicas que NO necesitan validación de token
    private static final List<String> PUBLIC_URLS = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/auth/",
            "/api/v1/categorias",
            "/api/v1/proveedores",
            "/api/v1/ubicaciones",
            "/api/productos/registrar",
            "/api/productos/inventario",
            "/api/productos/filtros",
            "/api/productos/detalles"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // 🔸 CRÍTICO: Permitir peticiones OPTIONS (CORS preflight) sin validar token
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🔸 CRÍTICO: Saltar validación JWT para rutas públicas
        boolean isPublicUrl = PUBLIC_URLS.stream()
                .anyMatch(publicUrl -> requestPath.startsWith(publicUrl));

        if (isPublicUrl) {
            filterChain.doFilter(request, response);
            return;
        }

        // Procesar token JWT para rutas protegidas
        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.obtenerUsername(jwt);
            } catch (Exception e) {
                // Token inválido o expirado, continuar sin autenticación
                logger.error("Error al extraer username del token: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validarToken(jwt)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}