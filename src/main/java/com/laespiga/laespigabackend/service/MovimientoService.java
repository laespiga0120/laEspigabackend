package com.laespiga.laespigabackend.service;

import com.laespiga.laespigabackend.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface MovimientoService {

    // --- MÉTODOS DE BÚSQUEDA Y MOVIMIENTOS ---

    List<ProductoBusquedaDto> buscarProductosPorNombre(String nombre);

    void registrarSalida(RegistroSalidaDto salidaDto, String username);
    List<MovimientoHistorialDto> obtenerHistorialDeSalidas();

    List<ProductoPorProveedorDto> obtenerProductosPorProveedor(Integer idProveedor);
    void registrarEntrada(RegistroEntradaDto entradaDto, String username);
    List<MovimientoHistorialDto> obtenerHistorialDeEntradas();

    // --- METODO DE REPORTE DE STOCK ---

    List<ReporteDto> generarReporteStockActual(
            Integer categoriaId,
            String marca,
            Boolean stockBajoMinimo,
            String sortBy,
            String sortDir
    );
    // --- NUEVOS MÉTODOS ---
    List<MovimientoHistorialDto> listarMovimientos(LocalDate fechaInicio, LocalDate fechaFin, String tipo);
    void eliminarMovimiento(Integer idMovimiento);
    void actualizarMovimiento(Integer idMovimiento, MovimientoUpdateDto updateDto);
}