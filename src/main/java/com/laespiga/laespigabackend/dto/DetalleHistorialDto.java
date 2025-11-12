package com.laespiga.laespigabackend.dto;

public class DetalleHistorialDto {
    private String nombreProducto;
    private Integer cantidad;
    private Double precioUnitario; // <-- AÑADIDO
    private Double subtotal;       // <-- AÑADIDO

    // Constructor actualizado para recibir el precio
    public DetalleHistorialDto(String nombreProducto, Integer cantidad, Double precioUnitario) {
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = (precioUnitario != null) ? precioUnitario : 0.0;

        // Calcular subtotal
        if (cantidad != null && precioUnitario != null) {
            this.subtotal = cantidad * precioUnitario;
        } else {
            this.subtotal = 0.0;
        }
    }

    // Getters y Setters
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    // --- NUEVOS GETTERS Y SETTERS ---
    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
}