package com.laespiga.laespigabackend.dto;

public class DetalleHistorialDto {
    private String nombreProducto;
    private Integer idProducto; // <-- NUEVO CAMPO
    private Integer cantidad;
    private Double precioVenta; // <-- AÑADIDO
    private Double precioCompra;
    private Double subtotal;       // <-- AÑADIDO

    // Constructor actualizado para recibir el precio
    public DetalleHistorialDto(Integer idProducto, String nombreProducto, Integer cantidad, Double precioVenta, Double precioCompra) {
        this.idProducto = idProducto; // <-- ASIGNAR
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioVenta = (precioVenta != null) ? precioVenta : 0.0;
        this.precioCompra = (precioCompra != null) ? precioCompra : 0.0;

        // Calcular subtotal (Lógica mejorada para soportar Entradas y Salidas)
        double precioCalculo = 0.0;
        if (precioVenta != null && precioVenta > 0) {
            precioCalculo = precioVenta;
        } else if (precioCompra != null) {
            precioCalculo = precioCompra;
        }

        if (cantidad != null) {
            this.subtotal = cantidad * precioCalculo;
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

    // Getters y Setters
    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

}