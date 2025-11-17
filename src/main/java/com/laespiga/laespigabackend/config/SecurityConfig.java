package com.laespiga.laespigabackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // 🔸 MUY IMPORTANTE: OPTIONS debe estar PRIMERO
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 🔸 Autenticación (sin token)
                        .requestMatchers("/auth/**").permitAll()

                        // 🔸 Endpoints públicos de API
                        .requestMatchers("/api/v1/categorias/**").permitAll()
                        .requestMatchers("/api/v1/proveedores/**").permitAll()
                        .requestMatchers("/api/v1/ubicaciones/**").permitAll()
                        .requestMatchers("/api/v1/movimientos/**").permitAll()

                        // 🔸 Productos - Endpoints públicos (GET y POST registro)
                        .requestMatchers(HttpMethod.POST, "/api/productos/registrar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/inventario").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/filtros").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/detalles/**").permitAll()

                        // 🔸 Productos - Actualizar (requiere autenticación)
                        .requestMatchers(HttpMethod.PUT, "/api/productos/actualizar/**").authenticated()

                        // 🔸 Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Agregar el filtro JWT
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();

        // 🔸 Permitir tu frontend en desarrollo y producción
        cors.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "https://la-espigafrontend.vercel.app"
        ));

        cors.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        cors.setAllowedHeaders(Arrays.asList("*"));
        cors.setAllowCredentials(true);
        cors.setExposedHeaders(Arrays.asList("Authorization"));
        cors.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }
}