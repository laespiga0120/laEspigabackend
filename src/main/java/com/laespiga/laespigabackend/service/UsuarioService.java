package com.laespiga.laespigabackend.service;

import com.laespiga.laespigabackend.dto.UsuarioDto;
import com.laespiga.laespigabackend.dto.UsuarioRequestDto;
import com.laespiga.laespigabackend.entity.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    Optional<Usuario> buscarPorUsername(String username);
    void eliminarUsuario(Integer idUsuario);

    Usuario registrarUsuario(Usuario usuario); // Mantenemos el original para AuthController
    UsuarioDto crearUsuario(UsuarioRequestDto request); // Nuevo para admin
    UsuarioDto actualizarUsuario(Integer id, UsuarioRequestDto request);
    List<UsuarioDto> listarUsuariosDto(); // Retorna  para la tabla
}
