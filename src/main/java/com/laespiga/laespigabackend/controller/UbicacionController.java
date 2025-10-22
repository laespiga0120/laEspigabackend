package com.laespiga.laespigabackend.controller;

import com.laespiga.laespigabackend.dto.AsignarUbicacionDto;
import com.laespiga.laespigabackend.dto.RepisaCreateDto;
import com.laespiga.laespigabackend.dto.RepisaDetalleDto;
import com.laespiga.laespigabackend.dto.UbicacionDto;
import com.laespiga.laespigabackend.exception.ResourceNotFoundException;
import com.laespiga.laespigabackend.service.UbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ubicaciones")
public class UbicacionController {

    @Autowired
    private UbicacionService ubicacionService;

    /**
     * Endpoint para obtener la lista de todas las repisas con sus dimensiones.
     * URL: GET /api/v1/ubicaciones/repisas
     */
    @GetMapping("/repisas")
    public ResponseEntity<List<RepisaDetalleDto>> listarRepisas() {
        List<RepisaDetalleDto> repisas = ubicacionService.obtenerTodasLasRepisasConDimensiones();
        return ResponseEntity.ok(repisas);
    }

    /**
     * Endpoint para obtener todas las ubicaciones (con su estado) de una repisa específica.
     * URL: GET /api/v1/ubicaciones/repisas/{id}/detalle
     */
    @GetMapping("/repisas/{id}/detalle")
    public ResponseEntity<List<UbicacionDto>> obtenerDetalleDeRepisa(@PathVariable Integer id) {
        try {
            List<UbicacionDto> ubicaciones = ubicacionService.obtenerUbicacionesPorRepisa(id);
            return ResponseEntity.ok(ubicaciones);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para crear una nueva repisa con sus ubicaciones.
     * URL: POST /api/v1/ubicaciones/repisas
     */
    @PostMapping("/repisas")
    public ResponseEntity<Map<String, String>> crearRepisa(@RequestBody RepisaCreateDto repisaCreateDto) {
        try {
            ubicacionService.crearRepisaYGenerarUbicaciones(repisaCreateDto);
            Map<String, String> response = Collections.singletonMap("message", "Repisa y ubicaciones creadas exitosamente.");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Collections.singletonMap("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint para asignar un producto a una ubicación.
     * URL: POST /api/v1/ubicaciones/asignar
     */
    @PostMapping("/asignar")
    public ResponseEntity<?> asignarProductoAUbicacion(@RequestBody AsignarUbicacionDto asignarDto) {
        try {
            UbicacionDto ubicacionActualizada = ubicacionService.asignarProductoAUbicacion(asignarDto);
            return new ResponseEntity<>(ubicacionActualizada, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(Collections.singletonMap("error", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(Collections.singletonMap("error", e.getMessage()), HttpStatus.CONFLICT); // 409 Conflict es bueno para "ya ocupado"
        }
    }
}