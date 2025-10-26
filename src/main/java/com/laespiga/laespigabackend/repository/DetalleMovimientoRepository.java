package com.laespiga.laespigabackend.repository;

import com.laespiga.laespigabackend.entity.DetalleMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleMovimientoRepository extends JpaRepository<DetalleMovimiento, Integer> {}

