package com.laespiga.laespigabackend.service;

import com.laespiga.laespigabackend.dto.AsignarUbicacionDto;
import com.laespiga.laespigabackend.dto.RepisaCreateDto;
import com.laespiga.laespigabackend.dto.RepisaDetalleDto;
import com.laespiga.laespigabackend.dto.UbicacionDto;

import java.util.List;

public interface UbicacionService {

    void crearRepisaYGenerarUbicaciones(RepisaCreateDto repisaCreateDto);

    UbicacionDto asignarProductoAUbicacion(AsignarUbicacionDto asignarDto);

    // --- Métodos nuevos para el frontend ---

    /**
     * Obtiene una lista de todas las repisas con sus detalles básicos y dimensiones.
     * @return Lista de DTOs de repisas.
     */
    List<RepisaDetalleDto> obtenerTodasLasRepisasConDimensiones();

    /**
     * Obtiene todas las ubicaciones (con su estado) de una repisa específica.
     * @param repisaId El ID de la repisa a consultar.
     * @return Lista de DTOs de ubicaciones.
     */
    List<UbicacionDto> obtenerUbicacionesPorRepisa(Integer repisaId);
}

