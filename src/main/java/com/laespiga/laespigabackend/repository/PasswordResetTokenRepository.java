package com.laespiga.laespigabackend.repository;

import com.laespiga.laespigabackend.entity.PasswordResetToken;
import com.laespiga.laespigabackend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUsuario(Usuario usuario);
    void deleteByUsuario(Usuario usuario); // Para limpiar tokens viejos
}