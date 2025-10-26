package com.laespiga.laespigabackend.service;

import com.laespiga.laespigabackend.dto.*;

import java.util.List;

public interface MovimientoService {

    // --- MÉTODOS DE BÚSQUEDA Y MOVIMIENTOS ---

    List<ProductoBusquedaDto> buscarProductosPorNombre(String nombre);

    void registrarSalida(RegistroSalidaDto salidaDto, Integer idUsuario);
    List<MovimientoHistorialDto> obtenerHistorialDeSalidas();

    List<ProductoPorProveedorDto> obtenerProductosPorProveedor(Integer idProveedor);
    void registrarEntrada(RegistroEntradaDto entradaDto, Integer idUsuario);
    List<MovimientoHistorialDto> obtenerHistorialDeEntradas();

    // --- METODO DE REPORTE DE STOCK ---

    List<ReporteDto> generarReporteStockActual(
            Integer categoriaId,
            String marca,
            Boolean stockBajoMinimo,
            String sortBy,
            String sortDir
    );
}