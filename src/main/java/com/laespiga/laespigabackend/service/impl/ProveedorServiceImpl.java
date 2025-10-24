package com.laespiga.laespigabackend.service.impl;

import com.laespiga.laespigabackend.entity.Proveedor;
import com.laespiga.laespigabackend.repository.ProveedorRepository;
import com.laespiga.laespigabackend.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorServiceImpl implements ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Override
    public List<Proveedor> listarProveedores() {
        return proveedorRepository.findAll();
    }

    @Override
    public Proveedor obtenerPorId(Integer id) {
        return proveedorRepository.findById(id).orElse(null);
    }

    @Override
    public Proveedor registrarProveedor(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    @Override
    public Proveedor actualizarProveedor(Integer id, Proveedor proveedor) {
        Proveedor existente = proveedorRepository.findById(id).orElse(null);
        if (existente != null) {
            existente.setNombreProveedor(proveedor.getNombreProveedor());
            existente.setTelefono(proveedor.getTelefono());
            existente.setEstado(proveedor.getEstado());
            return proveedorRepository.save(existente);
        }
        return null;
    }

    @Override
    public void eliminarProveedor(Integer id) {
        proveedorRepository.deleteById(id);
    }
}
