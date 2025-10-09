package com.laespiga.laespigabackend.controller;

import com.laespiga.laespigabackend.entity.Categoria;
import com.laespiga.laespigabackend.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public List<Categoria> listarCategorias() {
        return categoriaService.obtenerTodas();
    }
}
