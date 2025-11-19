package com.example.version1.controller;

import com.example.version1.model.Usuario;
import com.example.version1.service.UsuarioService;
import com.example.version1.service.UsuarioServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {


    private UsuarioService usuarioService;

    public HomeController() {
        System.out.println("\t Builder of " + this.getClass().getSimpleName());

    }

    @GetMapping("/")
    public String index() {
        System.out.println("\t Recogo la peticion a / y devuelvo la vista myIndex.html");
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        String texto = "\t Recogo la peticion a /login y devuelvo la vista login.html";


        return "login";
    }



}
