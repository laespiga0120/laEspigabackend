package com.laespiga.laespigabackend.repository;

import com.laespiga.laespigabackend.entity.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

// Repositorios para Movimiento y Detalle
@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {
    List<MovimientoInventario> findByTipoMovimientoOrderByFechaMovimientoDesc(String tipoMovimiento);
    // Nuevo método para el filtro general
    @Query("SELECT m FROM MovimientoInventario m " +
            "WHERE (:tipo IS NULL OR :tipo = '' OR m.tipoMovimiento = :tipo) " +
            "AND (CAST(:fechaInicio AS timestamp) IS NULL OR m.fechaMovimiento >= :fechaInicio) " +
            "AND (CAST(:fechaFin AS timestamp) IS NULL OR m.fechaMovimiento <= :fechaFin) " +
            "ORDER BY m.fechaMovimiento DESC")
    List<MovimientoInventario> buscarPorFiltros(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("tipo") String tipo
    );
}