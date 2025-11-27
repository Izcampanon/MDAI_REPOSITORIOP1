package com.example.version1.controller;

import com.example.version1.model.Evento;
import com.example.version1.model.Local;
import com.example.version1.model.Usuario;
import com.example.version1.service.CompraEntradaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class CompraEntradaController {

    private final CompraEntradaService compraEntradaService;

    @Autowired
    public CompraEntradaController(CompraEntradaService compraEntradaService) {
        this.compraEntradaService = compraEntradaService;
    }

    private boolean estaLogeado(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuario");
        return u != null;
    }

    // Mostrar formulario de búsqueda (público)
    @GetMapping({"/compra", "/compra/ubicacion"})
    public String compraUbicacionForm(Model model, HttpSession session) {
        model.addAttribute("nombreUbicacion", "");
        if (estaLogeado(session)) model.addAttribute("usuario", session.getAttribute("usuario"));
        return "compra_entrada_form";
    }

    // Procesar búsqueda y listar locales (público)
    @PostMapping("/compra/ubicacion/search")
    public String buscarLocalesPorUbicacion(@RequestParam("nombreUbicacion") String nombreUbicacion, Model model, HttpSession session) {
        List<Local> locales = compraEntradaService.buscarLocalesPorUbicacion(nombreUbicacion == null ? "" : nombreUbicacion.trim());
        model.addAttribute("locales", locales);
        model.addAttribute("nombreUbicacion", nombreUbicacion);
        if (locales == null || locales.isEmpty()) model.addAttribute("info", "No se encontraron locales para la ubicación especificada.");
        if (estaLogeado(session)) model.addAttribute("usuario", session.getAttribute("usuario"));
        return "compra_entrada_form";
    }

    // Mostrar eventos de un local seleccionado (público)
    @GetMapping("/compra/local/{id}")
    public String mostrarEventosLocal(@PathVariable("id") String localIdRaw,
                                      @RequestParam(value = "nombreUbicacion", required = false) String nombreUbicacion,
                                      Model model, HttpSession session) {
        if (localIdRaw == null || localIdRaw.isBlank()) {
            model.addAttribute("error", "No se indicó el identificador del local.");
            return "compra_entrada_form";
        }
        // El navegador puede insertar ;jsessionid=... en la ruta. Extraemos sólo la parte numérica antes del ';'
        String localIdStr = localIdRaw.split(";")[0];
        Long localId = null;
        try {
            localId = Long.valueOf(localIdStr);
        } catch (NumberFormatException nfe) {
            model.addAttribute("error", "Identificador de local inválido.");
            return "compra_entrada_form";
        }
        if (estaLogeado(session)) model.addAttribute("usuario", session.getAttribute("usuario"));

        try {
            Local local = compraEntradaService.obtenerLocalConEventos(localId);
            if (local == null) {
                model.addAttribute("info", "No se encontró el local seleccionado.");
            } else {
                model.addAttribute("localSeleccionado", local);
                model.addAttribute("eventos", local.getEventos());
            }
        } catch (Exception ex) {
            System.out.println("[CompraEntradaController] Error al obtener eventos para localId=" + localId + " -> " + ex.getMessage());
            model.addAttribute("error", "Se produjo un error al cargar los eventos. Intenta de nuevo más tarde.");
        }

        if (nombreUbicacion != null && !nombreUbicacion.trim().isEmpty()) {
            List<Local> locales = compraEntradaService.buscarLocalesPorUbicacion(nombreUbicacion);
            model.addAttribute("locales", locales);
            model.addAttribute("nombreUbicacion", nombreUbicacion);
        }

        return "compra_entrada_form";
    }

    // Seleccionar evento (requiere login)
    @PostMapping("/compra/evento/select")
    public String seleccionarEvento(@RequestParam("eventoId") Long eventoId,
                                    @RequestParam(value = "nombreUbicacion", required = false) String nombreUbicacion,
                                    Model model, HttpSession session) {
        if (!estaLogeado(session)) {
            model.addAttribute("error", "Debes iniciar sesión para comprar entradas.");
            model.addAttribute("usuario", new Usuario());
            return "login";
        }
        var evento = compraEntradaService.obtenerEvento(eventoId);
        if (evento == null) {
            model.addAttribute("info", "Evento no encontrado.");
            return "compra_entrada_form";
        }
        model.addAttribute("eventoSeleccionado", evento);
        model.addAttribute("nombreUbicacion", nombreUbicacion);
        model.addAttribute("entradaTipo", "GENERAL");
        model.addAttribute("cantidadConsumiciones", 1);
        return "entrada_form";
    }

    // POST desde entrada_form: validar entrada y redirigir a la página de pago (GET /compra/pago)
    @PostMapping("/compra/entrada/confirm")
    public String confirmarEntradaYMostrarPago(@RequestParam("eventoId") Long eventoId,
                                               @RequestParam(value = "tipo", required = false, defaultValue = "GENERAL") String tipo,
                                               @RequestParam(value = "cantidadConsumiciones", required = false, defaultValue = "1") int cantidadConsumiciones,
                                               @RequestParam(value = "nombreUbicacion", required = false) String nombreUbicacion,
                                               Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Debe estar logeado
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para continuar con la compra.");
            return "redirect:/login";
        }

        Evento evento = compraEntradaService.obtenerEvento(eventoId);
        if (evento == null) {
            redirectAttributes.addFlashAttribute("error", "Evento no encontrado.");
            return "redirect:/compra";
        }

        var validacion = compraEntradaService.validarEntrada(tipo, cantidadConsumiciones, evento, usuario);
        if (validacion.isPresent()) {
            redirectAttributes.addFlashAttribute("error", validacion.get());
            redirectAttributes.addAttribute("nombreUbicacion", nombreUbicacion);
            return "redirect:/compra/local/" + (evento.getLocal() != null && evento.getLocal().getId() != null ? evento.getLocal().getId() : "");
        }

        // Todo ok: redirigimos a la página de pago con parámetros en query string
        redirectAttributes.addAttribute("eventoId", eventoId);
        redirectAttributes.addAttribute("tipo", tipo);
        redirectAttributes.addAttribute("cantidadConsumiciones", cantidadConsumiciones);
        redirectAttributes.addAttribute("nombreUbicacion", nombreUbicacion);
        return "redirect:/compra/pago";
    }

    // GET: mostrar la página de pago (pago.html)
    @GetMapping("/compra/pago")
    public String mostrarPago(@RequestParam("eventoId") Long eventoId,
                              @RequestParam(value = "tipo", required = false, defaultValue = "GENERAL") String tipo,
                              @RequestParam(value = "cantidadConsumiciones", required = false, defaultValue = "1") int cantidadConsumiciones,
                              @RequestParam(value = "nombreUbicacion", required = false) String nombreUbicacion,
                              Model model,
                              HttpSession session) {

        Evento evento = compraEntradaService.obtenerEvento(eventoId);
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (evento == null) {
            model.addAttribute("error", "Evento no encontrado.");
            return "compra_entrada_form";
        }

        float precioUnitario = "VIP".equalsIgnoreCase(tipo) ? evento.getPrecioVip() : evento.getPrecioGeneral();
        float precioConsumicion = evento.getPrecioConsumicion();
        float precioTotal = precioUnitario + (cantidadConsumiciones * precioConsumicion);

        model.addAttribute("eventoSeleccionado", evento);
        model.addAttribute("usuario", usuario);
        model.addAttribute("tipo", tipo);
        model.addAttribute("cantidadConsumiciones", cantidadConsumiciones);
        model.addAttribute("nombreUbicacion", nombreUbicacion);
        model.addAttribute("precioUnitario", precioUnitario);
        model.addAttribute("precioConsumicion", precioConsumicion);
        model.addAttribute("precioTotal", precioTotal);

        return "pago"; // Thymeleaf template pago.html
    }

    // POST: procesar el pago y redirigir a "mis entradas"
    @PostMapping("/compra/entrada/pagar")
    public String procesarPago(@RequestParam("eventoId") Long eventoId,
                                @RequestParam(value = "tipo", required = false, defaultValue = "GENERAL") String tipo,
                                @RequestParam(value = "cantidadConsumiciones", required = false, defaultValue = "1") int cantidadConsumiciones,
                                @RequestParam(value = "nombreUbicacion", required = false) String nombreUbicacion,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para procesar el pago.");
            return "redirect:/login";
        }

        Evento evento = compraEntradaService.obtenerEvento(eventoId);
        if (evento == null) {
            redirectAttributes.addFlashAttribute("error", "Evento no encontrado.");
            return "redirect:/compra";
        }

        var resultado = compraEntradaService.procesarPago(evento, tipo, cantidadConsumiciones, usuario);
        if (resultado.isPresent()) {
            // error al procesar (saldo insuficiente, etc.) -> volver a la vista de pago con mensaje
            redirectAttributes.addFlashAttribute("error", resultado.get());
            redirectAttributes.addAttribute("eventoId", eventoId);
            redirectAttributes.addAttribute("tipo", tipo);
            redirectAttributes.addAttribute("cantidadConsumiciones", cantidadConsumiciones);
            redirectAttributes.addAttribute("nombreUbicacion", nombreUbicacion);
            return "redirect:/compra/pago";
        }

        // Compra realizada
        redirectAttributes.addFlashAttribute("info", "Compra realizada correctamente.");
        return "redirect:/mis-entradas";
    }
}
