package com.laespiga.laespigabackend.service.impl;

import com.laespiga.laespigabackend.dto.CategoriaCreateDto;
import com.laespiga.laespigabackend.dto.CategoriaDto;
import com.laespiga.laespigabackend.entity.Categoria;
import com.laespiga.laespigabackend.exception.ResourceNotFoundException;
import com.laespiga.laespigabackend.repository.CategoriaRepository;
import com.laespiga.laespigabackend.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> obtenerTodas() {
        return categoriaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDto> obtenerTodasConConteo() {
        return categoriaRepository.findAll().stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoriaDto crearCategoria(CategoriaCreateDto createDto) {
        if (categoriaRepository.existsByNombreCategoria(createDto.getNombreCategoria())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + createDto.getNombreCategoria());
        }

        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setNombreCategoria(createDto.getNombreCategoria());
        nuevaCategoria.setDescripcion(createDto.getDescripcion());

        Categoria categoriaGuardada = categoriaRepository.save(nuevaCategoria);

        return mapEntityToDto(categoriaGuardada);
    }

    @Override
    @Transactional
    public CategoriaDto actualizarCategoria(Integer id, CategoriaCreateDto updateDto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));

        // Verificar si el nuevo nombre ya está en uso por OTRA categoría
        categoriaRepository.findByNombreCategoria(updateDto.getNombreCategoria())
                .ifPresent(categoriaExistente -> {
                    if (!categoriaExistente.getIdCategoria().equals(id)) {
                        throw new IllegalArgumentException("Ya existe otra categoría con el nombre: " + updateDto.getNombreCategoria());
                    }
                });

        categoria.setNombreCategoria(updateDto.getNombreCategoria());
        categoria.setDescripcion(updateDto.getDescripcion());

        Categoria categoriaActualizada = categoriaRepository.save(categoria);
        return mapEntityToDto(categoriaActualizada);
    }

    @Override
    @Transactional
    public void eliminarCategoria(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));

        // Validación clave: no eliminar si hay productos asociados
        if (categoria.getProductos() != null && !categoria.getProductos().isEmpty()) {
            throw new IllegalStateException("No se puede eliminar la categoría '" + categoria.getNombreCategoria() + "' porque tiene productos asociados.");
        }

        categoriaRepository.delete(categoria);
    }

    private CategoriaDto mapEntityToDto(Categoria categoria) {
        CategoriaDto dto = new CategoriaDto();
        dto.setIdCategoria(categoria.getIdCategoria());
        dto.setNombreCategoria(categoria.getNombreCategoria());
        dto.setDescripcion(categoria.getDescripcion());
        dto.setCantidadProductos(categoria.getProductos() != null ? categoria.getProductos().size() : 0);
        return dto;
    }
}

