package com.laespiga.laespigabackend.dto;

public class DetalleHistorialDto {
    private String nombreProducto;
    private Integer cantidad;
    private Double precioVenta; // <-- AÑADIDO
    private Double precioCompra;
    private Double subtotal;       // <-- AÑADIDO

    // Constructor actualizado para recibir el precio
    public DetalleHistorialDto(String nombreProducto, Integer cantidad, Double precioVenta, Double precioCompra) {
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioVenta = (precioVenta != null) ? precioVenta : 0.0;
        this.precioCompra = (precioCompra != null) ? precioCompra : 0.0;

        // Calcular subtotal
        if (cantidad != null && precioVenta != null) {
            this.subtotal = cantidad * precioVenta;
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
    public Double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Double precioVenta) { this.precioVenta = precioVenta; }
    public Double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(Double precioCompra) { this.precioCompra = precioCompra; }
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
}