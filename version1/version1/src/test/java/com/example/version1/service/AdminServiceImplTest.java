package com.example.version1.service;

import com.example.version1.model.Evento;
import com.example.version1.model.Local;
import com.example.version1.model.Ubicacion;
import com.example.version1.repository.RepositoryEvento;
import com.example.version1.repository.RepositoryLocal;
import com.example.version1.repository.RepositoryUbicacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class AdminServiceImplTest {

    @Autowired
    private RepositoryUbicacion repositoryUbicacion;

    @Autowired
    private RepositoryLocal repositoryLocal;

    @Autowired
    private RepositoryEvento repositoryEvento;

    @Autowired
    private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        // La base de datos en memoria se limpia entre transacciones/autodiagnóstico
    }

    // Ubicacion tests
    @Test
    void validarUbicacion_null() {
        Optional<String> res = adminService.validarUbicacion(null);
        assertTrue(res.isPresent());
        assertEquals("Ubicación nula", res.get());
    }

    @Test
    void validarUbicacion_emptyName() {
        Ubicacion u = new Ubicacion();
        u.setNombre("   ");
        Optional<String> res = adminService.validarUbicacion(u);
        assertTrue(res.isPresent());
        assertEquals("El nombre de la ubicación no puede estar vacío", res.get());
    }

    @Test
    void validarUbicacion_shortName() {
        Ubicacion u = new Ubicacion();
        u.setNombre("ab");
        Optional<String> res = adminService.validarUbicacion(u);
        assertTrue(res.isPresent());
        assertEquals("El nombre debe tener al menos 3 caracteres", res.get());
    }

    @Test
    void validarUbicacion_exists() {
        // Guardar una ubicacion real en la BD y comprobar que la validacion detecta existencia
        Ubicacion existente = new Ubicacion();
        existente.setNombre("Madrid");
        repositoryUbicacion.save(existente);

        Ubicacion u = new Ubicacion();
        u.setNombre("Madrid");
        Optional<String> res = adminService.validarUbicacion(u);
        assertTrue(res.isPresent());
        assertEquals("Ya existe una ubicación con ese nombre", res.get());
    }

    @Test
    void crearUbicacion_shouldSave() {
        Ubicacion u = new Ubicacion();
        u.setNombre("Sevilla");

        Ubicacion result = adminService.crearUbicacion(u);
        assertNotNull(result);
        assertNotNull(result.getId());
        // comprobar que está en el repositorio
        Optional<Ubicacion> found = repositoryUbicacion.findById(result.getId());
        assertTrue(found.isPresent());
        assertEquals("Sevilla", found.get().getNombre());
    }

    // Local tests
    @Test
    void validarLocal_nonexistentUbicacion() {
        Local l = new Local();
        l.setNombre("MiLocal");
        Long ubicacionId = 99L; // no existe
        Optional<String> res = adminService.validarLocal(l, ubicacionId);
        assertTrue(res.isPresent());
        assertEquals("La ubicación seleccionada no existe", res.get());
    }

    @Test
    void validarLocal_duplicateName() {
        // Crear un local con nombre LocalX
        Local existing = new Local();
        existing.setNombre("LocalX");
        repositoryLocal.save(existing);

        Local l = new Local();
        l.setNombre("LocalX");
        Optional<String> res = adminService.validarLocal(l, null);
        assertTrue(res.isPresent());
        assertEquals("Ya existe un local con ese nombre", res.get());
    }

    @Test
    void crearLocal_setsUbicacionAndSaves() {
        Local l = new Local();
        l.setNombre("NuevoLocal");
        Ubicacion u = new Ubicacion();
        u.setNombre("Galicia");
        Ubicacion savedUb = repositoryUbicacion.save(u);

        Local result = adminService.crearLocal(l, savedUb.getId());
        assertNotNull(result);
        assertNotNull(result.getUbicacion());
        assertEquals(savedUb.getId(), result.getUbicacion().getId());

        Optional<Local> found = repositoryLocal.findById(result.getId());
        assertTrue(found.isPresent());
        assertEquals("NuevoLocal", found.get().getNombre());
        assertEquals(savedUb.getId(), found.get().getUbicacion().getId());
    }

    // Evento tests
    @Test
    void validarEvento_null() {
        Optional<String> res = adminService.validarEvento(null, null);
        assertTrue(res.isPresent());
        assertEquals("Evento nulo", res.get());
    }

    @Test
    void validarEvento_pastDate() {
        Evento e = new Evento();
        e.setTitulo("Concierto");
        e.setFecha(LocalDateTime.now().minusDays(1));
        e.setAforo(100);
        e.setEdadpermitida(0);
        Optional<String> res = adminService.validarEvento(e, null);
        assertTrue(res.isPresent());
        assertEquals("La fecha debe ser futura", res.get());
    }

    @Test
    void validarEvento_negativePricesAndAforo() {
        Evento e = new Evento();
        e.setTitulo("EventoOK");
        e.setFecha(LocalDateTime.now().plusDays(2));
        e.setAforo(-5);
        e.setEdadpermitida(0);
        e.setPrecioGeneral(-1f);
        Optional<String> res = adminService.validarEvento(e, null);
        assertTrue(res.isPresent());
        assertEquals("El aforo no puede ser negativo", res.get());
    }

    @Test
    void validarEvento_nonexistentLocal() {
        Evento e = new Evento();
        e.setTitulo("EventoLocal");
        e.setFecha(LocalDateTime.now().plusDays(2));
        e.setAforo(10);
        Long localId = 42L; // no existe
        Optional<String> res = adminService.validarEvento(e, localId);
        assertTrue(res.isPresent());
        assertEquals("El local seleccionado no existe", res.get());
    }

    @Test
    void crearEvento_setsLocalAndSaves() {
        Evento e = new Evento();
        e.setTitulo("Feria");
        e.setFecha(LocalDateTime.now().plusDays(10));
        Local local = new Local();
        local.setNombre("Sala7");
        Local savedLocal = repositoryLocal.save(local);

        Evento result = adminService.crearEvento(e, savedLocal.getId());
        assertNotNull(result);
        assertNotNull(result.getLocal());
        assertEquals(savedLocal.getId(), result.getLocal().getId());

        Optional<Evento> found = repositoryEvento.findById(result.getId());
        assertTrue(found.isPresent());
        assertEquals("Feria", found.get().getTitulo());
        assertEquals(savedLocal.getId(), found.get().getLocal().getId());
    }

}
