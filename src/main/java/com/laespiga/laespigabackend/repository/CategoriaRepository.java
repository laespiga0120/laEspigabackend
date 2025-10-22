package com.laespiga.laespigabackend.repository;

import com.laespiga.laespigabackend.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    /**
     * Busca una categoría por su nombre. Útil para validaciones.
     *
     * @param nombreCategoria El nombre de la categoría a buscar.
     * @return Un Optional que contiene la Categoria si se encuentra.
     */
    Optional<Categoria> findByNombreCategoria(String nombreCategoria);

    /**
     * Verifica de forma eficiente si ya existe una categoría con un nombre dado.
     *
     * @param nombreCategoria El nombre a verificar.
     * @return true si existe, false en caso contrario.
     */
    boolean existsByNombreCategoria(String nombreCategoria);
}
