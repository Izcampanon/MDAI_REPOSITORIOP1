// EntradaTest.java
package com.example.version1.clases;

import com.example.version1.model.Entrada;
import com.example.version1.model.Evento;
import com.example.version1.model.Usuario;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EntradaTest {

    @Test
    void testEntradaCreation() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setNombre("Test User");

        Evento evento = new Evento();
        evento.setTitulo("Test Event");

        // Act
        Entrada entrada = new Entrada();
        entrada.setNombre_evento("Concierto VIP");
        entrada.setNombre_usuario("Carlos López");
        entrada.setConsumiciones(true);
        entrada.setTipo("VIP");
        entrada.setUsuario(usuario);
        entrada.setEvento(evento);

        // Assert
        assertEquals("Concierto VIP", entrada.getNombre_evento());
        assertEquals("Carlos López", entrada.getNombre_usuario());
        assertTrue(entrada.isConsumiciones());
        assertEquals("VIP", entrada.getTipo());
        assertEquals(usuario, entrada.getUsuario());
        assertEquals(evento, entrada.getEvento());
    }
}
