package com.example.version1.controller;

import com.example.version1.repository.RepositoryEvento;
import com.example.version1.service.RecomendacionAIService;
import com.example.version1.model.Evento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}

