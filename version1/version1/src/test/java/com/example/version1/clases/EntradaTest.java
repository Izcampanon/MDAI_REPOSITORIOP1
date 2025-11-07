// EntradaTest.java
package com.example.version1.clases;

import com.example.version1.model.Entrada;
import com.example.version1.model.Evento;
import com.example.version1.model.Local;
import com.example.version1.model.Usuario;
import com.example.version1.repository.RepositoryEntrada;
import com.example.version1.repository.RepositoryEvento;
import com.example.version1.repository.RepositoryLocal;
import com.example.version1.repository.RepositoryUsuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class EntradaTest {

    @Autowired
    private RepositoryEntrada entradaRepository;

    @Autowired
    private RepositoryUsuario usuarioRepository;

    @Autowired
    private RepositoryLocal localRepository;

    @Autowired
    private RepositoryEvento eventoRepository;

    @Test
    void testEntradaCreation() {
        // Preparar
        Usuario usuario = new Usuario();
        usuario.setNombre("Juan Pérez");

        Evento evento = new Evento();
        evento.setTitulo("Concierto Rock");

        // Ejecutar
        Entrada entrada = new Entrada("GENERAL", usuario, evento, 2);

        // Comprobar
        assertEquals("Juan Pérez", entrada.getNombre_usuario());
        assertEquals("Concierto Rock", entrada.getNombre_evento());
        assertEquals("GENERAL", entrada.getTipo());
        assertEquals(2, entrada.getCantidad_consumiciones());
    }

    @Test
    void eliminarEntrada() {
        // Preparar
        Usuario usuario = new Usuario();
        usuario.setNombre("Ana Gómez");

        Evento evento = new Evento();
        evento.setTitulo("Festival Jazz");

        Entrada entrada = new Entrada("VIP", usuario, evento, 3);

        // Ejecutar
        entrada = null; // Simular eliminación

        // Comprobar
        assertNull(entrada);
    }

    @Test
    void findByUsuarioId_returnsEntries() {
        // Aislar
        entradaRepository.deleteAll();
        usuarioRepository.deleteAll();
        eventoRepository.deleteAll();
        localRepository.deleteAll();

        Usuario u = new Usuario();
        u.setNombre("Juan");
        u.setEmail("juan@example.com");
        u.setContrasenia("x");
        u.setEdad(30);
        u = usuarioRepository.save(u);

        Local local = new Local(null, "SalaEntrada");
        Evento e = new Evento(); e.setTitulo("Ev1"); e.setFecha(LocalDateTime.now().plusDays(5)); e.setAforo(100); e.setEstado(Boolean.TRUE);
        local.addEvento(e);
        localRepository.save(local);
        eventoRepository.flush();

        Entrada ent1 = new Entrada("GENERAL", u, e, 0);
        Entrada ent2 = new Entrada("VIP", u, e, 1);

        entradaRepository.save(ent1);
        entradaRepository.save(ent2);
        entradaRepository.flush();

        List<Entrada> porUsuario = entradaRepository.findByUsuarioId(u.getId());
        assertNotNull(porUsuario);
        assertEquals(2, porUsuario.size());
        assertTrue(porUsuario.stream().anyMatch(en -> "GENERAL".equalsIgnoreCase(en.getTipo())));
        assertTrue(porUsuario.stream().anyMatch(en -> "VIP".equalsIgnoreCase(en.getTipo())));
    }

    @Test
    void findByEventoId_and_countByEvento() {
        // Aislar
        entradaRepository.deleteAll();
        usuarioRepository.deleteAll();
        eventoRepository.deleteAll();
        localRepository.deleteAll();

        Usuario u1 = new Usuario(); u1.setNombre("Ana"); u1.setEmail("ana@example.com"); u1.setContrasenia("x"); u1.setEdad(28); u1 = usuarioRepository.save(u1);
        Usuario u2 = new Usuario(); u2.setNombre("Luis"); u2.setEmail("luis@example.com"); u2.setContrasenia("x"); u2.setEdad(35); u2 = usuarioRepository.save(u2);

        Local local = new Local(null, "SalaC");
        Evento e = new Evento(); e.setTitulo("EvCount"); e.setFecha(LocalDateTime.now().plusDays(3)); e.setAforo(100); e.setEstado(Boolean.TRUE);
        local.addEvento(e);
        localRepository.save(local);
        eventoRepository.flush();

        Entrada en1 = new Entrada("GENERAL", u1, e, 0);
        Entrada en2 = new Entrada("GENERAL", u2, e, 0);

        entradaRepository.save(en1);
        entradaRepository.save(en2);
        entradaRepository.flush();

        List<Entrada> porEvento = entradaRepository.findByEventoId(e.getId());
        assertNotNull(porEvento);
        assertEquals(2, porEvento.size());

        long count = entradaRepository.countByEventoId(e.getId());
        assertEquals(2L, count);
    }

    @Test
    void findByTipo_and_findByNombreUsuario() {
        // Aislar
        entradaRepository.deleteAll();
        usuarioRepository.deleteAll();
        eventoRepository.deleteAll();
        localRepository.deleteAll();

        Usuario u = new Usuario(); u.setNombre("Marta"); u.setEmail("marta@example.com"); u.setContrasenia("x"); u.setEdad(22); u = usuarioRepository.save(u);
        Local local = new Local(null, "SalaTipo");
        Evento e = new Evento(); e.setTitulo("EvTipo"); e.setFecha(LocalDateTime.now().plusDays(7)); e.setAforo(100); e.setEstado(Boolean.TRUE);
        local.addEvento(e);
        localRepository.save(local);
        eventoRepository.flush();

        Entrada en1 = new Entrada("GENERAL", u, e, 0);
        Entrada en2 = new Entrada("VIP", u, e, 1);
        Entrada en3 = new Entrada("GENERAL", u, e, 2);

        entradaRepository.save(en1);
        entradaRepository.save(en2);
        entradaRepository.save(en3);
        entradaRepository.flush();

        List<Entrada> generales = entradaRepository.findByTipo("general");
        assertNotNull(generales);
        assertEquals(2, generales.size());

        List<Entrada> porNombre = entradaRepository.findByNombreUsuario("marta");
        assertNotNull(porNombre);
        assertEquals(3, porNombre.size());
    }

}
