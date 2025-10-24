// RepositoryUsuarioTest.java
package com.example.version1.repository;

import com.example.version1.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RepositoryUsuarioTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RepositoryUsuario usuarioRepository;

    @Test
    void whenFindById_thenReturnUsuario() {
        // Arrange
        Usuario usuario = new Usuario("Test User", "test@email.com",
                "password", true, "123456789");
        entityManager.persist(usuario);
        entityManager.flush();

        // Act
        Usuario found = usuarioRepository.findById(usuario.getId()).orElse(null);

        // Assert
        assertNotNull(found);
        assertEquals(usuario.getNombre(), found.getNombre());
        assertEquals(usuario.getEmail(), found.getEmail());
    }

    @Test
    void whenSaveUsuario_thenUsuarioIsSaved() {
        // Arrange
        Usuario usuario = new Usuario("New User", "new@email.com",
                "pass123", false, "987654321");

        // Act
        Usuario saved = usuarioRepository.save(usuario);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("New User", saved.getNombre());
        assertEquals("new@email.com", saved.getEmail());
    }
}