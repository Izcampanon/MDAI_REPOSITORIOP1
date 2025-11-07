package com.example.version1.clases;

import com.example.version1.model.Local;
import com.example.version1.model.Ubicacion;
import com.example.version1.model.Usuario;
import com.example.version1.model.Evento;
import com.example.version1.repository.RepositoryLocal;
import com.example.version1.repository.RepositoryUbicacion;
import com.example.version1.repository.RepositoryUsuario;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class LocalTest {

    @Autowired
    private RepositoryLocal localRepository;

    @Autowired
    private RepositoryUbicacion ubicacionRepository;

    @Autowired
    private RepositoryUsuario usuarioRepository;

    @Test
    void crearLocal_sePersisteYRelacionConUbicacion() {
        // Crear Ubicacion y Local, asociarlos mediante el helper para mantener ambas caras
        Ubicacion ubicacion = new Ubicacion(null, "Madrid");
        Local local = new Local(null, "Bar Central");
        ubicacion.addLocal(local);

        // Persistir la ubicacion (cascade persiste el Local)
        ubicacion = ubicacionRepository.save(ubicacion);

        // Verificaciones
        assertNotNull(ubicacion.getId(), "La ubicacion debe tener id tras persistir");
        assertNotNull(ubicacion.getLocales(), "La lista de locales no debe ser null");
        assertEquals(1, ubicacion.getLocales().size(), "Debe existir 1 local asociado a la ubicacion");

        Local savedLocal = ubicacion.getLocales().get(0);
        assertNotNull(savedLocal.getId(), "El local debe tener id tras persistir por cascade");
        assertEquals("Bar Central", savedLocal.getNombre(), "El nombre del local debe coincidir");

        // Recuperar mediante el repositorio de Local para confirmar persistencia independiente
        Local fetched = localRepository.findById(savedLocal.getId()).orElse(null);
        assertNotNull(fetched, "El local debe recuperarse desde RepositoryLocal");
        assertEquals("Madrid", fetched.getUbicacion().getNombre(), "La ubicacion asociada al local debe ser 'Madrid'");
    }

    @Test
    void eliminarLocal_seEliminaYActualizaUbicacion() {
        // 1. crear ubicacion con dos locales
        Ubicacion ubicacion = new Ubicacion(null, "Barcelona");
        Local local1 = new Local(null, "Sala A");
        Local local2 = new Local(null, "Sala B");
        ubicacion.addLocal(local1);
        ubicacion.addLocal(local2);

        // persistir ubicacion (cascade persiste locales)
        ubicacion = ubicacionRepository.save(ubicacion);

        // ids generados
        Long idLocal1 = local1.getId();
        Long idLocal2 = local2.getId();
        assertNotNull(idLocal1, "Local1 debe tener id tras persistir");
        assertNotNull(idLocal2, "Local2 debe tener id tras persistir");

        // sanity: ambos locales existen
        assertTrue(localRepository.findById(idLocal1).isPresent());
        assertTrue(localRepository.findById(idLocal2).isPresent());

        // 2. obtener el Local gestionado, quitarlo de la Ubicacion y eliminarlo
        Local managedLocal1 = localRepository.findById(idLocal1).orElseThrow();

        // quitar de la colección de Ubicacion para mantener sincronía en memoria
        ubicacion.removeLocal(managedLocal1);

        // borrar el local
        localRepository.delete(managedLocal1);
        localRepository.flush();
        ubicacionRepository.flush();

        // 3. comprobar que local1 ya no existe y local2 sí
        assertFalse(localRepository.findById(idLocal1).isPresent(), "El local eliminado no debe existir en el repositorio");
        assertTrue(localRepository.findById(idLocal2).isPresent(), "El otro local debe seguir existiendo");

        // 4. recuperar ubicacion y comprobar que la lista de locales se ha actualizado
        Ubicacion uRec = ubicacionRepository.findById(ubicacion.getId()).orElse(null);
        assertNotNull(uRec, "La ubicacion debe seguir existiendo");
        assertTrue(uRec.getLocales().stream().noneMatch(l -> l.getId() != null && l.getId().equals(idLocal1)), "La ubicacion no debe contener el local eliminado");
        assertTrue(uRec.getLocales().stream().anyMatch(l -> l.getId() != null && l.getId().equals(idLocal2)), "La ubicacion debe seguir conteniendo el local no eliminado");
    }

    @Test
    void comporbarEventosInicialesListaVacia() {
        Local local = new Local(null, "Teatro Principal");
        assertNotNull(local.getEventos(), "La lista de eventos no debe ser null al crear un Local");
        assertTrue(local.getEventos().isEmpty(), "La lista de eventos debe estar vacía al crear un Local");
    }


    @Test
    void mostrarEventosDisponibles() {
        // Crear usuario con edad suficiente (>=18)
        Usuario usuario = new Usuario();
        usuario.setEdad(25);
        usuario = usuarioRepository.save(usuario);

        // Crear local
        Local local = new Local(null, "Sala Ensayo");

        // Evento disponible: fecha futura, aforo > entradas (no entradas), edad OK
        Evento e1 = new Evento();
        e1.setTitulo("Disponible 1");
        e1.setFecha(java.time.LocalDateTime.now().plusDays(1));
        e1.setAforo(100);
        // Capturar el resultado de la comprobación y asignarlo al campo estado
        boolean dispo1 = e1.estadoDiponible(usuario);
        e1.setEstado(Boolean.valueOf(dispo1));

        // Evento no disponible por fecha pasada
        Evento e2 = new Evento();
        e2.setTitulo("No disponible - fecha pasada");
        e2.setFecha(java.time.LocalDateTime.now().minusDays(5));
        e2.setAforo(100);
        boolean dispo2 = e2.estadoDiponible(usuario);
        e2.setEstado(Boolean.valueOf(dispo2));

        // Evento no disponible por aforo completo (aunque la fecha sea futura)
        Evento e3 = new Evento();
        e3.setTitulo("No disponible - aforo lleno");
        e3.setFecha(java.time.LocalDateTime.now().plusDays(1));
        e3.setAforo(0); // aforo 0 => entradas.size() (0) >= aforo (0) -> no disponible
        boolean dispo3 = e3.estadoDiponible(usuario);
        e3.setEstado(Boolean.valueOf(dispo3));

        // Asociar al local
        local.addEvento(e1);
        local.addEvento(e2);
        local.addEvento(e3);

        // Persistir y forzar flush
        local = localRepository.save(local);
        localRepository.flush();

        // Recuperar y obtener eventos disponibles mediante el método del Local
        Local fetched = localRepository.findById(local.getId()).orElseThrow();
        var disponibles = fetched.getEventosDisponibles();

        // Comprobaciones: sólo e1 debe aparecer como disponible
        assertNotNull(disponibles, "La lista de eventos disponibles no debe ser null");
        assertEquals(1, disponibles.size(), "Debe haber exactamente 1 evento disponible");
        assertTrue(disponibles.stream().anyMatch(ev -> "Disponible 1".equals(ev.getTitulo())),
                "La lista debe contener 'Disponible 1'");
        // Asegurar que todos los devueltos tienen estado true
        assertTrue(disponibles.stream().allMatch(ev -> Boolean.TRUE.equals(ev.getEstado())),
                "Todos los eventos devueltos deben tener estado true (disponible)");
    }



    //Test donde el usuario seleccione un local muestre lista de eventos
    @Test
    void usuarioSeleccionaLocal_mostrarEventos() {
        // Crear local y eventos en memoria
        Local local = new Local(null, "Auditorio Central");
        com.example.version1.model.Evento e1 = new com.example.version1.model.Evento();
        com.example.version1.model.Evento e2 = new com.example.version1.model.Evento();

        // Asociar eventos al local (helper mantiene la relación bidireccional)
        local.addEvento(e1);
        local.addEvento(e2);

        // Persistir el local (CascadeType.ALL persiste los eventos)
        local = localRepository.save(local);
        localRepository.flush();

        Long localId = local.getId();
        assertNotNull(localId, "El local debe tener id tras persistir");

        // Recuperar el local como si lo seleccionara un usuario
        Local fetched = localRepository.findById(localId).orElseThrow();

        // Comprobaciones: la lista se muestra (no nula) y contiene los 2 eventos
        assertNotNull(fetched.getEventos(), "La lista de eventos no debe ser null");
        assertEquals(2, fetched.getEventos().size(), "Debe mostrar 2 eventos asociados al local");

        // Comprobar que cada evento referencia al local recuperado (comparando ids)
        fetched.getEventos().forEach(ev -> {
            assertNotNull(ev.getLocal(), "El evento debe tener referencia al local");
            assertNotNull(ev.getLocal().getId(), "El local referenciado en el evento debe tener id");
            assertEquals(localId, ev.getLocal().getId(), "El evento debe referenciar al local seleccionado");
        });
    }

    @Test
    void findByIdWithEventos_obtieneEventos() {
        // Aislar
        localRepository.deleteAll();
        ubicacionRepository.deleteAll();

        Local local = new Local(null, "LocalEventos");
        Evento e1 = new Evento();
        e1.setTitulo("E1");
        e1.setFecha(java.time.LocalDateTime.now().plusDays(1));
        e1.setAforo(100);
        e1.setEstado(Boolean.TRUE);
        Evento e2 = new Evento();
        e2.setTitulo("E2");
        e2.setFecha(java.time.LocalDateTime.now().plusDays(2));
        e2.setAforo(100);
        e2.setEstado(Boolean.TRUE);

        local.addEvento(e1);
        local.addEvento(e2);

        local = localRepository.save(local);
        localRepository.flush();

        Local fetched = localRepository.findByIdConEventos(local.getId());
        assertNotNull(fetched);
        assertNotNull(fetched.getEventos());
        assertEquals(2, fetched.getEventos().size(), "Debe recuperar los 2 eventos asociados al local mediante fetch");
    }

    @Test
    void findByNombreConEventosDisponibles_devolverDisponibles() {
        // Aislar estado en BD
        localRepository.deleteAll();
        ubicacionRepository.deleteAll();

        // Crear local con 3 eventos: A1 (true), A2 (false), A3 (true)
        Local local = new Local(null, "AuditorioX");
        Evento a1 = new Evento(); a1.setTitulo("A1"); a1.setFecha(java.time.LocalDateTime.now().plusDays(1)); a1.setAforo(100); a1.setEstado(Boolean.TRUE);
        Evento a2 = new Evento(); a2.setTitulo("A2"); a2.setFecha(java.time.LocalDateTime.now().plusDays(1)); a2.setAforo(100); a2.setEstado(Boolean.FALSE);
        Evento a3 = new Evento(); a3.setTitulo("A3"); a3.setFecha(java.time.LocalDateTime.now().plusDays(1)); a3.setAforo(100); a3.setEstado(Boolean.TRUE);

        local.addEvento(a1);
        local.addEvento(a2);
        local.addEvento(a3);

        // Persistir y forzar flush para que los ids se generen
        localRepository.saveAndFlush(local);

        // Ejecutar el método del repositorio que debe devolver el Local con solo eventos disponibles
        var opt = localRepository.findByNombreConEventosDisponibles("auditoriox");
        assertTrue(opt.isPresent(), "Debe recuperar el local cuando existen eventos disponibles");
        Local fetched = opt.get();

        // La colección fetch debería contener únicamente eventos con estado = true
        assertNotNull(fetched.getEventos(), "La colección de eventos no debe ser null");
        assertTrue(fetched.getEventos().stream().allMatch(ev -> Boolean.TRUE.equals(ev.getEstado())), "Todos los eventos deben tener estado true");
        assertEquals(2, fetched.getEventos().size(), "Deben devolverse solo los 2 eventos disponibles");
    }

}
