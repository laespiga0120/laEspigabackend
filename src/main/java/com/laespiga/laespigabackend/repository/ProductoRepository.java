package com.laespiga.laespigabackend.repository;

import com.laespiga.laespigabackend.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // <-- NUEVA INTERFAZ
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// Se extiende JpaSpecificationExecutor
public interface ProductoRepository extends JpaRepository<Producto, Integer>, JpaSpecificationExecutor<Producto> {
    Optional<Producto> findByNombreProducto(String nombreProducto);
    List<Producto> findByNombreProductoContainingIgnoreCase(String nombre);
    boolean existsByNombreProducto(String nombreProducto);
    List<Producto> findTop5ByOrderByNombreProductoAsc();
    List<Producto> findByProveedorIdProveedor(Integer idProveedor);
}