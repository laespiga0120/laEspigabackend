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
                        // 🔸 OPTIONS PRIMERO
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 🔸 Autenticación y Recuperación
                        .requestMatchers("/auth/**").permitAll() // Incluye /auth/recovery/**

                        // 🔸 Endpoints públicos de API
                        .requestMatchers("/api/v1/categorias/**").permitAll()
                        .requestMatchers("/api/v1/proveedores/**").permitAll()
                        .requestMatchers("/api/v1/ubicaciones/**").permitAll()
                        .requestMatchers("/api/v1/movimientos/**").permitAll()
                        .requestMatchers("/api/v1/devoluciones/pendientes").permitAll()
                        .requestMatchers("/api/v1/devoluciones").permitAll()

                        // 🔸 Usuarios y Roles
                        .requestMatchers("/api/v1/usuarios/**").authenticated()
                        .requestMatchers("/api/v1/roles/**").authenticated()
                        .requestMatchers("/api/v1/devoluciones/**").authenticated()
                        // 🔸 Productos
                        .requestMatchers(HttpMethod.POST, "/api/productos/registrar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/inventario").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/filtros").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/detalles/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/productos/actualizar/**").authenticated()

                        // 🔸 Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
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