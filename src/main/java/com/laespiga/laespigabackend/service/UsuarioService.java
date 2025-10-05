package com.laespiga.laespigabackend.service;

import com.laespiga.laespigabackend.entity.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    Usuario registrarUsuario(Usuario usuario);
    Optional<Usuario> buscarPorUsername(String username);
    List<Usuario> listarUsuarios();
    void eliminarUsuario(Integer idUsuario);

}
