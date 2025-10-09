package com.laespiga.laespigabackend.service.impl;

import com.laespiga.laespigabackend.entity.Categoria;
import com.laespiga.laespigabackend.repository.CategoriaRepository;
import com.laespiga.laespigabackend.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public List<Categoria> obtenerTodas() {
        return categoriaRepository.findAll();
    }

    @Override
    public Optional<Categoria> obtenerPorId(Integer id) {
        return Optional.empty();
    }

    @Override
    public Optional<Categoria> obtenerPorNombre(String nombre) {
        return Optional.empty();
    }

    @Override
    public Categoria guardar(Categoria categoria) {
        return null;
    }

    @Override
    public Categoria actualizar(Categoria categoria) {
        return null;
    }

    @Override
    public void eliminar(Integer id) {

    }

    @Override
    public boolean existePorNombre(String nombre) {
        return false;
    }
}
