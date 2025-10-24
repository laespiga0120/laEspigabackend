package com.laespiga.laespigabackend.dto;

public class DetalleHistorialDto {
    private String nombreProducto;
    private Integer cantidad;

    public DetalleHistorialDto(String nombreProducto, Integer cantidad) {
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
    }
    // Getters y Setters
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
