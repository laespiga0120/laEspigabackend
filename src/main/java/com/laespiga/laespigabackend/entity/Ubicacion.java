package com.laespiga.laespigabackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ubicacion")
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ubicacion")
    private Integer idUbicacion;

    @Column(name = "nombre_ubicacion", nullable = false, unique = true, length = 100)
    private String nombreUbicacion;

    public Ubicacion() {}

    public Ubicacion(String nombreUbicacion) {
        this.nombreUbicacion = nombreUbicacion;
    }

    public Integer getIdUbicacion() {
        return idUbicacion;
    }

    public void setIdUbicacion(Integer idUbicacion) {
        this.idUbicacion = idUbicacion;
    }

    public String getNombreUbicacion() {
        return nombreUbicacion;
    }

    public void setNombreUbicacion(String nombreUbicacion) {
        this.nombreUbicacion = nombreUbicacion;
    }
}
