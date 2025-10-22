// java
package com.example.version1.clases;

import com.example.version1.model.Entrada;
import com.example.version1.repository.RepositoryEntrada;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EntradaRepositoryTest {

    @Autowired
    private RepositoryEntrada repositoryEntrada;

    @Test
    void saveAndFindById_shouldPersistAndRetrieve() {
        Entrada entrada = new Entrada();
        // Ajustar los setters según tu entidad real
        entrada.setNombre_evento("Prueba");
        entrada.setNombre_usuario("Contenido de prueba");

        Entrada saved = repositoryEntrada.save(entrada);
        Optional<Entrada> found = repositoryEntrada.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Prueba", found.get().getNombre_evento());
        assertEquals("Contenido de prueba", found.get().getNombre_usuario());
    }
}
