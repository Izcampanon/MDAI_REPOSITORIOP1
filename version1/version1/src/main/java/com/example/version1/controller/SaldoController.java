package com.example.version1.controller;

import com.example.version1.model.Usuario;
import com.example.version1.repository.RepositoryUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class SaldoController {

    private final RepositoryUsuario repositoryUsuario;

    @Autowired
    public SaldoController(RepositoryUsuario repositoryUsuario) {
        this.repositoryUsuario = repositoryUsuario;
    }

    @GetMapping("/saldo")
    public String verSaldo(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            model.addAttribute("error", "Debes iniciar sesión para ver tu saldo.");
            model.addAttribute("usuario", new Usuario());
            return "login";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("cantidad", "");
        return "saldo";
    }

    @PostMapping("/saldo/add")
    public String añadirSaldo(@RequestParam("cantidad") String cantidadStr, Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            model.addAttribute("error", "Debes iniciar sesión para añadir saldo.");
            model.addAttribute("usuario", new Usuario());
            return "login";
        }

        if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
            model.addAttribute("error", "Introduce una cantidad válida.");
            model.addAttribute("usuario", usuario);
            model.addAttribute("cantidad", "");
            return "saldo";
        }

        // Aceptar coma o punto como separador decimal
        cantidadStr = cantidadStr.replace(',', '.').trim();
        float cantidad;
        try {
            cantidad = Float.parseFloat(cantidadStr);
        } catch (NumberFormatException e) {
            model.addAttribute("error", "Cantidad inválida. Usa un número, por ejemplo 10 o 5.50.");
            model.addAttribute("usuario", usuario);
            model.addAttribute("cantidad", cantidadStr);
            return "saldo";
        }

        if (cantidad <= 0f) {
            model.addAttribute("error", "La cantidad debe ser mayor que 0.");
            model.addAttribute("usuario", usuario);
            model.addAttribute("cantidad", cantidadStr);
            return "saldo";
        }

        // Sumar al saldo y persistir
        float nuevoSaldo = usuario.getSaldo() + cantidad;
        usuario.setSaldo(nuevoSaldo);
        try {
            repositoryUsuario.save(usuario);
            // actualizar sesión
            session.setAttribute("usuario", usuario);
            model.addAttribute("success", "Saldo añadido correctamente. Nuevo saldo: " + String.format("%.2f", nuevoSaldo) + " €");
            model.addAttribute("usuario", usuario);
            model.addAttribute("cantidad", "");
            return "saldo";
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar el saldo: " + e.getMessage());
            model.addAttribute("usuario", usuario);
            model.addAttribute("cantidad", cantidadStr);
            return "saldo";
        }



    }
}

