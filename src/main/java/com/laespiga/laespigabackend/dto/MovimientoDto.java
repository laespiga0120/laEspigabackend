package com.laespiga.laespigabackend.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTOs para la gestión de movimientos de salida.
 */
public class MovimientoDto {

    /**
     * DTO para el payload de registrar una nueva salida.
     */
    public static class RegistroSalidaDto {
        private String motivo;
        private String observacion; // Para el caso "otro"
        private List<DetalleSalidaDto> detalles;

        // Getters y Setters
        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }
        public String getObservacion() { return observacion; }
        public void setObservacion(String observacion) { this.observacion = observacion; }
        public List<DetalleSalidaDto> getDetalles() { return detalles; }
        public void setDetalles(List<DetalleSalidaDto> detalles) { this.detalles = detalles; }
    }

    /**
     * DTO para cada item en la lista de productos a retirar.
     */
    public static class DetalleSalidaDto {
        private Integer idProducto;
        private int cantidad;

        // Getters y Setters
        public Integer getIdProducto() { return idProducto; }
        public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    }

    /**
     * DTO para el resultado de la búsqueda de productos.
     */
    public static class ProductoBusquedaDto {
        private Integer idProducto;
        private String nombreProducto;
        private String descripcionProducto;
        private int stock;

        // Constructor, Getters y Setters
        public ProductoBusquedaDto(Integer idProducto, String nombreProducto, String descripcionProducto, int stock) {
            this.idProducto = idProducto;
            this.nombreProducto = nombreProducto;
            this.descripcionProducto = descripcionProducto;
            this.stock = stock;
        }
        public Integer getIdProducto() { return idProducto; }
        public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }
        public String getNombreProducto() { return nombreProducto; }
        public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
        public String getDescripcionProducto() { return descripcionProducto; }
        public void setDescripcionProducto(String descripcionProducto) { this.descripcionProducto = descripcionProducto; }
        public int getStock() { return stock; }
        public void setStock(int stock) { this.stock = stock; }
    }

    /**
     * DTO para mostrar el historial de movimientos.
     */
    public static class MovimientoHistorialDto {
        private Integer idMovimiento;
        private String motivo;
        private LocalDateTime fechaMovimiento;
        private String nombreUsuario;
        private List<DetalleHistorialDto> detalles;

        // Getters y Setters
        public Integer getIdMovimiento() { return idMovimiento; }
        public void setIdMovimiento(Integer idMovimiento) { this.idMovimiento = idMovimiento; }
        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }
        public LocalDateTime getFechaMovimiento() { return fechaMovimiento; }
        public void setFechaMovimiento(LocalDateTime fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }
        public String getNombreUsuario() { return nombreUsuario; }
        public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
        public List<DetalleHistorialDto> getDetalles() { return detalles; }
        public void setDetalles(List<DetalleHistorialDto> detalles) { this.detalles = detalles; }
    }

    /**
     * DTO para cada detalle dentro del historial.
     */
    public static class DetalleHistorialDto {
        private String nombreProducto;
        private int cantidad;

        // Constructor, Getters y Setters
        public DetalleHistorialDto(String nombreProducto, int cantidad) {
            this.nombreProducto = nombreProducto;
            this.cantidad = cantidad;
        }
        public String getNombreProducto() { return nombreProducto; }
        public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    }
}
