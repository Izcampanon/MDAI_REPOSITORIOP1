package com.example.version1.controller;

import com.example.version1.model.Compra_Entrada;
import com.example.version1.model.Entrada;
import com.example.version1.model.Usuario;
import com.example.version1.service.GestionEntradasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class GestionEntradasController {

    private final GestionEntradasService gestionEntradasService;

    @Autowired
    public GestionEntradasController(GestionEntradasService gestionEntradasService) {
        this.gestionEntradasService = gestionEntradasService;
    }

    @GetMapping("/mis-entradas")
    public String verMisEntradas(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            model.addAttribute("error", "Debes iniciar sesión para ver tus entradas.");
            return "login";
        }

        List<Entrada> entradas = gestionEntradasService.obtenerEntradasPorUsuario(usuario.getId());

        // Separar en entradas disponibles (futuras y no devueltas), entradas anteriores (pasadas y no devueltas) y entradas devueltas
        List<Entrada> entradasDisponibles = new ArrayList<>();
        List<Entrada> entradasAnteriores = new ArrayList<>();
        List<Entrada> entradasDevueltas = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();

        if (entradas != null) {
            for (Entrada e : entradas) {
                try {
                    if (e.isDevuelta()) {
                        entradasDevueltas.add(e);
                        continue;
                    }
                    if (e.getEvento() != null && e.getEvento().getFecha() != null) {
                        if (e.getEvento().getFecha().isAfter(ahora) || e.getEvento().getFecha().isEqual(ahora)) {
                            entradasDisponibles.add(e);
                        } else {
                            entradasAnteriores.add(e);
                        }
                    } else {
                        // Si no hay evento o fecha, consideramos anterior
                        entradasAnteriores.add(e);
                    }
                } catch (Exception ex) {
                    // En caso de problemas por lazy loading u otros, clasificar como anterior
                    entradasAnteriores.add(e);
                }
            }
        }

        model.addAttribute("entradasDisponibles", entradasDisponibles);
        model.addAttribute("entradasAnteriores", entradasAnteriores);
        model.addAttribute("entradasDevueltas", entradasDevueltas);
        model.addAttribute("usuario", usuario);
        return "mis_entradas";
    }

    @GetMapping("/entrada/{id}")
    public String verEntradaDetalle(@PathVariable("id") Long id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            model.addAttribute("error", "Debes iniciar sesión para ver la entrada.");
            model.addAttribute("usuario", new Usuario());
            return "login";
        }

        Optional<Entrada> opt = gestionEntradasService.obtenerEntradaPorId(id);
        if (opt.isEmpty()) {
            model.addAttribute("error", "Entrada no encontrada.");
            return "redirect:/mis-entradas";
        }

        Entrada entrada = opt.get();

        // permisos: solo el propietario o admin pueden ver la compra asociada
        if (!usuario.isAdmin()) {
            if (entrada.getUsuario() == null || entrada.getUsuario().getId() == null || !entrada.getUsuario().getId().equals(usuario.getId())) {
                model.addAttribute("error", "No tienes permiso para ver esta entrada.");
                return "redirect:/mis-entradas";
            }
        }

        // Obtener la compra asociada a esta entrada
        Optional<Compra_Entrada> optCompra = gestionEntradasService.obtenerCompraPorEntradaId(id);
        if (optCompra.isEmpty()) {
            model.addAttribute("error", "No se encontró la información de compra asociada.");
            return "redirect:/mis-entradas";
        }

        Compra_Entrada compra = optCompra.get();
        model.addAttribute("compra", compra);
        model.addAttribute("entrada", entrada);
        model.addAttribute("usuario", usuario);
        return "entrada_detalle";
    }

    @PostMapping("/entrada/{id}/devolver")
    public String devolverEntrada(@PathVariable("id") Long id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            model.addAttribute("error", "Debes iniciar sesión para devolver la entrada.");
            model.addAttribute("usuario", new Usuario());
            return "login";
        }

        Optional<String> opt = gestionEntradasService.procesarDevolucion(id, usuario.getId());
        if (opt.isPresent() && !opt.get().isEmpty()) {
            model.addAttribute("error", opt.get());
            return "redirect:/entrada/" + id;
        }

        model.addAttribute("success", "Devolución realizada correctamente. Se reembolsó el importe.");
        return "redirect:/mis-entradas";
    }

}
