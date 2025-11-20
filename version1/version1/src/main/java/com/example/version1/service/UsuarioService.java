package com.example.version1.service;

import com.example.version1.model.Usuario;

public interface UsuarioService {

    //definir los metodos de validacion de usuario
    //que se van a usar en la aplicacion

    public boolean validarUsuario(Usuario usuario);

    // validar solo credenciales (usado en login)
    public boolean validarCredenciales(String email, String contrasenia);

}
