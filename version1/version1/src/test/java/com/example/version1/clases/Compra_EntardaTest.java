package com.example.version1.clases;

import com.example.version1.model.Compra_Entrada;
import com.example.version1.model.Entrada;
import com.example.version1.model.Evento;
import com.example.version1.model.Usuario;
import com.example.version1.repository.RepositoryCompra_Entrada;
import com.example.version1.repository.RepositoryEntrada;
import com.example.version1.repository.RepositoryEvento;
import com.example.version1.repository.RepositoryUsuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class Compra_EntardaTest {

    @Autowired
    private RepositoryCompra_Entrada compraRepository;

    @Autowired
    private RepositoryEntrada entradaRepository;

    @Autowired
    private RepositoryUsuario usuarioRepository;

    @Autowired
    private RepositoryEvento eventoRepository;

    @Test
    void crearCompra_Entrada() {
        // 1. Crear usuario y persistir
        Usuario usuario = new Usuario("Juan", "juan@test.com", "pwd", true, "600000000");
        usuario = usuarioRepository.save(usuario);
        assertNotNull(usuario.getId(), "Usuario debe tener id tras persistir");

        // 2. Crear un evento mínimo y persistir (las entradas necesitan referencia a evento)
        Evento evento = new Evento("Concierto", LocalDateTime.now().plusDays(10), "Banda", "Ciudad", "Desc", 300, "disponible", "+18", false, null);
        evento = eventoRepository.save(evento);
        assertNotNull(evento.getId(), "Evento debe tener id tras persistir");

        // 3. Crear entradas asociadas al usuario y al evento (no es necesario persistir entradas por separado)
        Entrada entrada1 = new Entrada("Concierto", usuario.getNombre(), false, "GENERAL", usuario, evento);
        Entrada entrada2 = new Entrada("Concierto", usuario.getNombre(), true, "VIP", usuario, evento);

        // 4. Crear la compra con las entradas y asociarla al usuario
        Compra_Entrada compra = new Compra_Entrada(new Date(), 59.99f, new ArrayList<>(Arrays.asList(entrada1, entrada2)), usuario);

        // 5. Persistir la compra (cascade debe persistir las entradas)
        compra = compraRepository.save(compra);

        // 6. Verificaciones básicas
        assertNotNull(compra.getId(), "La compra debe tener id tras persistir");
        assertEquals(2, compra.getTipo_entradas().size(), "La compra debe contener 2 entradas");
        assertEquals(usuario.getId(), compra.getUsuario().getId(), "La compra debe estar asociada al usuario correcto");

        // 7. Comprobar que las entradas han sido persistidas en la tabla de entradas
        assertTrue(entradaRepository.findAll().stream().anyMatch(e -> e.getNombre_evento().equals("Concierto") && e.getTipo().equals("GENERAL")));
        assertTrue(entradaRepository.findAll().stream().anyMatch(e -> e.getNombre_evento().equals("Concierto") && e.getTipo().equals("VIP")));

        // 8. Recuperar la compra desde el repositorio y verificar relaciones
        Compra_Entrada compraRecuperada = compraRepository.findById(compra.getId()).orElse(null);
        assertNotNull(compraRecuperada, "La compra debe recuperarse desde el repositorio");
        assertEquals(2, compraRecuperada.getTipo_entradas().size(), "La compra recuperada debe contener 2 entradas");
        assertEquals(usuario.getId(), compraRecuperada.getUsuario().getId(), "La compra recuperada debe estar asociada al mismo usuario");
    }

    @Test
    void actualizarPrecioCompra_Entrada() {     // Preparar datos: usuario, evento, entradas y compra
        Usuario usuario = new Usuario("Ana", "ana@test.com", "pwd", true, "611111111");
        usuario = usuarioRepository.save(usuario);

        Evento evento = new Evento("Show", LocalDateTime.now().plusDays(5), "Artist", "City", "Desc", 100, "disponible", "+18", false, null);
        evento = eventoRepository.save(evento);

        Entrada ent1 = new Entrada("Show", usuario.getNombre(), false, "GENERAL", usuario, evento);

        Compra_Entrada compra = new Compra_Entrada(new Date(), 30.0f, new ArrayList<>(Arrays.asList(ent1)), usuario);
        compra = compraRepository.save(compra);

        // Verificar precio inicial
        Compra_Entrada saved = compraRepository.findById(compra.getId()).orElseThrow();
        assertEquals(30.0f, saved.getPrecio(), 0.001f, "Precio inicial debe ser 30.0");

        // Actualizar precio
        saved.setPrecio(45.5f);
        compraRepository.save(saved);

        // Recuperar y verificar cambio
        Compra_Entrada updated = compraRepository.findById(compra.getId()).orElseThrow();
        assertEquals(45.5f, updated.getPrecio(), 0.001f, "El precio debe actualizarse a 45.5");

        // Asegurar que las entradas siguen asociadas
        assertNotNull(updated.getTipo_entradas());
        assertEquals(1, updated.getTipo_entradas().size());
        assertEquals("Show", updated.getTipo_entradas().get(0).getNombre_evento());
    }


}
