package com.example.version1.controller;

import com.example.version1.model.Usuario;
import com.example.version1.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {


    private final UsuarioService usuarioService;

    @Autowired
    public HomeController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
        System.out.println("\t Constructor of " + this.getClass().getSimpleName());

    }

    @GetMapping("/")
    public String index() {
        System.out.println("\t Recogo la peticion a / y devuelvo la vista myIndex.html");
        return "index";
    }

    // iniciar sesion
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("usuario", new Usuario());
        System.out.println("\t Petición a /login -> vista login");
        return "login";
    }

    @PostMapping("/login")
    public String loginPost(@ModelAttribute Usuario usuario, Model model, HttpSession session) {
        // Autenticar y guardar usuario en sesión
        String email = usuario.getEmail();
        String contrasenia = usuario.getContrasenia();
        Usuario autenticado = usuarioService.autenticar(email, contrasenia);
        if (autenticado != null) {
            session.setAttribute("usuario", autenticado);
            System.out.println("\t Usuario autenticado -> redirigir");
            if (autenticado.isAdmin()) {
                return "redirect:/admin";
            }
            return "redirect:/";
        } else {
            model.addAttribute("error", "Usuario o contraseña inválidos");
            System.out.println("\t Usuario inválido -> volver a login");
            return "login";
        }
    }

    // Registro
    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registroSubmit(@ModelAttribute Usuario usuario, Model model) {
        // validar campos mínimos con el servicio
        boolean valido = usuarioService.validarUsuario(usuario);
        if (!valido) {
            model.addAttribute("error", "Datos inválidos o incompletos. Revise los campos.");
            model.addAttribute("usuario", usuario);
            return "registro";
        }

        try {
            usuarioService.crearUsuario(usuario);
            model.addAttribute("success", "Registro correcto. Ya puedes iniciar sesión.");
            return "login"; // redirigir al login mostrando mensaje
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear el usuario: " + e.getMessage());
            model.addAttribute("usuario", usuario);
            return "registro";
        }
    }

    // Nuevo: ruta para iniciar flujo de compra (redirige al formulario de búsqueda de ubicación)
    @GetMapping("/comprar")
    public String comprar(HttpSession session, Model model) {
        Usuario u = (Usuario) session.getAttribute("usuario");
        if (u == null) {
            model.addAttribute("error", "Debes iniciar sesión para comprar entradas.");
            return "login";
        }
        return "redirect:/compra/ubicacion";
    }

}
