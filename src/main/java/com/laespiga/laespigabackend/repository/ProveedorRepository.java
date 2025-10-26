package com.laespiga.laespigabackend.repository;

import com.laespiga.laespigabackend.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

    Optional<Proveedor> findByNombreProveedor(String nombreProveedor);

    Optional<Proveedor> findByTelefono(String telefono);

    boolean existsByNombreProveedor(String nombreProveedor);
    boolean existsByTelefono(String telefonoProveedor);

}
