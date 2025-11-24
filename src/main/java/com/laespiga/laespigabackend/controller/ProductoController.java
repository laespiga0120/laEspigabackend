package com.laespiga.laespigabackend.controller;

import com.laespiga.laespigabackend.dto.FiltrosDto;
import com.laespiga.laespigabackend.dto.ProductoDTO;
import com.laespiga.laespigabackend.dto.ProductoDetalleDto;
import com.laespiga.laespigabackend.dto.ProductoInventarioDto;
import com.laespiga.laespigabackend.entity.Categoria;
import com.laespiga.laespigabackend.dto.ProductoUpdateDto; // <-- AÑADIDO
import com.laespiga.laespigabackend.entity.Producto;
import com.laespiga.laespigabackend.entity.Proveedor;
import com.laespiga.laespigabackend.entity.Ubicacion;
import com.laespiga.laespigabackend.exception.ResourceNotFoundException;
import com.laespiga.laespigabackend.exception.UbicacionOcupadaException;
import com.laespiga.laespigabackend.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException; // <-- AÑADIDO
import org.springframework.security.core.Authentication; // <-- AÑADIDO

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        try {
            // Validación manual: fecha de vencimiento
            if (productoDTO.isPerecible()) {
                if (productoDTO.getFechaVencimiento() == null ||
                        productoDTO.getFechaVencimiento().isBefore(LocalDateTime.now())) {
                    return ResponseEntity.badRequest()
                            .body("Debe ingresar una fecha de vencimiento válida");
                }
            }

            // Validación de duplicado
            boolean existe = productoService.existePorNombre(productoDTO.getNombreProducto());
            if (existe) {
                return ResponseEntity.badRequest()
                        .body("Ya existe un producto con ese nombre");
            }
            if(productoDTO.getPrecioCompra()>= productoDTO.getPrecioVenta()) {
                return ResponseEntity.badRequest()
                        .body("El precio de venta debe ser mayor al precio de compra");
            }

            // Mapeo manual del DTO → Entidad
            Producto producto = new Producto();
            producto.setNombreProducto(productoDTO.getNombreProducto());
            producto.setDescripcionProducto(productoDTO.getDescripcion());
            producto.setUnidadMedida(productoDTO.getUnidadMedida());
            producto.setPrecioCompra(productoDTO.getPrecioCompra());
            producto.setPrecioVenta(productoDTO.getPrecioVenta());
            producto.setStock(productoDTO.getStock() != null ? productoDTO.getStock() : 0);
            producto.setStockMinimo(productoDTO.getStockMinimo());
            producto.setPerecible(productoDTO.isPerecible());
            producto.setFechaVencimiento(productoDTO.getFechaVencimiento());
            producto.setMarca(productoDTO.getMarca());

            // 🔹 Set categoría y ubicación (solo IDs por ahora)
            Categoria categoria = new Categoria();
            categoria.setIdCategoria(productoDTO.getIdCategoria());
            producto.setCategoria(categoria);
            Proveedor proveedor = new Proveedor();
            proveedor.setIdProveedor(productoDTO.getIdProveedor());
            producto.setProveedor(proveedor);

            // ...
// 🔹 Asignar la ubicación seleccionada desde el frontend
            if (productoDTO.getIdUbicacion() != null) {
                Ubicacion ubicacion = new Ubicacion();
                // Usamos el ID que viene en el payload
                ubicacion.setIdUbicacion(productoDTO.getIdUbicacion());
                producto.setUbicacion(ubicacion);
            } else {
                // Si la ubicación es obligatoria, puedes lanzar un error aquí
                return ResponseEntity.badRequest().body("Debe seleccionar una ubicación para el producto");
            }
// ...
            // 🔹 Guardar producto
            productoService.guardar(producto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Producto registrado correctamente");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se pudo registrar el producto, intente nuevamente");
        }
    }

    // --- NUEVOS ENDPOINTS PARA EL PANEL PRINCIPAL (Index.tsx) ---

    /**
     * Endpoint para obtener la lista filtrada y ordenada de productos para la tabla principal.
     * GET /api/v1/productos/inventario?nombre=...&categoriaId=...&repisa=...&fila=...&columna=...&sortBy=...&sortDir=...
     */
    @GetMapping("/inventario")
    public ResponseEntity<List<ProductoInventarioDto>> getInventario(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) String repisa,
            @RequestParam(required = false) Integer fila,
            @RequestParam(required = false) Integer columna,
            @RequestParam(defaultValue = "nombreProducto") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        List<ProductoInventarioDto> productos = productoService.getInventario(
                nombre, categoriaId, repisa, fila, columna, sortBy, sortDir
        );
        return ResponseEntity.ok(productos);
    }

    /**
     * Endpoint para obtener los datos para poblar los filtros (categorías y repisas).
     * GET /api/v1/productos/filtros
     */
    @GetMapping("/filtros")
    public ResponseEntity<FiltrosDto> getFiltrosInventario() {
        FiltrosDto filtros = productoService.getFiltrosInventario();
        return ResponseEntity.ok(filtros);
    }

    /**
     * Endpoint para obtener los detalles de un solo producto (para el modal).
     * GET /api/v1/productos/detalles/{id}
     */
    @GetMapping("/detalles/{id}")
    public ResponseEntity<ProductoDetalleDto> getProductoDetalle(@PathVariable Integer id) {
        ProductoDetalleDto detalle = productoService.getProductoDetalle(id);
        return ResponseEntity.ok(detalle);
    }

    /**
     * Endpoint para actualizar parcialmente un producto (Nombre, Desc, Cat, Precio, StockMin).
     * PUT /api/v1/productos/actualizar/{id}
     */
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Integer id,
            @Valid @RequestBody ProductoUpdateDto dto,
            Authentication authentication
    ) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acceso denegado.");
        }

        try {
            String username = authentication.getName();
            ProductoDetalleDto productoActualizado = productoService.actualizarProductoParcial(id, dto, username);
            return ResponseEntity.ok(productoActualizado);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (UbicacionOcupadaException e) { // --- NUEVO CATCH ---
            // Devolver JSON con información del producto que ocupa la ubicación
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "UBICACION_OCUPADA");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("idProductoOcupante", e.getIdProductoOcupante());
            errorResponse.put("nombreProductoOcupante", e.getNombreProductoOcupante());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el producto: " + e.getMessage());
        }
    }
}
