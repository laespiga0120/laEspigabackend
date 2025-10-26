package com.laespiga.laespigabackend.dto;

public class ProductoBusquedaDto {
    private Integer idProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private Integer stock;

    public ProductoBusquedaDto(Integer idProducto, String nombreProducto, String descripcionProducto, Integer stock) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.descripcionProducto = descripcionProducto;
        this.stock = stock;
    }
    // Getters y Setters
    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public String getDescripcionProducto() { return descripcionProducto; }
    public void setDescripcionProducto(String descripcionProducto) { this.descripcionProducto = descripcionProducto; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
