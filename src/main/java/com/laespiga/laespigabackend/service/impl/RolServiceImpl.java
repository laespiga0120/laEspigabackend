package com.laespiga.laespigabackend.service.impl;

import com.laespiga.laespigabackend.entity.Rol;
import com.laespiga.laespigabackend.repository.RolRepository;
import com.laespiga.laespigabackend.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolServiceImpl implements RolService {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }
}

