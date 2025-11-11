package com.laespiga.laespigabackend.controller;

import com.laespiga.laespigabackend.dto.*;
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

    // -------------------------------------------------------------------------
    // --- ENDPOINTS DE BÚSQUEDA Y SALIDAS ---
    // -------------------------------------------------------------------------

    /**
     * Busca productos por nombre.
     * URL: GET /api/v1/movimientos/productos/buscar?nombre=...
     */
    @GetMapping("/productos/buscar")
    public ResponseEntity<List<ProductoBusquedaDto>> buscarProductos(
            // Permitir que el parámetro 'nombre' esté vacío para traer todos
            @RequestParam(required = false, defaultValue = "") String nombre
    ) {
        return ResponseEntity.ok(movimientoService.buscarProductosPorNombre(nombre));
    }

    /**
     * Registra una nueva salida.
     * URL: POST /api/v1/movimientos/salidas
     */
    @PostMapping("/salidas")
    public ResponseEntity<?> registrarSalida(@RequestBody RegistroSalidaDto salidaDto) {
        try {
            Integer idUsuarioAutenticado = 1; // ID de usuario fijo para el ejemplo
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
    public ResponseEntity<List<MovimientoHistorialDto>> obtenerHistorialDeSalidas() {
        return ResponseEntity.ok(movimientoService.obtenerHistorialDeSalidas());
    }

    // -------------------------------------------------------------------------
    // --- ENDPOINTS DE ENTRADAS (NUEVOS) ---
    // -------------------------------------------------------------------------

    /**
     * Obtiene los productos filtrados por un proveedor.
     * URL: GET /api/v1/movimientos/productos/proveedor/{idProveedor}
     */
    @GetMapping("/productos/proveedor/{idProveedor}")
    public ResponseEntity<List<ProductoPorProveedorDto>> obtenerProductosPorProveedor(@PathVariable Integer idProveedor) {
        List<ProductoPorProveedorDto> productos = movimientoService.obtenerProductosPorProveedor(idProveedor);
        return ResponseEntity.ok(productos);
    }

    /**
     * Registra una nueva entrada de mercadería.
     * URL: POST /api/v1/movimientos/entradas
     */
    @PostMapping("/entradas")
    public ResponseEntity<?> registrarEntrada(@RequestBody RegistroEntradaDto entradaDto) {
        try {
            Integer idUsuarioAutenticado = 1; // ID de usuario fijo para el ejemplo
            movimientoService.registrarEntrada(entradaDto, idUsuarioAutenticado);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Entrada registrada exitosamente."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtiene el historial de entradas.
     * URL: GET /api/v1/movimientos/entradas/historial
     */
    @GetMapping("/entradas/historial")
    public ResponseEntity<List<MovimientoHistorialDto>> obtenerHistorialDeEntradas() {
        return ResponseEntity.ok(movimientoService.obtenerHistorialDeEntradas());
    }
}