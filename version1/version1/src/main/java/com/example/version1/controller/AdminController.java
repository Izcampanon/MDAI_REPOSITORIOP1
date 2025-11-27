package com.example.version1.controller;

import com.example.version1.model.Evento;
import com.example.version1.model.Local;
import com.example.version1.model.Ubicacion;
import com.example.version1.model.Usuario;
import com.example.version1.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    private boolean verificarAdmin(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuario");
        return u != null && u.isAdmin();
    }

    @GetMapping("/admin")
    public String adminIndex(Model model, HttpSession session) {
        if (!verificarAdmin(session)) {
            model.addAttribute("error", "Acceso no autorizado. Inicia sesión como admin.");
            return "login";
        }
        return "admin_index";
    }

    // Ubicacion
    @GetMapping("/admin/ubicacion/new")
    public String newUbicacionForm(Model model, HttpSession session) {
        if (!verificarAdmin(session)) {
            model.addAttribute("error", "Acceso no autorizado. Inicia sesión como admin.");
            return "login";
        }
        model.addAttribute("ubicacion", new Ubicacion());
        return "ubicacion_form";
    }

    @PostMapping("/admin/ubicacion/new")
    public String createUbicacion(@ModelAttribute Ubicacion ubicacion, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!verificarAdmin(session)) {
            model.addAttribute("error", "Acceso no autorizado. Inicia sesión como admin.");
            return "login";
        }
        var opt = adminService.validarUbicacion(ubicacion);
        if (opt.isPresent()) {
            model.addAttribute("error", opt.get());
            model.addAttribute("ubicacion", ubicacion);
            return "ubicacion_form";
        }
        // Guardar y redirigir al índice del admin (el administrador decidirá crear Local desde allí)
        Ubicacion saved = adminService.crearUbicacion(ubicacion);
        redirectAttributes.addFlashAttribute("success", "Ubicación creada correctamente");
        return "redirect:/admin";
    }

    // Local
    @GetMapping("/admin/local/new")
    public String newLocalForm(Model model, HttpSession session, @RequestParam(value = "ubicacionId", required = false) Long ubicacionId) {
        if (!verificarAdmin(session)) {
            model.addAttribute("error", "Acceso no autorizado. Inicia sesión como admin.");
            return "login";
        }
        model.addAttribute("local", new Local());
        List<Ubicacion> ubicaciones = adminService.listarUbicaciones();
        model.addAttribute("ubicaciones", ubicaciones);
        // pasar el id seleccionado (si existe) para preseleccionar en la plantilla
        model.addAttribute("selectedUbicacionId", ubicacionId);
        return "local_form";
    }

    @PostMapping("/admin/local/new")
    public String createLocal(@ModelAttribute Local local, @RequestParam(value = "ubicacionId", required = false) Long ubicacionId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!verificarAdmin(session)) {
            model.addAttribute("error", "Acceso no autorizado. Inicia sesión como admin.");
            return "login";
        }
        var opt = adminService.validarLocal(local, ubicacionId);
        if (opt.isPresent()) {
            model.addAttribute("error", opt.get());
            model.addAttribute("local", local);
            List<Ubicacion> ubicaciones = adminService.listarUbicaciones();
            model.addAttribute("ubicaciones", ubicaciones);
            return "local_form";
        }
        adminService.crearLocal(local, ubicacionId);
        redirectAttributes.addFlashAttribute("success", "Local creado correctamente");
        return "redirect:/admin";
    }

    // Evento
    @GetMapping("/admin/evento/new")
    public String newEventoForm(Model model, HttpSession session) {
        if (!verificarAdmin(session)) {
            model.addAttribute("error", "Acceso no autorizado. Inicia sesión como admin.");
            return "login";
        }
        model.addAttribute("evento", new Evento());
        List<Local> locales = adminService.listarLocales();
        model.addAttribute("locales", locales);
        return "evento_form";
    }

    @PostMapping("/admin/evento/new")
    public String createEvento(@ModelAttribute Evento evento, @RequestParam(value = "localId", required = false) Long localId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!verificarAdmin(session)) {
            model.addAttribute("error", "Acceso no autorizado. Inicia sesión como admin.");
            return "login";
        }
        var opt = adminService.validarEvento(evento, localId);
        if (opt.isPresent()) {
            model.addAttribute("error", opt.get());
            model.addAttribute("evento", evento);
            List<Local> locales = adminService.listarLocales();
            model.addAttribute("locales", locales);
            return "evento_form";
        }
        adminService.crearEvento(evento, localId);
        redirectAttributes.addFlashAttribute("success", "Evento creado correctamente");
        return "redirect:/admin";
    }

}
