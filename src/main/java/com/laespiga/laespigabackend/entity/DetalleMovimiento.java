package com.laespiga.laespigabackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime; // <-- Usamos LocalDateTime

@Entity
@Table(name = "detalle_movimiento")
public class DetalleMovimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_movimiento", nullable = false)
    private MovimientoInventario movimientoInventario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    // --- COLUMNAS PARA EL REGISTRO DE ENTRADA ---

    @Column(name = "precio_unitario")
    private Double precioUnitario;

    @Column(name = "fecha_vencimiento")
    private LocalDateTime fechaVencimiento; // <-- ¡Cambiado a LocalDateTime!

    @Column(name = "observacion_detalle")
    private String observacionDetalle;

    @Column(name = "id_lote")
    private Integer idLote;

    // -------------------------------------------------------------

    // --- Getters y Setters (Asegúrate de cambiar el tipo en el setter/getter de fechaVencimiento) ---

    public Integer getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Integer idDetalle) { this.idDetalle = idDetalle; }
    public MovimientoInventario getMovimientoInventario() { return movimientoInventario; }
    public void setMovimientoInventario(MovimientoInventario movimientoInventario) { this.movimientoInventario = movimientoInventario; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

    public LocalDateTime getFechaVencimiento() { return fechaVencimiento; } // <-- Tipo LocalDateTime
    public void setFechaVencimiento(LocalDateTime fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; } // <-- Tipo LocalDateTime

    public String getObservacionDetalle() { return observacionDetalle; }
    public void setObservacionDetalle(String observacionDetalle) { this.observacionDetalle = observacionDetalle; }
    public Integer getIdLote() { return idLote; }
    public void setIdLote(Integer idLote) { this.idLote = idLote; }
}
