package com.laespiga.laespigabackend.service.impl;

import com.laespiga.laespigabackend.entity.Producto;
import com.laespiga.laespigabackend.repository.ProductoRepository;
import com.laespiga.laespigabackend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Override
    public Optional<Producto> obtenerPorId(Integer id) {
        return productoRepository.findById(id);
    }

    @Override
    public Optional<Producto> obtenerPorNombre(String nombre) {
        return productoRepository.findByNombreProducto(nombre);
    }

    @Override
    public Producto guardar(Producto producto) {
        // Verifica si ya existe un producto con el mismo nombre
        if (productoRepository.existsByNombreProducto(producto.getNombreProducto())) {
            throw new IllegalArgumentException("Ya existe un producto con ese nombre");
        }
        return productoRepository.save(producto);
    }

    @Override
    public Producto actualizar(Producto producto) {
        // Verifica si el producto existe
        if (!productoRepository.existsById(producto.getIdProducto())) {
            throw new IllegalArgumentException("El producto no existe");
        }
        return productoRepository.save(producto);
    }

    @Override
    public boolean existePorNombre(String nombre) {
        return productoRepository.existsByNombreProducto(nombre);
    }
}
