package com.laespiga.laespigabackend.controller;

import com.laespiga.laespigabackend.dto.LoginRequest;
import com.laespiga.laespigabackend.dto.LoginResponse;
import com.laespiga.laespigabackend.entity.Rol;
import com.laespiga.laespigabackend.entity.Usuario;
import com.laespiga.laespigabackend.repository.RolRepository;
import com.laespiga.laespigabackend.repository.UsuarioRepository;
import com.laespiga.laespigabackend.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // 1Autenticar credenciales
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            String token = jwtUtil.generarToken(usuario.getUsername(), usuario.getRol().getNombreRol());

            LoginResponse response = new LoginResponse(
                    "Inicio de sesión correcto",
                    usuario.getRol().getNombreRol(),
                    token
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(
                    new LoginResponse("Credenciales inválidas", null, null)
            );
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        try {
            // 1️⃣ Validar datos obligatorios
            if (usuario.getUsername() == null || usuario.getPassword() == null) {
                return ResponseEntity.badRequest().body("Username y password son obligatorios");
            }

            // 2️⃣ Validar rol
            Rol rol;
            if (usuario.getRol() != null && usuario.getRol().getIdRol() != null) {
                // Usar el idRol proporcionado en el JSON
                rol = rolRepository.findById(usuario.getRol().getIdRol())
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            } else {
                // Usar rol por defecto "USER"
                rol = rolRepository.findByNombreRol("USER")
                        .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));
            }

            // 3️⃣ Encriptar la contraseña antes de guardar
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

            // 4️⃣ Establecer relación bidireccional de manera segura
            usuario.setRol(rol);
            if (rol.getUsuarios() != null) {
                rol.getUsuarios().add(usuario);
            }

            // 5️⃣ Guardar usuario
            usuarioRepository.save(usuario);

            return ResponseEntity.ok("Usuario registrado correctamente");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar usuario: " + e.getMessage());
        }
    }




}

