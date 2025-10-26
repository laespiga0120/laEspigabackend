package com.laespiga.laespigabackend.service;

import com.laespiga.laespigabackend.entity.Proveedor;
import com.laespiga.laespigabackend.dto.ProveedorDto;
import com.laespiga.laespigabackend.dto.ProveedorCreateDto;
import java.util.List;

public interface ProveedorService {

    List<Proveedor>obtenerTodas();
    List<ProveedorDto> obtenerTodasConConteo();
    ProveedorDto crearProveedor(ProveedorCreateDto createDto);
    ProveedorDto actualizarProveedor(Integer id, ProveedorCreateDto updateDto);
    void eliminarProveedor(Integer id);
}
