// EventoTest.java
package com.example.version1.clases;

import com.example.version1.model.Evento;
import com.example.version1.model.Local;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class EventoTest {

    @Test
    void testEventoCreation() {
        // Arrange
        Local local = new Local(1L, "Sala Razzmatazz");
        LocalDateTime fecha = LocalDateTime.of(2024, 12, 25, 21, 0);

        // Act
        Evento evento = new Evento();
        evento.setTitulo("Concierto Rock");
        evento.setFecha(fecha);
        evento.setArtista("Los Rockeros");
        evento.setUbicacion("Barcelona");
        evento.setDescripcion("Gran concierto de rock");
        evento.setAforo(1000);
        evento.setEstado("disponible");
        evento.setTipo("+18");
        evento.setEstado_aforo(false); // Aforo incompleto
        evento.setLocal(local);

        // Assert
        assertEquals("Concierto Rock", evento.getTitulo());
        assertEquals(fecha, evento.getFecha());
        assertEquals("Los Rockeros", evento.getArtista());
        assertEquals(1000, evento.getAforo());
        assertEquals("disponible", evento.getEstado());
        assertFalse(evento.isEstado_aforo());
        assertEquals(local, evento.getLocal());
    }
}
