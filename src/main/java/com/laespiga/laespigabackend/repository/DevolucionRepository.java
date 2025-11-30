package com.laespiga.laespigabackend.repository;

import com.laespiga.laespigabackend.entity.Devolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface DevolucionRepository extends JpaRepository<Devolucion, Integer> {

    // Buscar devoluciones pendientes
    List<Devolucion> findByEstadoOrderByFechaRecepcionProgramadaAsc(String estado);

    // Buscar devoluciones pendientes que ya vencieron su hora de recepción (para el scheduler)
    @Query("SELECT d FROM Devolucion d WHERE d.estado = 'PENDIENTE' AND " +
            "(d.fechaRecepcionProgramada < :fechaActual OR " +
            "(d.fechaRecepcionProgramada = :fechaActual AND d.horaRecepcionProgramada <= :horaActual))")
    List<Devolucion> findDevolucionesParaProcesar(LocalDate fechaActual, LocalTime horaActual);
}