package com.laespiga.laespigabackend.service;

import com.laespiga.laespigabackend.dto.ProductoUpdateDto;
import com.laespiga.laespigabackend.entity.Producto;
import com.laespiga.laespigabackend.dto.ProductoInventarioDto;
import com.laespiga.laespigabackend.dto.ProductoDetalleDto;
import com.laespiga.laespigabackend.dto.FiltrosDto;
import java.util.List;
import java.util.Optional;

public interface ProductoService {

    List<Producto> listarTodos();

    Optional<Producto> obtenerPorId(Integer id);

    Optional<Producto> obtenerPorNombre(String nombre);

    Producto guardar(Producto producto);

    Producto actualizar(Producto producto);


    boolean existePorNombre(String nombre);

    // --- NUEVOS MÉTODOS PARA EL PANEL PRINCIPAL (Index.tsx) ---

    /**
     * Obtiene la lista de productos para la tabla principal, aplicando filtros y ordenamiento.
     *
     * @param nombre      Filtro por nombre (LIKE)
     * @param categoriaId Filtro por ID de categoría
     * @param repisa      Filtro por código de repisa (JOIN)
     * @param fila        Filtro por número de fila (JOIN)
     * @param columna     Filtro por número de columna (JOIN)
     * @param sortBy      Campo por el cual ordenar
     * @param sortDir     Dirección de ordenamiento (asc/desc)
     * @return Lista de DTOs para el inventario.
     */
    List<ProductoInventarioDto> getInventario(String nombre, Integer categoriaId, String repisa, Integer fila, Integer columna, String sortBy, String sortDir);

    /**
     * Obtiene los detalles completos de un producto, incluyendo sus lotes.
     *
     * @param id ID del producto
     * @return DTO con los detalles completos.
     */
    ProductoDetalleDto getProductoDetalle(Integer id);

    /**
     * Obtiene los datos necesarios para poblar los dropdowns de filtros.
     *
     * @return DTO conteniendo la lista de categorías y repisas.
     */
    FiltrosDto getFiltrosInventario();

    // --- AÑADIDO PARA ACTUALIZAR ---
    /**
     * Actualiza parcialmente un producto.
     *
     * @param idProducto ID del producto a actualizar.
     * @param dto DTO con los campos a modificar.
     * @param username Username del usuario que realiza la operación (para validación de permisos).
     * @return El DTO de detalle actualizado.
     */
    ProductoDetalleDto actualizarProductoParcial(Integer idProducto, ProductoUpdateDto dto, String username);
}
