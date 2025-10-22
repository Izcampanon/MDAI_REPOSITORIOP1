// UsuarioTest.java
package com.example.version1.clases;

import com.example.version1.model.Usuario;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void testUsuarioCreation() {
        // Arrange (Preparación)
        Usuario usuario = new Usuario();

        // Act (Ejecución)
        usuario.setNombre("Juan Pérez");
        usuario.setEmail("juan@email.com");
        usuario.setContrasenia("password123");
        usuario.setEdad(true); // Mayor de edad
        usuario.setTelefono("123456789");

        // Assert (Verificación)
        assertEquals("Juan Pérez", usuario.getNombre());
        assertEquals("juan@email.com", usuario.getEmail());
        assertEquals("password123", usuario.getContrasenia());
        assertTrue(usuario.isEdad());
        assertEquals("123456789", usuario.getTelefono());
    }

    @Test
    void testUsuarioConstructorWithParameters() {
        // Arrange & Act
        Usuario usuario = new Usuario("María García", "maria@email.com",
                "pass456", true, "987654321");

        // Assert
        assertEquals("María García", usuario.getNombre());
        assertEquals("maria@email.com", usuario.getEmail());
        assertTrue(usuario.isEdad());
    }
}
