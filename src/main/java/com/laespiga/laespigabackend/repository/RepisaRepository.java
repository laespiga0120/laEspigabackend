package com.laespiga.laespigabackend.repository;

import com.laespiga.laespigabackend.entity.Repisa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepisaRepository extends JpaRepository<Repisa, Integer> {

    Optional<Repisa> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}
