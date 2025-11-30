package com.example.version1.service;

import com.example.version1.model.Evento;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class RecomendacionAIService {

    public String obtenerRecomendaciones(List<Evento> eventosDisponibles, String preferenciasUsuario) {

        if (eventosDisponibles == null || eventosDisponibles.isEmpty()) {
            return "No hay eventos disponibles para recomendar.";
        }

        String prefs = (preferenciasUsuario == null) ? "" : preferenciasUsuario.toLowerCase(Locale.ROOT);

        // Calcular una puntuación simple para ordenar eventos
        List<Evento> ordenados = eventosDisponibles.stream()
                .sorted(Comparator.comparingDouble((Evento e) -> -scoreEvento(e, prefs)))
                .collect(Collectors.toList());

        int max = Math.min(3, ordenados.size());
        StringBuilder sb = new StringBuilder();
        sb.append("Te recomiendo los siguientes ").append(max).append(" eventos:\n\n");

        for (int i = 0; i < max; i++) {
            Evento e = ordenados.get(i);
            sb.append(i + 1).append(". ").append(e.getTitulo() != null ? e.getTitulo() : "(sin título)");
            if (e.getArtista() != null && !e.getArtista().isBlank()) sb.append(" — ").append(e.getArtista());
            sb.append("\n");

            // Razón: coincidencia de preferencias y proximidad
            int razones = 0;
            if (!prefs.isBlank()) {
                String texto = (safe(e.getTitulo()) + " " + safe(e.getArtista()) + " " + safe(e.getDescripcion())).toLowerCase(Locale.ROOT);
                if (prefs.chars().anyMatch(ch -> Character.isLetter(ch))) {
                    // contar palabras clave simples
                    String[] palabras = prefs.split("\\s+");
                    int matches = 0;
                    for (String p : palabras) {
                        if (p.length() < 2) continue;
                        if (texto.contains(p)) matches++;
                    }
                    if (matches > 0) {
                        sb.append("   • Coincide con tus preferencias (").append(matches).append(" palabra(s) clave).");
                        razones++;
                    }
                }
            }

            // Proximidad en fecha
            try {
                if (e.getFecha() != null) {
                    long dias = Duration.between(LocalDateTime.now(), e.getFecha()).toDays();
                    if (dias >= 0 && dias <= 30) {
                        if (razones > 0) sb.append("\n");
                        sb.append("   • Fecha próxima (en los próximos ").append(Math.max(1, dias)).append(" días).");
                        razones++;
                    }
                }
            } catch (Exception ex) {
                // ignorar
            }

            // Disponibilidad
            if (Boolean.TRUE.equals(e.getEstado())) {
                if (razones > 0) sb.append("\n");
                sb.append("   • Evento disponible para la compra.");
                razones++;
            }

            if (razones == 0) {
                sb.append("   • Tiene buenas críticas o es relevante según la programación.");
            }

            sb.append("\n\n");
        }

        sb.append("Si quieres, puedo afinar las recomendaciones si me das más detalles (género, rango de fechas, horario preferido, precio máximo, etc.).");
        return sb.toString();
    }


    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static double scoreEvento(Evento e, String prefs) {
        double score = 0.0;
        String texto = (safe(e.getTitulo()) + " " + safe(e.getArtista()) + " " + safe(e.getDescripcion())).toLowerCase(Locale.ROOT);
        if (prefs != null && !prefs.isBlank()) {
            String[] palabras = prefs.split("\\s+");
            for (String p : palabras) {
                if (p.length() < 2) continue;
                if (texto.contains(p)) score += 10.0;
            }
        }
        // proximidad en fecha: eventos próximos suman más
        try {
            if (e.getFecha() != null) {
                long dias = Duration.between(LocalDateTime.now(), e.getFecha()).toDays();
                if (dias >= 0) score += Math.max(0, 30 - dias) * 0.2; // hasta +6
                else score -= 5; // eventos pasados penalizan
            }
        } catch (Exception ex) { }

        if (Boolean.TRUE.equals(e.getEstado())) score += 2.0;
        // mayor aforo puede indicar evento grande
        try { score += Math.min(5, Math.max(0, e.getAforo() / 100.0)); } catch (Exception ignored) {}
        return score;
    }

}