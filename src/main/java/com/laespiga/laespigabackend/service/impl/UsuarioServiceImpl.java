package com.laespiga.laespigabackend.service.impl;

import com.laespiga.laespigabackend.entity.Usuario;
import com.laespiga.laespigabackend.repository.UsuarioRepository;
import com.laespiga.laespigabackend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Usuario registrarUsuario(Usuario usuario) {
        //Validacion de duplicados
        if(usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        //Asignar fecha y estado por defecto si no se proporcionan
        if(usuario.getFechaIngreso() == null) {
            usuario.setFechaIngreso(LocalDateTime.now());
        }
        if (usuario.getEstado() == null){
            usuario.setEstado(true);
        }
        //Encriptar contraseña
        String hashed = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(hashed);

        //guardar en la base de datos
        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public void eliminarUsuario(Integer idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }

}
