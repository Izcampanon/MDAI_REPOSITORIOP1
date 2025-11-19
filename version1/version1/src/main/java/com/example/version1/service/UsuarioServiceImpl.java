package com.example.version1.service;


import com.example.version1.repository.RepositoryUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final RepositoryUsuario repositoryUsuario;

    @Autowired
    public UsuarioServiceImpl(RepositoryUsuario repositoryUsuario) {
        System.out.println("\t Constructing UsuarioServiceImpl");
        this.repositoryUsuario = repositoryUsuario;
    }

    @Override
    public boolean validarUsuario(String nombre, String email, String contrasenia, String edad, String telefono) {
        System.out.println("\t Validando usuario en UsuarioServiceImpl");
        // Implementacion basica de validacion
        if (nombre == null || nombre.isEmpty()) return false;
        if (email == null || email.isEmpty() || !email.contains("@")) return false;
        if(contrasenia == null || contrasenia.length() < 6) return false;
        try {
            int edadInt = Integer.parseInt(edad);
            if (edadInt < 18) return false; //es menor de edad
        } catch (NumberFormatException e) {
            return false;
        }
        if (telefono == null || telefono.length() < 9) return false;


        return true;
    }
}

