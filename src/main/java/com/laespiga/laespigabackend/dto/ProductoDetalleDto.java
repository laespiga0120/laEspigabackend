package com.laespiga.laespigabackend.dto;

import java.util.List;

// Este DTO es para el modal "Ver Detalles"
public class ProductoDetalleDto {

    private Integer idProducto; // <-- AÑADIDO
    private String nombre;
    private String categoria;
    private String marca;
    private String descripcion;
    private Double precio;
    private Integer stockDisponible;
    private Integer stockMinimo;
    private String ubicacion;
    private boolean perecible;
    private String fechaVencimientoProxima; // La fecha más cercana
    private String proveedor; // <-- AÑADIDO
    private List<LoteDetalleDto> lotes;

    // Getters y Setters

    // --- AÑADIDO GETTER Y SETTER ---
    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }
    // -------------------------------
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

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public boolean isPerecible() {
        return perecible;
    }

    public void setPerecible(boolean perecible) {
        this.perecible = perecible;
    }

    public String getFechaVencimientoProxima() {
        return fechaVencimientoProxima;
    }

    public void setFechaVencimientoProxima(String fechaVencimientoProxima) {
        this.fechaVencimientoProxima = fechaVencimientoProxima;
    }

    // --- AÑADIDO GETTER Y SETTER ---
    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }
    // -------------------------------

    public List<LoteDetalleDto> getLotes() {
        return lotes;
    }

    public void setLotes(List<LoteDetalleDto> lotes) {
        this.lotes = lotes;
    }
}