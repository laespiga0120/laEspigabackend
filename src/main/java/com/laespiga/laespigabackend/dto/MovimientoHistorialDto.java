package com.laespiga.laespigabackend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MovimientoHistorialDto {
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
