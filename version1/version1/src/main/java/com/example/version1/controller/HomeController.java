package com.example.version1.controller;

import com.example.version1.model.Usuario;
import com.example.version1.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;

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

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("usuario", new Usuario());
        System.out.println("\t Petición a /login -> vista login");
        return "login";
    }

    @PostMapping("/login")
    public String loginPost(@ModelAttribute Usuario usuario, Model model) {
        // Usar validarCredenciales: solo necesitamos email y contrasenia para el login
        String email = usuario.getEmail();
        String contrasenia = usuario.getContrasenia();
        boolean valido = usuarioService.validarCredenciales(email, contrasenia);
        if (valido) {
            System.out.println("\t Usuario válido -> redirigir a /");
            return "redirect:/";
        } else {
            model.addAttribute("error", "Usuario o contraseña inválidos");
            System.out.println("\t Usuario inválido -> volver a login");
            return "login";
        }
    }



}
