package com.laespiga.laespigabackend.service.impl;

import com.laespiga.laespigabackend.entity.Producto;
import com.laespiga.laespigabackend.entity.Ubicacion;
import com.laespiga.laespigabackend.exception.ResourceNotFoundException;
import com.laespiga.laespigabackend.repository.ProductoRepository;
import com.laespiga.laespigabackend.repository.UbicacionRepository;
import com.laespiga.laespigabackend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private UbicacionRepository ubicacionRepository;

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
    @Transactional // <-- Importante para asegurar que todo se guarde o nada
    public Producto guardar(Producto producto) {
        // Verificamos si se asignó una ubicación
        if (producto.getUbicacion() != null && producto.getUbicacion().getIdUbicacion() != null) {

            // 1. Buscamos la ubicación en la base de datos
            Ubicacion ubicacion = ubicacionRepository.findById(producto.getUbicacion().getIdUbicacion())
                    .orElseThrow(() -> new ResourceNotFoundException("La ubicación seleccionada no existe."));

            // 2. Verificamos si ya está ocupada
            if ("OCUPADA".equals(ubicacion.getEstado())) {
                throw new IllegalStateException("La ubicación seleccionada ya se encuentra ocupada.");
            }

            // 3. La marcamos como ocupada
            ubicacion.setEstado("OCUPADA");

            // 4. JPA se encargará de guardar la ubicación actualizada al guardar el producto
            producto.setUbicacion(ubicacion);
        }

        // 5. Guardamos el producto con su ubicación ya actualizada
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
