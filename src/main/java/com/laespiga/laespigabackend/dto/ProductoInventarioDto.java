package com.laespiga.laespigabackend.dto;

// Este DTO es para la tabla principal del Index.tsx
public class ProductoInventarioDto {

    private Integer idProducto;
    private String nombre;
    private String categoria;
    private Double precio;
    private Integer stockDisponible;
    private Integer stockMinimo;
    private String ubicacion; // Formato "A-1-2"
    private String proveedor; // <-- AÑADIDO
    // Constructor, Getters y Setters

    public ProductoInventarioDto() {}

    public ProductoInventarioDto(Integer idProducto, String nombre, String categoria, Double precio, Integer stockDisponible, Integer stockMinimo, String ubicacion, String proveedor) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stockDisponible = stockDisponible;
        this.stockMinimo = stockMinimo;
        this.ubicacion = ubicacion;
        this.proveedor = proveedor; // <-- AÑADIDO
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(Integer stockDisponible) {
        this.stockDisponible = stockDisponible;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    // --- AÑADIDO GETTER Y SETTER ---
    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }
}