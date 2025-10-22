package com.laespiga.laespigabackend.controller;

import com.laespiga.laespigabackend.dto.CategoriaCreateDto;
import com.laespiga.laespigabackend.dto.CategoriaDto;
import com.laespiga.laespigabackend.entity.Categoria;
import com.laespiga.laespigabackend.exception.ResourceNotFoundException;
import com.laespiga.laespigabackend.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    /**
     * Endpoint para obtener la lista SIMPLE de categorías.
     * Es el endpoint principal que ya tienes en tu frontend.
     * URL: GET /api/v1/categorias
     */
    @GetMapping
    public List<Categoria> listarCategorias() {
        return categoriaService.obtenerTodas();
    }

    /**
     * Endpoint para obtener la lista COMPLETA de categorías con el conteo de productos.
     * URL: GET /api/v1/categorias/con-conteo
     */
    @GetMapping("/con-conteo")
    public ResponseEntity<List<CategoriaDto>> listarCategoriasConConteo() {
        List<CategoriaDto> categorias = categoriaService.obtenerTodasConConteo();
        return ResponseEntity.ok(categorias);
    }

    /**
     * Endpoint para crear una nueva categoría.
     * URL: POST /api/v1/categorias
     */
    @PostMapping
    public ResponseEntity<?> crearCategoria(@Valid @RequestBody CategoriaCreateDto createDto) {
        try {
            CategoriaDto nuevaCategoria = categoriaService.crearCategoria(createDto);
            return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para actualizar una categoría existente.
     * URL: PUT /api/v1/categorias/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCategoria(@PathVariable Integer id, @Valid @RequestBody CategoriaCreateDto updateDto) {
        try {
            CategoriaDto categoriaActualizada = categoriaService.actualizarCategoria(id, updateDto);
            return ResponseEntity.ok(categoriaActualizada);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para eliminar una categoría.
     * URL: DELETE /api/v1/categorias/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Integer id) {
        try {
            categoriaService.eliminarCategoria(id);
            return ResponseEntity.noContent().build(); // HTTP 204
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            // Se usa 409 Conflict cuando una regla de negocio impide la acción
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }
}
