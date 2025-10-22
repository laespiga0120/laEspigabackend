package com.laespiga.laespigabackend.service.impl;

import com.laespiga.laespigabackend.entity.Lote;
import com.laespiga.laespigabackend.entity.Producto;
import com.laespiga.laespigabackend.entity.Ubicacion;
import com.laespiga.laespigabackend.exception.ResourceNotFoundException;
import com.laespiga.laespigabackend.repository.LoteRepository;
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

    @Autowired
    private LoteRepository loteRepository;


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
    @Transactional
    public Producto guardar(Producto producto) {
        // Validaciones de ubicación (ya existentes)
        if (producto.getUbicacion() != null && producto.getUbicacion().getIdUbicacion() != null) {
            Ubicacion ubicacion = ubicacionRepository.findById(producto.getUbicacion().getIdUbicacion())
                    .orElseThrow(() -> new ResourceNotFoundException("La ubicación seleccionada no existe."));

            if ("OCUPADA".equals(ubicacion.getEstado())) {
                throw new IllegalStateException("La ubicación seleccionada ya se encuentra ocupada.");
            }

            ubicacion.setEstado("OCUPADA");
            producto.setUbicacion(ubicacion);
        }

        // Guardar producto
        Producto productoGuardado = productoRepository.save(producto);

        // 🔹 Crear lote inicial si hay stock
        if (productoGuardado.getStock() != null && productoGuardado.getStock() > 0) {
            Lote lote = new Lote();
            lote.setProducto(productoGuardado);
            lote.setCodigoLote("L" + productoGuardado.getIdProducto() + "-" + System.currentTimeMillis());
            lote.setCantidad(productoGuardado.getStock());
            lote.setFechaVencimiento(productoGuardado.getFechaVencimiento());
            loteRepository.save(lote);
        }

        return productoGuardado;
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
