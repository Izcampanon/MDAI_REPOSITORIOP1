package com.example.version1.service;

import com.example.version1.model.Usuario;

public interface UsuarioService {

    //definir los metodos de validacion de usuario
    //que se van a usar en la aplicacion

    // validar usuario completo (usado en registro)
    boolean validarUsuario(Usuario usuario);

    // validar solo credenciales (usado en login)
    boolean validarCredenciales(String email, String contrasenia);

    // crear/guardar usuario en la BD
    Usuario crearUsuario(Usuario usuario);

    // autenticar y devolver el usuario si las credenciales son correctas
    Usuario autenticar(String email, String contrasenia);

}
