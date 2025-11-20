package com.example.version1.service;


import com.example.version1.model.Usuario;
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
    public boolean validarUsuario(Usuario usuario) {
        System.out.println("\t Validando usuario en UsuarioServiceImpl");
        // Implementacion basica de validacion
        if (usuario.getNombre() == null || usuario.getNombre().isEmpty()) return false;
        if (usuario.getEmail() == null || usuario.getEmail().isEmpty() || !usuario.getEmail().contains("@")) return false;
        if(usuario.getContrasenia() == null || usuario.getContrasenia().length() < 6) return false;
        try {
            if (usuario.getEdad() < 18) return false; //es menor de edad
        } catch (NumberFormatException e) {
            return false;
        }
        if (usuario.getTelefono() == null || usuario.getTelefono().length() < 9) return false;


        return true;
    }

    @Override
    public boolean validarCredenciales(String email, String contrasenia) {
        System.out.println("\t Validando credenciales en UsuarioServiceImpl");
        if (email == null || email.isEmpty() || !email.contains("@")) return false;
        if (contrasenia == null || contrasenia.isEmpty()) return false;

        // Buscar usuario por email y comparar contrasenia
        try {
            return repositoryUsuario.findByEmailIgnoreCase(email)
                    .map(u -> contrasenia.equals(u.getContrasenia()))
                    .orElse(false);
        } catch (Exception e) {
            System.out.println("\t Error al validar credenciales: " + e.getMessage());
            return false;
        }
    }



}
