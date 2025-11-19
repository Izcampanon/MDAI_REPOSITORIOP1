package com.example.version1.service;

public interface UsuarioService {

    //definir los metodos de validacion de usuario
    //que se van a usar en la aplicacion

    public boolean validarUsuario(String nombre, String email, String contrasenia, String edad, String telefono);


}
