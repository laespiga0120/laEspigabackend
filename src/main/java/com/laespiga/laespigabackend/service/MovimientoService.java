package com.laespiga.laespigabackend.service;

import com.laespiga.laespigabackend.dto.MovimientoDto.*;
import java.util.List;

public interface MovimientoService {
    /**
     * Busca productos por nombre para el autocompletado.
     */
    List<ProductoBusquedaDto> buscarProductosPorNombre(String nombre);

    /**
     * Registra una nueva salida de inventario.
     */
    void registrarSalida(RegistroSalidaDto salidaDto, Integer idUsuario);

    /**
     * Obtiene el historial de salidas.
     */
    List<MovimientoHistorialDto> obtenerHistorialDeSalidas();
}