package com.example.version1.controller;

import com.example.version1.model.Usuario;
import com.example.version1.model.Compra_Entrada;
import com.example.version1.repository.RepositoryUsuario;
import com.example.version1.repository.RepositoryCompra_Entrada;
import com.example.version1.repository.RepositoryEntrada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminUsuarioController {

    private final RepositoryUsuario repositoryUsuario;
    private final RepositoryCompra_Entrada repositoryCompraEntrada;
    private final RepositoryEntrada repositoryEntrada;

    @Autowired
    public AdminUsuarioController(RepositoryUsuario repositoryUsuario, RepositoryCompra_Entrada repositoryCompraEntrada, RepositoryEntrada repositoryEntrada) {
        this.repositoryUsuario = repositoryUsuario;
        this.repositoryCompraEntrada = repositoryCompraEntrada;
        this.repositoryEntrada = repositoryEntrada;
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

    // Ver detalle de un usuario: datos, saldo y compras asociadas
    @GetMapping("/admin/user/{id}")
    public String verUsuario(@PathVariable("id") Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario admin = (Usuario) session.getAttribute("usuario");
        if (admin == null || !admin.isAdmin()) {
            model.addAttribute("error", "Debes ser administrador para acceder a esta página.");
            model.addAttribute("usuario", new Usuario());
            return "login";
        }

        Optional<Usuario> opt = repositoryUsuario.findById(id);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/admin/users";
        }
        Usuario u = opt.get();

        // Obtener compras asociadas (puede venir vacía)
        List<Compra_Entrada> compras = repositoryCompraEntrada.findByUsuarioId(u.getId());

        model.addAttribute("usuarioDetalle", u);
        model.addAttribute("compras", compras);
        model.addAttribute("usuario", admin);
        return "admin_user_detail";
    }

    // Mostrar formulario de edición
    @GetMapping("/admin/user/{id}/edit")
    public String editarUsuarioForm(@PathVariable("id") Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario admin = (Usuario) session.getAttribute("usuario");
        if (admin == null || !admin.isAdmin()) {
            model.addAttribute("error", "Debes ser administrador para acceder a esta página.");
            model.addAttribute("usuario", new Usuario());
            return "login";
        }

        Optional<Usuario> opt = repositoryUsuario.findById(id);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/admin/users";
        }

        model.addAttribute("usuarioEdit", opt.get());
        model.addAttribute("usuario", admin);
        return "admin_user_edit";
    }

    // Procesar edición
    @PostMapping("/admin/user/{id}/edit")
    public String editarUsuario(@PathVariable("id") Long id,
                                @RequestParam("nombre") String nombre,
                                @RequestParam("email") String email,
                                @RequestParam(value = "contrasenia", required = false) String contrasenia,
                                @RequestParam(value = "edad", required = false, defaultValue = "0") int edad,
                                @RequestParam(value = "telefono", required = false) String telefono,
                                @RequestParam(value = "admin", required = false) boolean esAdmin,
                                @RequestParam(value = "saldo", required = false, defaultValue = "0.0") float saldo,
                                HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario admin = (Usuario) session.getAttribute("usuario");
        if (admin == null || !admin.isAdmin()) {
            model.addAttribute("error", "Debes ser administrador para acceder a esta página.");
            model.addAttribute("usuario", new Usuario());
            return "login";
        }

        Optional<Usuario> opt = repositoryUsuario.findById(id);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/admin/users";
        }

        Usuario u = opt.get();

        // Comprobar si email está en uso por otro usuario
        if (email != null && !email.equalsIgnoreCase(u.getEmail()) && repositoryUsuario.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "El email ya está en uso por otro usuario.");
            return "redirect:/admin/user/" + id + "/edit";
        }

        u.setNombre(nombre);
        u.setEmail(email);
        if (contrasenia != null && !contrasenia.trim().isEmpty()) u.setContrasenia(contrasenia);
        u.setEdad(edad);
        u.setTelefono(telefono);
        u.setAdmin(esAdmin);
        u.setSaldo(saldo);

        repositoryUsuario.save(u);
        redirectAttributes.addFlashAttribute("success", "Usuario actualizado correctamente.");
        return "redirect:/admin/users";
    }

    // Eliminar usuario (POST)
    @PostMapping("/admin/user/{id}/delete")
    @Transactional
    public String eliminarUsuario(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        Usuario admin = (Usuario) session.getAttribute("usuario");
        if (admin == null || !admin.isAdmin()) {
            model.addAttribute("error", "Debes ser administrador para acceder a esta página.");
            model.addAttribute("usuario", new Usuario());
            return "login";
        }

        Optional<Usuario> opt = repositoryUsuario.findById(id);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/admin/users";
        }

        // Primero borrar entradas del usuario (tabla Entrada) para evitar FK constraint
        try {
            // Primero limpiar las filas de la tabla de unión y las compras del usuario
            repositoryCompraEntrada.deleteByUsuarioIdNative(id);

            // Después borrar las entradas asociadas directamente en la tabla Entrada
            try {
                int removedEntradas = repositoryEntrada.deleteByUsuarioIdNative(id);
                System.out.println("[AdminUsuarioController] Entradas eliminadas: " + removedEntradas);
            } catch (Exception ex) {
                System.out.println("[AdminUsuarioController] Error al borrar entradas del usuario (tabla Entrada): " + ex.getMessage());
            }
        } catch (Exception ex) {
            System.out.println("[AdminUsuarioController] Error al borrar compras/entradas del usuario: " + ex.getMessage());
        }

        // Luego borrar el usuario
        try {
            repositoryUsuario.deleteById(id);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el usuario: " + ex.getMessage());
            return "redirect:/admin/users";
        }

        redirectAttributes.addFlashAttribute("success", "Usuario eliminado correctamente.");
        return "redirect:/admin/users";
    }
}
