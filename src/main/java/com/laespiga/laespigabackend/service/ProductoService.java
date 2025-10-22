package com.laespiga.laespigabackend.service;

import com.laespiga.laespigabackend.entity.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoService {

    List<Producto> listarTodos();

    Optional<Producto> obtenerPorId(Integer id);

    Optional<Producto> obtenerPorNombre(String nombre);

    Producto guardar(Producto producto);

    Producto actualizar(Producto producto);


    boolean existePorNombre(String nombre);
}
