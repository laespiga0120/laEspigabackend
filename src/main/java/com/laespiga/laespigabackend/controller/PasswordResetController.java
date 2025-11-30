package com.laespiga.laespigabackend.controller;

import com.laespiga.laespigabackend.dto.PasswordResetDto;
import com.laespiga.laespigabackend.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth/recovery")
@CrossOrigin
public class PasswordResetController {

    @Autowired
    private PasswordResetService resetService;

    // Paso 1: Enviar Código
    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestBody @Valid PasswordResetDto.ForgotRequest request) {
        resetService.solicitarRecuperacion(request.getEmail());
        // Siempre retornamos OK por seguridad para no revelar emails existentes
        return ResponseEntity.ok(Map.of("message", "Si el correo existe, se ha enviado un código de verificación."));
    }

    // Paso 2: Verificar Código
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody @Valid PasswordResetDto.VerifyCodeRequest request) {
        boolean isValid = resetService.validarCodigo(request.getEmail(), request.getCodigo());
        if (isValid) {
            return ResponseEntity.ok(Map.of("message", "Código válido", "valid", true));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Código inválido o expirado", "valid", false));
        }
    }

    // Paso 3: Resetear Contraseña
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid PasswordResetDto.ChangePasswordRequest request) {
        try {
            resetService.cambiarContrasena(request.getEmail(), request.getCodigo(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}