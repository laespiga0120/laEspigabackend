package com.laespiga.laespigabackend.service;

import com.laespiga.laespigabackend.entity.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriaService {

    List<Categoria> obtenerTodas();

    Optional<Categoria> obtenerPorId(Integer id);

    Optional<Categoria> obtenerPorNombre(String nombre);

    Categoria guardar(Categoria categoria);

    Categoria actualizar(Categoria categoria);

    void eliminar(Integer id);

    boolean existePorNombre(String nombre);
}