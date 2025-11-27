package com.example.version1.controller;

import com.example.version1.model.Usuario;
import com.example.version1.repository.RepositoryUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminUsuarioController {

    private final RepositoryUsuario repositoryUsuario;

    @Autowired
    public AdminUsuarioController(RepositoryUsuario repositoryUsuario) {
        this.repositoryUsuario = repositoryUsuario;
    }

    @GetMapping("/admin/users")
    public String listarUsuarios(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        // Si no hay usuario en sesión o no es admin, devolver login con mensaje (consistente con otros controladores)
        if (usuario == null || !usuario.isAdmin()) {
            model.addAttribute("error", "Debes ser administrador para acceder a esta página.");
            model.addAttribute("usuario", new Usuario());
            System.out.println("[AdminUsuarioController] Acceso no autorizado a /admin/users");
            return "login";
        }

        List<Usuario> usuarios = repositoryUsuario.findAll();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuario", usuario);
        System.out.println("[AdminUsuarioController] Mostrando " + (usuarios == null ? 0 : usuarios.size()) + " usuarios para admin: " + usuario.getEmail());
        return "admin_users";
    }
}
