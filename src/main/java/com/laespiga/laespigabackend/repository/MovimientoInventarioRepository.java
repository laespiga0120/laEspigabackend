package com.laespiga.laespigabackend.repository;

import com.laespiga.laespigabackend.entity.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Repositorios para Movimiento y Detalle
@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {
    List<MovimientoInventario> findByTipoMovimientoOrderByFechaMovimientoDesc(String tipoMovimiento);
}