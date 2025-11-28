package com.example.version1.service;

import com.example.version1.model.Evento;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecomendacionAIServiceTest {

    @Test
    void obtenerRecomendaciones_basic() {
        RecomendacionAIService service = new RecomendacionAIService();

        Evento e1 = new Evento();
        e1.setTitulo("Rock Night");
        e1.setArtista("Band A");
        e1.setDescripcion("Concierto de rock y fiesta");
        e1.setFecha(LocalDateTime.now().plusDays(5));
        e1.setAforo(300);
        e1.setEstado(true);

        Evento e2 = new Evento();
        e2.setTitulo("Pop Evening");
        e2.setArtista("Pop Singer");
        e2.setDescripcion("Concierto pop familiar");
        e2.setFecha(LocalDateTime.now().plusDays(40));
        e2.setAforo(150);
        e2.setEstado(true);

        Evento e3 = new Evento();
        e3.setTitulo("Jazz Afternoon");
        e3.setArtista("Jazz Trio");
        e3.setDescripcion("Tarde de jazz relajado");
        e3.setFecha(LocalDateTime.now().plusDays(10));
        e3.setAforo(80);
        e3.setEstado(false);

        List<Evento> eventos = List.of(e1, e2, e3);

        String res = service.obtenerRecomendaciones(eventos, "rock");
        assertNotNull(res);
        assertTrue(res.toLowerCase().contains("rock") || res.contains("Rock Night"));
    }
}

