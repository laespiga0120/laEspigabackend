package com.laespiga.laespigabackend.repository;

import com.laespiga.laespigabackend.entity.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {
    Optional<Ubicacion> findByNombreUbicacion(String nombreUbicacion);

    boolean existsByNombreUbicacion(String nombre);

}
