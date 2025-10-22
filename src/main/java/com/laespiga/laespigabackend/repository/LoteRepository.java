package com.laespiga.laespigabackend.repository;

import com.laespiga.laespigabackend.entity.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Integer> {
    List<Lote> findByProductoIdProducto(Integer idProducto);
    @Query("SELECT l FROM Lote l WHERE l.producto.idProducto = :idProducto AND l.cantidad > 0 ORDER BY l.fechaVencimiento ASC, l.fechaRegistro ASC")
    List<Lote> findLotesDisponiblesParaSalida(Integer idProducto);
}
