package com.laespiga.laespigabackend.controller;

import com.laespiga.laespigabackend.dto.DevolucionCreateDto;
import com.laespiga.laespigabackend.dto.DevolucionListDto;
import com.laespiga.laespigabackend.service.impl.DevolucionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/devoluciones")
public class DevolucionController {

    @Autowired
    private DevolucionServiceImpl devolucionService;

    @PostMapping
    public ResponseEntity<?> registrarDevolucion(@Valid @RequestBody DevolucionCreateDto dto) {
        try {
            devolucionService.registrarSolicitud(dto);
            return ResponseEntity.ok(Map.of("message", "Solicitud de devolución registrada. El stock se repondrá automáticamente en la fecha indicada."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<DevolucionListDto>> listarPendientes() {
        return ResponseEntity.ok(devolucionService.listarPendientes());
    }
}