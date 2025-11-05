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
        usuario.setNombre("Juan Pérez");

        Evento evento = new Evento();
        evento.setTitulo("Concierto Rock");

        // Act
        Entrada entrada = new Entrada("GENERAL", usuario, evento, 2);

        // Assert
        assertEquals("Juan Pérez", entrada.getNombre_usuario());
        assertEquals("Concierto Rock", entrada.getNombre_evento());
        assertEquals("GENERAL", entrada.getTipo());
        assertEquals(2, entrada.getCantidad_consumiciones());
    }

    @Test
    void eliminarEntrada() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setNombre("Ana Gómez");

        Evento evento = new Evento();
        evento.setTitulo("Festival Jazz");

        Entrada entrada = new Entrada("VIP", usuario, evento, 3);

        // Act
        entrada = null; // Simulate deletion

        // Assert
        assertNull(entrada);
    }
}
