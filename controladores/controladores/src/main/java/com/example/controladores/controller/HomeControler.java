package com.example.controladores.controller; // Ajusta el paquete si no usaste subpaquete

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("izan")

// Mapea la ruta raíz "/"
@RequestMapping("/izan")
public class HomeControler {

    @GetMapping("/")
    public String index() {
        // Redirige a la vista estática que debe estar en src/main/resources/static/
        return "myIndex.html";
    }

    @GetMapping("/home")
    public String home() {
        // Redirige a la vista estática que debe estar en src/main/resources/static/
        return "<h1>Bienvenido a la página de inicio</h1>";
    }

    @GetMapping()
}
