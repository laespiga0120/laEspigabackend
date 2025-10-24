package com.laespiga.laespigabackend.service;

import com.laespiga.laespigabackend.entity.Proveedor;
import java.util.List;

public interface ProveedorService {
    List<Proveedor> listarProveedores();
    Proveedor obtenerPorId(Integer id);
    Proveedor registrarProveedor(Proveedor proveedor);
    Proveedor actualizarProveedor(Integer id, Proveedor proveedor);
    void eliminarProveedor(Integer id);
}
