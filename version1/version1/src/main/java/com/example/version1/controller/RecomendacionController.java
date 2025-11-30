package com.example.version1.controller;

import com.example.version1.repository.RepositoryEvento;
import com.example.version1.service.RecomendacionAIService;
import com.example.version1.model.Evento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class RecomendacionController {

    private final RecomendacionAIService recomendacionAIService;
    private final RepositoryEvento repositoryEvento;

    @Autowired
    public RecomendacionController(RecomendacionAIService recomendacionAIService, RepositoryEvento repositoryEvento) {
        this.recomendacionAIService = recomendacionAIService;
        this.repositoryEvento = repositoryEvento;
    }

    public static record RecomendacionRequest(String preferencias) {}

    @PostMapping(path = "/recomendaciones", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, String>> recomendaciones(@RequestBody RecomendacionRequest request) {
        try {
            List<Evento> eventos = repositoryEvento.findAll();
            String texto = recomendacionAIService.obtenerRecomendaciones(eventos, request == null ? null : request.preferencias());
            Map<String, String> resp = new HashMap<>();
            resp.put("recomendaciones", texto);
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Error al generar recomendaciones: " + ex.getMessage());
            return ResponseEntity.status(500).body(err);
        }
    }

    // Endpoint GET que devuelve una lista JSON de recomendaciones para el frontend
    @GetMapping(path = "/api/recomendaciones", produces = "application/json")
    public ResponseEntity<List<Map<String, String>>> recomendacionesParaFrontend(@RequestParam(name = "max", required = false, defaultValue = "3") int max) {
        try {
            List<Evento> eventos = repositoryEvento.findAll();
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
            List<Map<String, String>> lista = eventos.stream().limit(max).map(e -> {
                Map<String, String> m = new HashMap<>();
                m.put("title", e.getTitulo() != null ? e.getTitulo() : "(sin título)");
                try { m.put("when", e.getFecha() != null ? e.getFecha().format(fmt) : ""); } catch (Exception ex) { m.put("when", ""); }
                m.put("location", (e.getLocal() != null && e.getLocal().getNombre() != null) ? e.getLocal().getNombre() : "");
                m.put("id", e.getId() != null ? String.valueOf(e.getId()) : "");
                return m;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(lista);
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(java.util.Collections.emptyList());
        }
    }

    // Nuevo endpoint: usa el servicio para generar un texto de recomendaciones y además devuelve eventos relacionados
    @GetMapping(path = "/api/recomendaciones/search", produces = "application/json")
    public ResponseEntity<Map<String, Object>> searchByKeywordUsingService(@RequestParam(name = "q") String q,
                                                                           @RequestParam(name = "max", required = false, defaultValue = "10") int max) {
        try {
            Map<String, Object> resp = new HashMap<>();
            if (q == null || q.trim().isEmpty()) {
                resp.put("recomendaciones", "");
                resp.put("eventos", java.util.Collections.emptyList());
                return ResponseEntity.ok(resp);
            }
            String kw = q.trim().toLowerCase(Locale.ROOT);
            List<Evento> eventos = repositoryEvento.findAll();

            // Texto del servicio (usa la lógica ya implementada)
            String texto = recomendacionAIService.obtenerRecomendaciones(eventos, q);
            resp.put("recomendaciones", texto);

            // Lista de eventos relacionados: buscar en título, artista o descripcion
            List<Map<String, String>> matched = eventos.stream()
                    .filter(e -> {
                        String text = (e.getTitulo() == null ? "" : e.getTitulo()) + " " + (e.getArtista() == null ? "" : e.getArtista()) + " " + (e.getDescripcion() == null ? "" : e.getDescripcion());
                        return text.toLowerCase(Locale.ROOT).contains(kw);
                    })
                    .limit(max)
                    .map(e -> {
                        Map<String, String> m = new HashMap<>();
                        m.put("title", e.getTitulo() != null ? e.getTitulo() : "(sin título)");
                        try { m.put("when", e.getFecha() != null ? e.getFecha().toLocalDate().toString() : ""); } catch (Exception ex) { m.put("when", ""); }
                        m.put("location", (e.getLocal() != null && e.getLocal().getNombre() != null) ? e.getLocal().getNombre() : "");
                        m.put("id", e.getId() != null ? String.valueOf(e.getId()) : "");
                        return m;
                    })
                    .collect(Collectors.toList());

            resp.put("eventos", matched);
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(java.util.Map.of("recomendaciones", "", "eventos", java.util.Collections.emptyList()));
        }
    }

}
