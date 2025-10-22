package com.laespiga.laespigabackend.repository;

import com.laespiga.laespigabackend.entity.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Integer> {
    List<Lote> findByProductoIdProducto(Integer idProducto);
}
