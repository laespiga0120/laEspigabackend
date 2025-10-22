package com.laespiga.laespigabackend.controller;

import com.laespiga.laespigabackend.dto.ProductoDTO;
import com.laespiga.laespigabackend.entity.Categoria;
import com.laespiga.laespigabackend.entity.Producto;
import com.laespiga.laespigabackend.entity.Ubicacion;
import com.laespiga.laespigabackend.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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

            // Mapeo manual del DTO → Entidad
            Producto producto = new Producto();
            producto.setNombreProducto(productoDTO.getNombreProducto());
            producto.setDescripcionProducto(productoDTO.getDescripcion());
            producto.setUnidadMedida(productoDTO.getUnidadMedida());
            producto.setPrecio(productoDTO.getPrecio());
            producto.setStock(productoDTO.getStock() != null ? productoDTO.getStock() : 0);
            producto.setStockMinimo(productoDTO.getStockMinimo());
            producto.setPerecible(productoDTO.isPerecible());
            producto.setFechaVencimiento(productoDTO.getFechaVencimiento());
            producto.setMarca(productoDTO.getMarca());

            // 🔹 Set categoría y ubicación (solo IDs por ahora)
            Categoria categoria = new Categoria();
            categoria.setIdCategoria(productoDTO.getIdCategoria());
            producto.setCategoria(categoria);

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
}
