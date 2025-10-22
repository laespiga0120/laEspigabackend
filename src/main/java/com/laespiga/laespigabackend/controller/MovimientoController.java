package com.laespiga.laespigabackend.controller;

import com.laespiga.laespigabackend.dto.MovimientoDto.*;
import com.laespiga.laespigabackend.service.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/movimientos")
public class MovimientoController {

    @Autowired
    private MovimientoService movimientoService;

    /**
     * Busca productos por nombre.
     * URL: GET /api/v1/movimientos/productos/buscar?nombre=...
     */
    @GetMapping("/productos/buscar")
    public ResponseEntity<List<ProductoBusquedaDto>> buscarProductos(@RequestParam String nombre) {
        return ResponseEntity.ok(movimientoService.buscarProductosPorNombre(nombre));
    }

    /**
     * Registra una nueva salida.
     * URL: POST /api/v1/movimientos/salidas
     */
    @PostMapping("/salidas")
    public ResponseEntity<?> registrarSalida(@RequestBody RegistroSalidaDto salidaDto) {
        try {
            // NOTA: En un sistema real, el ID de usuario se obtendría del contexto de seguridad (ej. JWT).
            // Por ahora, usaremos un ID fijo para la demostración.
            Integer idUsuarioAutenticado = 1;
            movimientoService.registrarSalida(salidaDto, idUsuarioAutenticado);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Salida registrada exitosamente."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtiene el historial de salidas.
     * URL: GET /api/v1/movimientos/salidas/historial
     */
    @GetMapping("/salidas/historial")
    public ResponseEntity<List<MovimientoHistorialDto>> obtenerHistorial() {
        return ResponseEntity.ok(movimientoService.obtenerHistorialDeSalidas());
    }
}
