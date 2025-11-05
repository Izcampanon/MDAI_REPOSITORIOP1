// UsuarioTest.java
package com.example.version1.clases;

import com.example.version1.model.*;
import com.example.version1.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UsuarioTest {

    @Autowired
    private RepositoryUsuario usuarioRepository;

    @Autowired
    private RepositoryCompra_Entrada compraEntradaRepository;

    @Autowired
    private RepositoryEntrada entradaRepository;

    @Autowired
    private RepositoryEvento eventoRepository;

    @Autowired
    private RepositoryLocal localRepository;

    @Autowired
    private RepositoryUbicacion ubicacionRepository;

    //test que cree usuario y verifique que se haya guardado correctamente
    @Test
    void testCrearUsuario() {
        Usuario u = new Usuario("Juan", "juan@test.com", "pwd", 30, "600000000");
        Usuario saved = usuarioRepository.save(u);
        assertNotNull(saved.getId(), "El usuario debe tener id tras persistir");

        Usuario fetched = usuarioRepository.findById(saved.getId()).orElse(null);
        assertNotNull(fetched, "Debe poder recuperarse el usuario guardado");
        assertEquals("Juan", fetched.getNombre());
        assertEquals(30, fetched.getEdad());
    }

    //test que elimine un usuario y verifique que se haya eliminado correctamente
    @Test
    void testEliminarUsuario() {
        // crear usuario
        Usuario u = new Usuario("Ana", "ana@test.com", "pwd", 28, "611111111");
        u = usuarioRepository.save(u);
        assertNotNull(u.getId(), "El usuario debe tener id tras persistir");

        // crear evento
        Evento evento = new Evento("Concierto", LocalDateTime.now().plusDays(10), "Banda", "Ciudad", "Desc", 300, Boolean.TRUE, 0, null);
        evento = eventoRepository.save(evento);

        // crear entradas y compra asociada al usuario
        Entrada e1 = new Entrada("GENERAL", u, evento, 0);
        Entrada e2 = new Entrada("VIP", u, evento, 1);

        Compra_Entrada compra = new Compra_Entrada(new Date(), 59.99f, new ArrayList<>(Arrays.asList(e1, e2)), u);
        compra = compraEntradaRepository.save(compra);

        // Recuperar la compra persistida para obtener id/entradas reales
        Compra_Entrada compraGuardada = compraEntradaRepository.findById(compra.getId()).orElseThrow();
        assertNotNull(compraGuardada.getId(), "La compra debe tener id tras persistir");

        // comprobar que hay al menos 2 entradas persistidas
        assertTrue(entradaRepository.findAll().size() >= 2);

        // eliminar usuario usando el id recuperado de la entidad gestionada
        Long usuarioId = u.getId();
        // Primero eliminar la compra asociada para evitar referencias a usuario en la BD
        compraEntradaRepository.deleteById(compraGuardada.getId());
        compraEntradaRepository.flush();

        usuarioRepository.deleteById(usuarioId);
        usuarioRepository.flush();

        assertFalse(usuarioRepository.findById(usuarioId).isPresent(), "El usuario debe haber sido eliminado");

        // Las compras deberían haberse eliminado por cascade (si no, al menos no referenciarán al usuario)
        boolean anyCompraForUser = compraEntradaRepository.findAll().stream().anyMatch(c -> c.getUsuario() != null && c.getUsuario().getId() != null && c.getUsuario().getId().equals(usuarioId));
        assertFalse(anyCompraForUser, "No debe existir compra asociada al usuario eliminado");

        // Las entradas asociadas no deben existir o no deben estar asociadas al usuario eliminado
        boolean anyEntradaForUser = entradaRepository.findAll().stream().anyMatch(en -> en.getUsuario() != null && en.getUsuario().getId() != null && en.getUsuario().getId().equals(usuarioId));
        assertFalse(anyEntradaForUser, "No debe existir entrada asociada al usuario eliminado");
    }

    //test que compruebe que el nombre de ubicacion introducido por el usuario corresponde a algun objeto ubicacion en la base de datos
    @Test
    void testVerificarUbicacionUsuario() {
        Ubicacion ub = new Ubicacion(null, "Granada");
        ub = ubicacionRepository.save(ub);

        // Simular entrada de nombre por parte del usuario
        String nombreBuscado = "Granada";
        boolean existe = ubicacionRepository.findAll().stream().anyMatch(u -> u.getNombre().equalsIgnoreCase(nombreBuscado));
        assertTrue(existe, "La ubicacion 'Granada' debe existir en el repositorio");

        // Comprobar negativo
        String otro = "Córdoba";
        boolean existe2 = ubicacionRepository.findAll().stream().anyMatch(u -> u.getNombre().equalsIgnoreCase(otro));
        assertFalse(existe2, "La ubicacion 'Córdoba' no debe existir");
    }

    //test que compruebe que un usuario peude ver y elegir entre una lista de eventos disponibles
    @Test
    void testVerEventosDisponibles() {
        Usuario usuario = new Usuario("Paco", "paco@test.com", "pwd", 25, "622222222");
        usuario = usuarioRepository.save(usuario);

        Local local = new Local(null, "Sala X");

        Evento e1 = new Evento();
        e1.setTitulo("Evento Futuro Disponible");
        e1.setFecha(LocalDateTime.now().plusDays(2));
        e1.setAforo(100);
        e1.estadoDiponible(usuario); // establece estado

        Evento e2 = new Evento();
        e2.setTitulo("Evento Pasado No Disponible");
        e2.setFecha(LocalDateTime.now().minusDays(2));
        e2.setAforo(100);
        e2.estadoDiponible(usuario);

        local.addEvento(e1);
        local.addEvento(e2);

        local = localRepository.save(local);
        localRepository.flush();

        Local fetched = localRepository.findById(local.getId()).orElseThrow();
        List<Evento> disponibles = fetched.getEventosDisponibles();

        assertNotNull(disponibles);
        assertTrue(disponibles.stream().anyMatch(ev -> "Evento Futuro Disponible".equals(ev.getTitulo())));
        assertFalse(disponibles.stream().anyMatch(ev -> "Evento Pasado No Disponible".equals(ev.getTitulo())));
    }

    //test que compruebe que un usuario peude ver y elegir entre una lista de locales disponibles
    @Test
    void testVerLocalesDisponibles() {
        Ubicacion ub = new Ubicacion(null, "Bilbao");
        Local l1 = new Local(null, "Local A");
        Local l2 = new Local(null, "Local B");
        ub.addLocal(l1);
        ub.addLocal(l2);
        ub = ubicacionRepository.save(ub);

        Usuario u = new Usuario("Marta", "marta@test.com", "pwd", 29, "633333333");
        u.setUbicacion(ub);
        u = usuarioRepository.save(u);

        Usuario fetched = usuarioRepository.findById(u.getId()).orElseThrow();
        assertNotNull(fetched.getUbicacion());
        assertEquals(2, fetched.getUbicacion().getLocales().size());
    }

    //test que comprueube ue el usuario puede ver detalles de un evento seleccionado
    @Test
    void testVerDetallesEvento() {
        Evento ev = new Evento("Show", LocalDateTime.now().plusDays(3), "Artista", "Calle Falsa", "Descripción larga", 50, Boolean.TRUE, 0, null);
        ev = eventoRepository.save(ev);

        Evento fetched = eventoRepository.findById(ev.getId()).orElseThrow();
        assertEquals("Show", fetched.getTitulo());
        assertEquals("Artista", fetched.getArtista());
        assertEquals("Descripción larga", fetched.getDescripcion());
    }


    //test que compruebe que el usuario puede ver sus entradas compradas
    @Test
    void testVerEntradasCompradas() {
        Usuario u = new Usuario("Luis", "luis@test.com", "pwd", 35, "644444444");
        u = usuarioRepository.save(u);
        assertNotNull(u.getId(), "Usuario debe tener id");

        Evento evento = new Evento("Concierto", LocalDateTime.now().plusDays(6), "Banda", "Ciudad", "Desc", 200, Boolean.TRUE, 0, null);
        evento = eventoRepository.save(evento);

        Entrada ent1 = new Entrada("GENERAL", u, evento, 0);
        Entrada ent2 = new Entrada("VIP", u, evento, 1);

        Compra_Entrada compra = new Compra_Entrada(new Date(), 40.0f, new ArrayList<>(Arrays.asList(ent1, ent2)), u);
        compra = compraEntradaRepository.save(compra);

        // Recuperar la compra persistida para asegurar que las entradas tienen id asignado
        Compra_Entrada compraGuardada = compraEntradaRepository.findById(compra.getId()).orElseThrow();
        assertNotNull(compraGuardada.getId(), "La compra debe existir en la BD");
        assertNotNull(compraGuardada.getTipo_entradas());
        assertEquals(2, compraGuardada.getTipo_entradas().size());

        Usuario fetched = usuarioRepository.findById(u.getId()).orElseThrow();
        // Verificar relación inversa: la compra recuperada referencia al usuario
        assertEquals(u.getId(), compraGuardada.getUsuario().getId());
        // Opcional: comprobar que el repositorio de compras tiene la compra
        assertTrue(compraEntradaRepository.findById(compraGuardada.getId()).isPresent());
    }

    //test que compruebe que el usuario puede cancelar una entrada comprada
    @Test
    void testCancelarEntradaComprada() {
        Usuario u = new Usuario("Sara", "sara@test.com", "pwd", 27, "655555555");
        u = usuarioRepository.save(u);
        assertNotNull(u.getId(), "Usuario debe tener id");

        Evento evento = new Evento("Evento", LocalDateTime.now().plusDays(8), "Grupo", "Sitio", "Desc", 100, Boolean.TRUE, 0, null);
        evento = eventoRepository.save(evento);

        Entrada e1 = new Entrada("GENERAL", u, evento, 0);
        Entrada e2 = new Entrada("GENERAL", u, evento, 0);

        Compra_Entrada compra = new Compra_Entrada(new Date(), 20.0f, new ArrayList<>(Arrays.asList(e1, e2)), u);
        compra = compraEntradaRepository.save(compra);

        // Recuperar la compra guardada para obtener los ids reales de las entradas persistidas
        Compra_Entrada compraGuardada = compraEntradaRepository.findById(compra.getId()).orElseThrow();
        assertNotNull(compraGuardada.getTipo_entradas());
        assertFalse(compraGuardada.getTipo_entradas().isEmpty());

        Long idEntradaAEliminar = compraGuardada.getTipo_entradas().get(0).getId();
        assertNotNull(idEntradaAEliminar, "La entrada debe tener id antes de eliminarla");

        // Cancelar (borrar) la entrada usando el id persistido
        // En la aplicación lo razonable es quitar la entrada de la compra y guardar la compra
        Compra_Entrada compraBefore = compraEntradaRepository.findById(compra.getId()).orElseThrow();
        compraBefore.getTipo_entradas().removeIf(en -> en.getId() != null && en.getId().equals(idEntradaAEliminar));
        compraEntradaRepository.save(compraBefore);
        compraEntradaRepository.flush();

        // Recuperar compra y comprobar que la entrada cancelada ya no esté presente
        Compra_Entrada fetchedCompra = compraEntradaRepository.findById(compra.getId()).orElseThrow();
        boolean contiene = fetchedCompra.getTipo_entradas().stream().anyMatch(en -> en.getId() != null && en.getId().equals(idEntradaAEliminar));
        assertFalse(contiene, "La entrada cancelada no debe aparecer en la compra");
    }


}
