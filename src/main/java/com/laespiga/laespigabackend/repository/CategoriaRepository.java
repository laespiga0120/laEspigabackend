package com.laespiga.laespigabackend.repository;

import com.laespiga.laespigabackend.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    Optional<Categoria> findByNombreCategoria(String nombreCategoria);

    boolean existsByNombreCategoria(String nombre);
}
