package com.laespiga.laespigabackend.service;

import com.laespiga.laespigabackend.entity.Ubicacion;
import java.util.List;
import java.util.Optional;

public interface UbicacionService {

    List<Ubicacion> listarTodas();

    Optional<Ubicacion> obtenerPorId(Integer id);

    Optional<Ubicacion> obtenerPorNombre(String nombre);

    Ubicacion guardar(Ubicacion ubicacion);

    Ubicacion actualizar(Ubicacion ubicacion);

    void eliminar(Integer id);

    boolean existePorNombre(String nombre);
}