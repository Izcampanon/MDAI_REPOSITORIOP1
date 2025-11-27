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
        if (usuario == null) return false;
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) return false;
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty() || !usuario.getEmail().contains("@")) return false;
        if (usuario.getContrasenia() == null || usuario.getContrasenia().length() < 6) return false;
        // edad: comprobación básica
        if (usuario.getEdad() <= 0 || usuario.getEdad() < 18) return false;
        if (usuario.getTelefono() == null || usuario.getTelefono().trim().length() < 7) return false;
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

    @Override
    public Usuario crearUsuario(Usuario usuario) {
        System.out.println("\t Creando usuario en UsuarioServiceImpl");
        if (usuario == null) throw new IllegalArgumentException("Usuario nulo");
        String email = usuario.getEmail();
        if (email == null || email.isEmpty()) throw new IllegalArgumentException("Email vacío");

        if (repositoryUsuario.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Inicializar saldo por defecto al crear usuario (ej: 100.0)
        if (usuario.getSaldo() <= 0F) {
            usuario.setSaldo(100.0F);
        }

        return repositoryUsuario.save(usuario);
    }

    @Override
    public Usuario autenticar(String email, String contrasenia) {
        System.out.println("\t Autenticando usuario en UsuarioServiceImpl");
        if (email == null || contrasenia == null) return null;
        try {
            return repositoryUsuario.findByEmailIgnoreCase(email)
                    .filter(u -> contrasenia.equals(u.getContrasenia()))
                    .orElse(null);
        } catch (Exception e) {
            System.out.println("\t Error al autenticar: " + e.getMessage());
            return null;
        }
    }

}
