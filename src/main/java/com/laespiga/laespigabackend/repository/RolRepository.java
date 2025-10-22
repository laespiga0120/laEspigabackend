package com.laespiga.laespigabackend.repository;

import com.laespiga.laespigabackend.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByidRol(Integer idRol);
    Optional<Rol> findByNombreRol(String nombreRol);
}
