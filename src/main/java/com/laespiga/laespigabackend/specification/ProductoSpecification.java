package com.laespiga.laespigabackend.specification;

import com.laespiga.laespigabackend.entity.Producto;
import com.laespiga.laespigabackend.entity.Categoria;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class ProductoSpecification {

    // 1. Filtro por ID de Categoría
    public static Specification<Producto> hasCategoria(Integer categoriaId) {
        return (root, query, criteriaBuilder) -> {
            if (categoriaId == null) {
                return criteriaBuilder.conjunction();
            }
            // Navega de Producto a Categoria
            Join<Producto, Categoria> categoriaJoin = root.join("categoria", JoinType.INNER);
            return criteriaBuilder.equal(categoriaJoin.get("idCategoria"), categoriaId);
        };
    }

    // 2. Filtro por Marca (usa el String directo de la entidad Producto)
    public static Specification<Producto> hasMarca(String marca) {
        return (root, query, criteriaBuilder) -> {
            if (marca == null || marca.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            // Usa LIKE para búsqueda parcial e IGNORA mayúsculas/minúsculas
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("marca")), "%" + marca.toLowerCase() + "%");
        };
    }

    // 3. Filtro por Stock Bajo Mínimo (Stock < StockMínimo)
    public static Specification<Producto> isStockBajoMinimo() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThan(root.get("stock"), root.get("stockMinimo"));
    }
}